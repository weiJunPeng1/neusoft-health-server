<template>
  <view class="page">
    <NavHeader title="智能问诊" showBack @back="goBack" handleBackBySelf="true">
      <template #right>
        <view class="history-btn" @click="goToHistory">
          <SvgIcon name="scroll" :size="20" color="#4A90D9" />
        </view>
      </template>
    </NavHeader>

    <!-- 紧急横幅 -->
    <view class="emergency-banner" @click="showEmergency">
      <view class="emer-icon"><SvgIcon name="warning" :size="20" color="#FFFFFF" /></view>
      <view class="emer-text">
        <text class="emer-title">紧急情况？请立即拨打120</text>
      </view>
      <text class="emer-arrow">›</text>
    </view>

    <!-- 消息列表 -->
    <scroll-view scroll-y class="msg-area" :scroll-top="scrollTop" @click="skipTypingEffect">
      <view v-for="(msg, i) in messages" :key="i" :class="['msg-row', msg.role === 'user' ? 'msg-user' : 'msg-ai']">
        <view v-if="msg.role === 'assistant'" class="msg-avatar"><SvgIcon name="robot" :size="20" color="#4A90D9" /></view>
        <view :class="['msg-bubble', msg.role === 'user' ? 'bubble-user' : 'bubble-ai']">
          <view 
            class="msg-text" 
            v-html="formatMessage(typingState.active && typingState.index === i ? typingState.displayed : msg.content)"
          ></view>
          <view v-if="msg.role === 'assistant' && msg.id" class="msg-actions">
            <view 
              class="msg-action-btn" 
              :class="{ 'speaking': isSpeaking }"
              @click.stop="speak(msg.content)"
            >
              <SvgIcon name="volume" :size="16" :color="isSpeaking ? '#4A90D9' : '#BBBFC4'" />
            </view>
            <view 
              class="msg-action-btn" 
              :class="{ 'favorited': favoritedIds.includes(msg.id!) }"
              @click.stop="toggleFavorite(msg.id!)"
            >
              <SvgIcon :name="favoritedIds.includes(msg.id!) ? 'heart' : 'heart-outline'" :size="16" :color="favoritedIds.includes(msg.id!) ? '#FF4757' : '#BBBFC4'" />
            </view>
          </view>
        </view>
        <view v-if="msg.role === 'user'" class="msg-avatar"><SvgIcon name="user" :size="20" color="#4A90D9" /></view>
      </view>

      <!-- AI 加载动画 -->
      <view v-if="isLoading" class="msg-row msg-ai">
        <view class="msg-avatar"><SvgIcon name="robot" :size="20" color="#4A90D9" /></view>
        <view class="msg-bubble bubble-ai">
          <view class="typing-indicator">
            <view class="typing-dot"></view>
            <view class="typing-dot"></view>
            <view class="typing-dot"></view>
            <text class="typing-text">AI回复中</text>
          </view>
        </view>
      </view>

      <!-- 快捷问题（仅初始状态） -->
      <view v-if="messages.length === 1" class="quick-qs">
        <text class="quick-label">快捷问题</text>
        <view class="quick-tags">
          <text v-for="q in quickQuestions" :key="q" class="quick-tag" @click="sendMessage(q)">{{ q }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 输入栏 -->
    <view class="input-bar">
      <input
        v-model="inputText"
        class="msg-input"
        placeholder="输入您的健康问题..."
        :adjust-position="false"
        confirm-type="send"
        @confirm="sendMessage(inputText)"
      />
      <view class="send-btn" :class="{ 'send-btn-disabled': isLoading }" @click="!isLoading && sendMessage(inputText)">
        <text class="send-icon">↑</text>
      </view>
    </view>

    <!-- 紧急弹窗 -->
    <Modal :visible="showEmerModal" @close="showEmerModal = false">
      <view class="modal-inner">
        <view class="emer-modal-icon"><SvgIcon name="alert" :size="48" color="#FF4757" /></view>
        <text class="emer-modal-title">紧急医疗情况</text>
        <text class="emer-modal-body">如果您正在经历胸痛、呼吸困难、严重创伤等紧急情况，请立即拨打120急救电话。AI健康助手不能替代紧急医疗救助。</text>
        <button class="btn-primary" @click="showEmerModal = false">我知道了</button>
      </view>
    </Modal>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Modal from '@/components/Modal/Modal.vue'
import { ref, reactive, nextTick, onMounted, onUnmounted } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { consultApi } from '@/api/consult'
import { memberApi } from '@/api/member'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { useSettingsStore } from '@/stores/settings'
import { useVoicePlayer } from '@/composables/useVoicePlayer'
import type { ConsultMessage, MemberStatus } from '@/types'

const { isSpeaking, preload, speak, autoSpeak, stop: stopVoice } = useVoicePlayer()

const inputText = ref('')
const scrollTop = ref(0)
const showEmerModal = ref(false)
const currentSessionId = ref<number | null>(null)
const fromHistory = ref(false)
const isLoading = ref(false)

const messages = reactive<ConsultMessage[]>([])
const favoritedIds = ref<number[]>([])
const healthProfile = ref<any>(null)
const autoSyncHealthProfile = ref(true)

const typingState = reactive({
  active: false,
  index: -1,
  content: '',
  displayed: ''
})

const memberStatus = reactive<MemberStatus>({
  levelCode: '',
  levelName: '',
  dailyQuota: 3,
  todayUsed: 0,
  contextRounds: 0,
  autoSync: false,
  deepAnalysis: false,
  exportEnabled: false,
  expireTime: '',
  remainingDays: 0,
  inGracePeriod: false
})

const quickQuestions = [
  '感冒发烧怎么办',
  '头痛是什么原因',
  '如何改善睡眠',
  '血压高怎么办',
  '皮肤过敏处理',
]

const formatMessage = (content: string): string => {
  if (!content) return ''
  return content
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br/>')
}

let typingTimer: ReturnType<typeof setInterval> | null = null

const startTypingEffect = (index: number, content: string, onComplete?: () => void) => {
  if (typingTimer) {
    clearInterval(typingTimer)
  }

  typingState.active = true
  typingState.index = index
  typingState.content = content
  typingState.displayed = ''

  let charIndex = 0
  typingTimer = setInterval(() => {
    if (charIndex < content.length) {
      typingState.displayed += content[charIndex]
      charIndex++
      nextTick(() => scrollToBottom())
    } else {
      clearInterval(typingTimer!)
      typingTimer = null
      typingState.active = false
      messages[index].content = content
      onComplete?.()
    }
  }, 30)
}

const skipTypingEffect = () => {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  if (typingState.active && typingState.index >= 0) {
    const content = typingState.content
    messages[typingState.index].content = content
    typingState.active = false
    autoSpeak(content)
  }
}

const loadSessionMessages = async (sessionId: number) => {
  try {
    const res = await consultApi.listMessages(sessionId)
    messages.splice(0, messages.length)
    messages.push(...(res.data || []))
    nextTick(() => scrollToBottom())

    // 预合成最后一条AI消息的语音
    const lastAiMsg = [...messages].reverse().find(m => m.role === 'assistant')
    if (lastAiMsg?.content) {
      preload(lastAiMsg.content)
    }
  } catch (err) {
    console.error('加载会话消息失败', err)
  }
}

const checkQuota = async (): Promise<boolean> => {
  try {
    const res = await memberApi.getStatus()
    Object.assign(memberStatus, res.data)
    
    if (!memberStatus.levelCode || memberStatus.levelCode === 'L0') {
      if (memberStatus.todayUsed >= memberStatus.dailyQuota) {
        uni.showModal({
          title: '额度用完',
          content: `今日免费咨询额度已用完（今日已使用${memberStatus.todayUsed}次），开通会员享受更多咨询次数。`,
          confirmText: '去开通',
          cancelText: '知道了',
          success: (res) => {
            if (res.confirm) {
              uni.switchTab({ url: '/pages/member/index' })
            }
          }
        })
        return false
      }
    }
    return true
  } catch (err) {
    console.error('检查会员状态失败', err)
    return true
  }
}

const sendMessage = async (text: string) => {
  const msg = text.trim()
  if (!msg || isLoading.value) return
  isLoading.value = true

  const canSend = await checkQuota()
  if (!canSend) {
    isLoading.value = false
    return
  }

  if (!currentSessionId.value) {
    try {
      const res = await consultApi.createSession(msg)
      currentSessionId.value = res.data.id
    } catch (err) {
      uni.showToast({ title: '创建会话失败', icon: 'none' })
      return
    }
  }

  messages.push({ id: 0, sessionId: currentSessionId.value, role: 'user', content: msg, createdTime: new Date().toISOString() })
  inputText.value = ''
  isLoading.value = true
  nextTick(() => scrollToBottom())

  try {
    const profileData = autoSyncHealthProfile.value ? healthProfile.value : undefined
    const res = await consultApi.sendMessage(currentSessionId.value, msg, profileData)
    memberStatus.todayUsed++

    const aiMsg: ConsultMessage = { ...res.data, content: '' }
    messages.push(aiMsg)
    const msgIndex = messages.length - 1

    isLoading.value = false
    preload(res.data.content)
    startTypingEffect(msgIndex, res.data.content, () => {
      autoSpeak(res.data.content)
    })
  } catch (err: any) {
    isLoading.value = false
    messages.push({ id: 0, sessionId: currentSessionId.value, role: 'assistant', content: err.message || '发送失败，请重试', createdTime: new Date().toISOString() })
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  scrollTop.value = 99999
}

const showEmergency = () => {
  showEmerModal.value = true
}

const goBack = () => {
  if (fromHistory.value) {
    uni.switchTab({ url: '/pages/index/index' })
  } else {
    const pages = getCurrentPages()
    if (pages.length <= 1) {
      uni.switchTab({ url: '/pages/index/index' })
    } else {
      uni.navigateBack()
    }
  }
}

const goToHistory = () => {
  uni.navigateTo({ url: '/pages/consult-history/index' })
}

const toggleFavorite = async (messageId: number) => {
  try {
    const isFavorited = await userApi.toggleFavorite(messageId)
    const index = favoritedIds.value.indexOf(messageId)
    if (isFavorited) {
      if (index === -1) favoritedIds.value.push(messageId)
      uni.showToast({ title: '已收藏', icon: 'success' })
    } else {
      if (index !== -1) favoritedIds.value.splice(index, 1)
      uni.showToast({ title: '已取消收藏', icon: 'none' })
    }
  } catch (err: any) {
    uni.showToast({ title: err.message || '操作失败', icon: 'none' })
  }
}

const loadFavoritedIds = async () => {
  try {
    const res = await userApi.getFavorites()
    favoritedIds.value = (res.data || []).map((f: any) => f.messageId).filter((id: number) => id > 0)
  } catch (err) {
    console.error('加载收藏列表失败', err)
  }
}

const loadHealthProfile = async () => {
  try {
    const res = await userApi.getHealthProfile()
    healthProfile.value = res.data || null
  } catch (err) {
    console.error('加载健康档案失败', err)
  }
}

const loadSettings = async () => {
  try {
    await useSettingsStore.load()
    autoSyncHealthProfile.value = useSettingsStore.settings.autoSyncHealthProfile ?? true
  } catch (err) {
    console.error('加载设置失败', err)
  }
}

onLoad((options: any) => {
  if (options?.sessionId) {
    currentSessionId.value = parseInt(options.sessionId)
    fromHistory.value = true
  }
})

onMounted(async () => {
  if (!useUserStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  
  await Promise.all([
    loadFavoritedIds(),
    loadHealthProfile(),
    loadSettings()
  ])
  
  if (currentSessionId.value) {
    await loadSessionMessages(currentSessionId.value)
  } else {
    messages.push({ id: 0, sessionId: 0, role: 'assistant', content: '您好！我是智能问诊助手，请问您有什么症状或健康问题需要咨询？我将为您提供专业的医疗建议。', createdTime: new Date().toISOString() })
  }
})

onShow(async () => {
  if (currentSessionId.value) {
    await loadSessionMessages(currentSessionId.value)
  }
})

onUnmounted(() => {
  stopVoice()
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
})
</script>

<style scoped>
.page { height: 100vh; display: flex; flex-direction: column; background: #F5F7FA; }

.emergency-banner {
  background: linear-gradient(135deg, #FF6B6B, #FF4757);
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.emer-icon { font-size: 20px; }
.emer-text { flex: 1; }
.emer-title { font-size: 13px; color: #FFFFFF; font-weight: 500; }
.emer-arrow { font-size: 18px; color: rgba(255,255,255,0.7); }

.msg-area {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.msg-row {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
  align-items: flex-start;
}
.msg-user { justify-content: flex-end; }
.msg-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.msg-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 16px;
  line-height: 1.5;
}
.bubble-ai { background: #FFFFFF; border-radius: 0 16px 16px 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.bubble-user { background: #4A90D9; border-radius: 16px 0 16px 16px; }
.msg-text { font-size: 14px; color: #1F2329; }
.bubble-user .msg-text { color: #FFFFFF; }

.msg-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
  gap: 8px;
}
.msg-action-btn {
  padding: 4px 8px;
  border-radius: 8px;
  opacity: 0.5;
  transition: all 0.2s;
}
.msg-action-btn:active,
.msg-action-btn:hover {
  opacity: 1;
}
.msg-action-btn.speaking {
  background: rgba(74, 144, 217, 0.1);
}
.action-icon { font-size: 16px; }

/* AI 加载动画 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 0;
}
.typing-dot {
  width: 8px;
  height: 8px;
  background: #4A90D9;
  border-radius: 50%;
  animation: typingBounce 1.4s infinite ease-in-out;
}
.typing-dot:nth-child(1) { animation-delay: 0s; }
.typing-dot:nth-child(2) { animation-delay: 0.2s; }
.typing-dot:nth-child(3) { animation-delay: 0.4s; }
.typing-text {
  font-size: 14px;
  color: #8F959E;
  margin-left: 4px;
}
@keyframes typingBounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

.quick-qs { margin-top: 16px; }
.quick-label { font-size: 13px; color: #8F959E; margin-bottom: 10px; display: block; }
.quick-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.quick-tag {
  padding: 10px 16px;
  background: #FFFFFF;
  border-radius: 20px;
  font-size: 13px;
  color: #646A73;
  border: 1px solid #E5E6EB;
}

.input-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: #FFFFFF;
  border-top: 1px solid #F0F1F5;
  padding-bottom: calc(10px + env(safe-area-inset-bottom));
}
.msg-input {
  flex: 1;
  height: 40px;
  background: #F5F7FA;
  border-radius: 20px;
  padding: 0 16px;
  font-size: 14px;
}
.send-btn {
  width: 40px;
  height: 40px;
  background: #4A90D9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.send-icon { color: #FFFFFF; font-size: 18px; font-weight: 700; }
.send-btn-disabled { opacity: 0.5; pointer-events: none; }

.modal-inner { text-align: center; }
.emer-modal-icon { font-size: 48px; margin-bottom: 12px; }
.emer-modal-title { font-size: 18px; font-weight: 600; color: #1F2329; display: block; margin-bottom: 8px; }
.emer-modal-body { font-size: 14px; color: #646A73; line-height: 1.7; display: block; margin-bottom: 20px; }
.btn-primary {
  width: 100%;
  background: linear-gradient(135deg, #FF6B6B, #FF4757);
  color: #FFFFFF;
  border: none;
  padding: 12px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
}

.history-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(74, 144, 217, 0.1);
  border-radius: 50%;
}
.history-icon { font-size: 20px; }
</style>
