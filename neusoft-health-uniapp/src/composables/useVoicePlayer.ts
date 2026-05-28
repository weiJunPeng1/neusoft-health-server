import { ref } from 'vue'
import { consultApi } from '@/api/consult'
import { useSettingsStore } from '@/stores/settings'

const isSpeaking = ref(false)
let isProcessing = false
let audioContext: any = null
let h5Audio: HTMLAudioElement | null = null

const audioCache = new Map<string, string>()
const pendingRequests = new Map<string, Promise<string | null>>()

function cleanText(text: string): string {
  return text
    .replace(/\*\*(.+?)\*\*/g, '$1')
    .replace(/[#*`\-]/g, '')
    .replace(/\n+/g, '。')
    .replace(/\s+/g, ' ')
    .trim()
}

function getTextHash(text: string): string {
  let hash = 0
  for (let i = 0; i < text.length; i++) {
    const char = text.charCodeAt(i)
    hash = ((hash << 5) - hash) + char
    hash = hash & hash
  }
  return hash.toString(36)
}

function base64ToUint8Array(base64: string): Uint8Array {
  const binaryStr = atob(base64)
  const bytes = new Uint8Array(binaryStr.length)
  for (let i = 0; i < binaryStr.length; i++) {
    bytes[i] = binaryStr.charCodeAt(i)
  }
  return bytes
}

function stop() {
  // #ifdef H5
  if (h5Audio) {
    h5Audio.pause()
    h5Audio.src = ''
    h5Audio = null
  }
  // #endif

  // #ifdef MP-WEIXIN
  if (audioContext) {
    audioContext.stop()
    audioContext.destroy()
    audioContext = null
  }
  // #endif

  isSpeaking.value = false
}

function playOnH5(base64: string) {
  stop()

  const audioSrc = `data:audio/mpeg;base64,${base64}`
  h5Audio = new Audio(audioSrc)

  h5Audio.onplay = () => { isSpeaking.value = true }
  h5Audio.onended = () => {
    isSpeaking.value = false
    h5Audio = null
  }
  h5Audio.onerror = () => {
    console.error('H5语音播报播放失败')
    isSpeaking.value = false
    h5Audio = null
  }

  h5Audio.play().catch(err => {
    console.error('H5播放失败', err)
    isSpeaking.value = false
  })
}

function playOnMp(base64: string) {
  stop()

  const buffer = uni.base64ToArrayBuffer(base64)
  const fs = uni.getFileSystemManager()
  const filePath = `${uni.env.USER_DATA_PATH}/tts_${Date.now()}.mp3`

  fs.writeFile({
    filePath,
    data: buffer,
    encoding: 'binary',
    success: () => {
      audioContext = uni.createInnerAudioContext()
      audioContext.src = filePath
      audioContext.onPlay(() => { isSpeaking.value = true })
      audioContext.onEnded(() => {
        isSpeaking.value = false
        audioContext?.destroy()
        audioContext = null
        fs.unlink({ filePath, fail: () => {} })
      })
      audioContext.onError((err: any) => {
        console.error('小程序语音播报播放失败', err)
        isSpeaking.value = false
        audioContext?.destroy()
        audioContext = null
        fs.unlink({ filePath, fail: () => {} })
      })
      audioContext.play()
    },
    fail: (err: any) => {
      console.error('写入音频文件失败', err)
    }
  })
}

function playBase64(base64: string) {
  // #ifdef H5
  playOnH5(base64)
  // #endif

  // #ifdef MP-WEIXIN
  playOnMp(base64)
  // #endif
}

async function fetchAudio(text: string): Promise<string | null> {
  try {
    const res = await consultApi.synthesize(text)
    return res.data?.audio || null
  } catch (err) {
    console.error('语音合成失败', err)
    return null
  }
}

async function getAudio(text: string): Promise<string | null> {
  const key = getTextHash(text)

  if (audioCache.has(key)) {
    return audioCache.get(key)!
  }

  if (pendingRequests.has(key)) {
    return pendingRequests.get(key)!
  }

  const promise = fetchAudio(text).then(audio => {
    if (audio) {
      audioCache.set(key, audio)
    }
    pendingRequests.delete(key)
    return audio
  })

  pendingRequests.set(key, promise)
  return promise
}

// #ifdef H5
async function streamAndPlay(text: string): Promise<void> {
  stop()
  isSpeaking.value = true

  try {
    const stream = await consultApi.synthesizeSse(text)
    if (!stream) {
      const audio = await getAudio(text)
      if (audio) playBase64(audio)
      return
    }

    const reader = stream.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    const chunks: Uint8Array[] = []

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      let currentEvent = ''
      let currentData = ''

      for (const line of lines) {
        if (line.startsWith('event')) {
          currentEvent = line.substring(line.indexOf(':') + 1).trim()
        } else if (line.startsWith('data')) {
          currentData = line.substring(line.indexOf(':') + 1).trim()
        } else if (line === '' && currentEvent && currentData) {
          if (currentEvent === 'audio' && currentData !== 'complete') {
            try {
              chunks.push(base64ToUint8Array(currentData))
            } catch (e) {
              // skip malformed chunk
            }
          } else if (currentEvent === 'done' && chunks.length > 0) {
            const merged = mergeChunks(chunks)
            const blob = new Blob([merged], { type: 'audio/mpeg' })
            const url = URL.createObjectURL(blob)
            h5Audio = new Audio(url)
            h5Audio.onended = () => {
              isSpeaking.value = false
              URL.revokeObjectURL(url)
              h5Audio = null
            }
            h5Audio.onerror = () => {
              console.error('H5流式音频播放失败')
              isSpeaking.value = false
              URL.revokeObjectURL(url)
              h5Audio = null
            }
            h5Audio.play().catch(err => {
              console.error('H5播放失败', err)
              isSpeaking.value = false
            })
          }
          currentEvent = ''
          currentData = ''
        }
      }
    }
  } catch (err) {
    console.error('SSE流式TTS失败，回退到非流式', err)
    const audio = await getAudio(text)
    if (audio) playBase64(audio)
  }
}

function mergeChunks(chunks: Uint8Array[]): Uint8Array {
  let totalLen = 0
  for (const c of chunks) totalLen += c.length
  const merged = new Uint8Array(totalLen)
  let offset = 0
  for (const c of chunks) {
    merged.set(c, offset)
    offset += c.length
  }
  return merged
}

function uint8ArrayToBase64(bytes: Uint8Array): string {
  let binaryStr = ''
  for (let i = 0; i < bytes.length; i++) {
    binaryStr += String.fromCharCode(bytes[i])
  }
  return btoa(binaryStr)
}
// #endif

export function useVoicePlayer() {
  const preload = async (text: string) => {
    if (!useSettingsStore.voiceEnabled || !text) return

    const cleaned = cleanText(text)
    if (!cleaned) return

    await getAudio(cleaned)
  }

  const speak = async (text: string) => {
    if (!useSettingsStore.voiceEnabled || !text) return
    if (isProcessing) return
    if (isSpeaking.value) {
      stop()
      return
    }

    isProcessing = true
    const cleaned = cleanText(text)
    if (!cleaned) {
      isProcessing = false
      return
    }

    try {
      // #ifdef H5
      await streamAndPlay(cleaned)
      return
      // #endif

      // #ifndef H5
      const audio = await getAudio(cleaned)
      if (audio) {
        playBase64(audio)
      }
      // #endif
    } finally {
      isProcessing = false
    }
  }

  const autoSpeak = async (text: string) => {
    if (!useSettingsStore.voiceEnabled || !text) return
    if (isProcessing) return

    const cleaned = cleanText(text)
    if (!cleaned) return

    if (isSpeaking.value) {
      stop()
    }

    isProcessing = true

    try {
      // #ifdef H5
      await streamAndPlay(cleaned)
      return
      // #endif

      // #ifndef H5
      const audio = await getAudio(cleaned)
      if (audio) {
        playBase64(audio)
      }
      // #endif
    } finally {
      isProcessing = false
    }
  }

  return {
    isSpeaking,
    preload,
    speak,
    autoSpeak,
    stop
  }
}
