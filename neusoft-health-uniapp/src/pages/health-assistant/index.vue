<template>
  <view class="page">
    <NavHeader title="AI健康助手" showBack fallbackUrl="/pages/services/index" />

    <view v-if="currentView === 'list'" class="main-view">
      <scroll-view scroll-y class="scroll-body">
        <view class="welcome-card">
          <view class="welcome-icon">
            <SvgIcon name="robot" :size="32" color="#FFFFFF" />
          </view>
          <view class="welcome-text">
            <text class="welcome-title">您好！我是您的AI健康助手</text>
            <text class="welcome-desc">选择下方功能，获取个性化健康服务</text>
          </view>
        </view>

        <view class="section-title">智能服务</view>
        <Card v-for="(item, i) in items" :key="i" :class="{ 'card-disabled': item.disabled }">
          <view class="list-row" @click="onItemClick(item)">
            <view class="list-icon-wrap" :style="{ background: item.bgColor }">
              <SvgIcon :name="item.icon" :size="22" color="#FFFFFF" />
            </view>
            <view class="list-left">
              <view class="list-title-row">
                <text class="list-title">{{ item.text }}</text>
                <text v-if="item.tag" class="list-tag" :style="{ background: item.tagBg, color: item.tagColor }">{{ item.tag }}</text>
              </view>
              <text class="list-sub">{{ item.desc }}</text>
            </view>
            <text class="list-arrow">›</text>
          </view>
        </Card>

        <view class="disclaimer-bar">
          <text class="disclaimer-text">AI健康助手提供的信息仅供参考，不能替代专业医疗诊断。如有身体不适，请及时就医。</text>
        </view>

        <view style="height: 40px;" />
      </scroll-view>
    </view>

    <view v-if="currentView === 'ai-result'" class="result-view">
      <view class="result-header">
        <view class="result-back" @click="backToList">
          <text class="back-arrow">‹</text>
          <text class="back-text">返回</text>
        </view>
        <text class="result-title">{{ currentFeature?.text }}</text>
        <view style="width: 60px;" />
      </view>

      <scroll-view scroll-y class="result-body">
        <view v-if="aiLoading" class="loading-state">
          <view class="loading-circle">
            <SvgIcon name="robot" :size="32" color="#4A90D9" />
          </view>
          <text class="loading-title">AI正在分析中...</text>
          <text class="loading-sub">请稍候，正在为您生成个性化内容</text>
          <view class="loading-dots">
            <view class="dot dot1" />
            <view class="dot dot2" />
            <view class="dot dot3" />
          </view>
        </view>

        <view v-if="!aiLoading && aiContent" class="result-content">
          <view class="result-card">
            <template v-for="(line, i) in parsedContent" :key="i">
              <text v-if="line.type === 'h1'" class="md-h1">{{ line.text }}</text>
              <text v-else-if="line.type === 'h2'" class="md-h2">{{ line.text }}</text>
              <text v-else-if="line.type === 'h3'" class="md-h3">{{ line.text }}</text>
              <view v-else-if="line.type === 'li'" class="md-li">
                <text class="md-bullet">•</text>
                <text class="md-li-text">
                  <template v-for="(seg, j) in line.segments" :key="j">
                    <text v-if="seg.bold" class="md-bold">{{ seg.text }}</text>
                    <text v-else>{{ seg.text }}</text>
                  </template>
                </text>
              </view>
              <view v-else-if="line.type === 'oli'" class="md-li">
                <text class="md-ol-num">{{ line.num }}.</text>
                <text class="md-li-text">
                  <template v-for="(seg, j) in line.segments" :key="j">
                    <text v-if="seg.bold" class="md-bold">{{ seg.text }}</text>
                    <text v-else>{{ seg.text }}</text>
                  </template>
                </text>
              </view>
              <text v-else-if="line.type === 'empty'" class="md-empty" />
              <text v-else class="md-p">
                <template v-for="(seg, j) in line.segments" :key="j">
                  <text v-if="seg.bold" class="md-bold">{{ seg.text }}</text>
                  <text v-else>{{ seg.text }}</text>
                </template>
              </text>
            </template>
          </view>
          <view v-if="aiDisclaimer" class="disclaimer-card">
            <text class="disclaimer-icon">ℹ</text>
            <text class="disclaimer-msg">{{ aiDisclaimer }}</text>
          </view>
        </view>

        <view v-if="!aiLoading && aiError" class="error-state">
          <SvgIcon name="warning" :size="40" color="#FF4757" />
          <text class="error-text">{{ aiError }}</text>
          <view class="retry-btn" @click="retryAI">
            <text class="retry-text">重试</text>
          </view>
        </view>

        <view style="height: 40px;" />
      </scroll-view>
    </view>

    <view v-if="currentView === 'reminders'" class="reminder-view">
      <view class="result-header">
        <view class="result-back" @click="backToList">
          <text class="back-arrow">‹</text>
          <text class="back-text">返回</text>
        </view>
        <text class="result-title">用药提醒</text>
        <view class="add-btn" @click="showAddReminder = true">
          <text class="add-text">+ 添加</text>
        </view>
      </view>

      <scroll-view scroll-y class="result-body">
        <view v-if="reminders.length === 0" class="empty-reminders">
          <SvgIcon name="pill" :size="48" color="#BBBFC4" />
          <text class="empty-title">暂无用药提醒</text>
          <text class="empty-desc">点击右上角"添加"按钮创建提醒</text>
        </view>

        <view v-for="(r, i) in reminders" :key="i" class="reminder-card">
          <view class="reminder-row">
            <view class="reminder-info">
              <text class="reminder-name">{{ r.name }}</text>
              <text class="reminder-dosage">{{ r.dosage }}</text>
            </view>
            <view class="reminder-delete" @click="deleteReminder(i)">
              <text class="delete-text">删除</text>
            </view>
          </view>
          <view class="reminder-times">
            <text v-for="(t, j) in r.times" :key="j" class="time-tag">{{ t }}</text>
          </view>
          <text v-if="r.note" class="reminder-note">{{ r.note }}</text>
        </view>

        <view style="height: 40px;" />
      </scroll-view>
    </view>

    <Modal :visible="showAddReminder" @close="showAddReminder = false">
      <view class="modal-add-reminder">
        <text class="modal-title">添加用药提醒</text>
        <view class="modal-field">
          <text class="field-label">药品名称</text>
          <input v-model="newReminder.name" class="field-input" placeholder="如：阿莫西林" />
        </view>
        <view class="modal-field">
          <text class="field-label">用法用量</text>
          <input v-model="newReminder.dosage" class="field-input" placeholder="如：每次1粒，每日3次" />
        </view>
        <view class="modal-field">
          <text class="field-label">提醒时间</text>
          <view class="time-picker-row">
            <view
              v-for="t in defaultTimes"
              :key="t"
              class="time-option"
              :class="{ selected: newReminder.times.includes(t) }"
              @click="toggleTime(t)"
            >
              {{ t }}
            </view>
          </view>
        </view>
        <view class="modal-field">
          <text class="field-label">备注（选填）</text>
          <input v-model="newReminder.note" class="field-input" placeholder="如：饭后服用" />
        </view>
        <view class="modal-actions">
          <button class="btn-cancel" @click="showAddReminder = false">取消</button>
          <button class="btn-confirm" @click="addReminder">确认添加</button>
        </view>
      </view>
    </Modal>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import Modal from '@/components/Modal/Modal.vue'
import { ref, reactive, computed } from 'vue'
import { assistantApi } from '@/api/assistant'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'

interface TextSegment {
  text: string
  bold: boolean
}

interface ContentLine {
  type: 'h1' | 'h2' | 'h3' | 'p' | 'li' | 'oli' | 'empty'
  text?: string
  num?: number
  segments?: TextSegment[]
}

interface Reminder {
  name: string
  dosage: string
  times: string[]
  note: string
}

type ViewType = 'list' | 'ai-result' | 'reminders'

const currentView = ref<ViewType>('list')
const currentFeature = ref<any>(null)
const aiLoading = ref(false)
const aiContent = ref('')
const aiDisclaimer = ref('')
const aiError = ref('')
const showAddReminder = ref(false)

const items = ref([
  {
    text: '健康报告解读',
    desc: 'AI分析体检报告，解读关键指标',
    icon: 'chart',
    bgColor: 'linear-gradient(135deg, #4A90D9, #357ABD)',
    tag: '',
    tagBg: '',
    tagColor: '',
    action: 'report',
    disabled: false
  },
  {
    text: '每日健康贴士',
    desc: '获取今日个性化健康建议',
    icon: 'lightbulb',
    bgColor: 'linear-gradient(135deg, #FAAD14, #FFD700)',
    tag: '每日更新',
    tagBg: '#FFF7E6',
    tagColor: '#D48806',
    action: 'daily_tips',
    disabled: false
  },
  {
    text: '用药提醒',
    desc: '智能定时提醒，不错过每次用药',
    icon: 'pill',
    bgColor: 'linear-gradient(135deg, #52C41A, #3CB371)',
    tag: '',
    tagBg: '',
    tagColor: '',
    action: 'reminder',
    disabled: false
  },
  {
    text: '健康档案',
    desc: '查看和管理我的健康数据',
    icon: 'clipboard',
    bgColor: 'linear-gradient(135deg, #722ED1, #9254DE)',
    tag: '',
    tagBg: '',
    tagColor: '',
    action: 'record',
    disabled: false
  },
  {
    text: '健康评估',
    desc: '全面健康评分与风险分析',
    icon: 'hospital',
    bgColor: 'linear-gradient(135deg, #FF4757, #FF6B6B)',
    tag: 'AI分析',
    tagBg: '#FFF1F0',
    tagColor: '#FF4757',
    action: 'health_assessment',
    disabled: false
  },
  {
    text: '生活建议',
    desc: '个性化饮食运动指导方案',
    icon: 'salad',
    bgColor: 'linear-gradient(135deg, #13C2C2, #36CFC9)',
    tag: '',
    tagBg: '',
    tagColor: '',
    action: 'lifestyle_advice',
    disabled: false
  }
])

const defaultTimes = ['08:00', '12:00', '18:00', '21:00']

const reminders = ref<Reminder[]>([])

const newReminder = reactive<Reminder>({
  name: '',
  dosage: '',
  times: [],
  note: ''
})

const parsedContent = computed<ContentLine[]>(() => {
  if (!aiContent.value) return []
  return parseMarkdown(aiContent.value)
})

const loadReminders = () => {
  try {
    const stored = uni.getStorageSync('medication_reminders')
    if (stored) {
      reminders.value = JSON.parse(stored)
    }
  } catch (e) {
    reminders.value = []
  }
}

const saveReminders = () => {
  uni.setStorageSync('medication_reminders', JSON.stringify(reminders.value))
}

const onItemClick = (item: any) => {
  switch (item.action) {
    case 'report':
      uni.navigateTo({ url: '/pages/health-report/index' })
      break
    case 'record':
      uni.navigateTo({ url: '/pages/health-record/index' })
      break
    case 'reminder':
      loadReminders()
      currentView.value = 'reminders'
      break
    case 'daily_tips':
    case 'health_assessment':
    case 'lifestyle_advice':
      currentFeature.value = item
      currentView.value = 'ai-result'
      fetchAIResult(item.action)
      break
  }
}

const fetchAIResult = async (feature: string) => {
  aiLoading.value = true
  aiContent.value = ''
  aiDisclaimer.value = ''
  aiError.value = ''

  try {
    let healthProfile: string | undefined
    try {
      const profileRes = await userApi.getHealthProfile()
      if (profileRes.data) {
        const p = profileRes.data
        const parts: string[] = []
        if (p.height) parts.push(`身高${p.height}cm`)
        if (p.weight) parts.push(`体重${p.weight}kg`)
        if (p.bloodType) parts.push(`血型${p.bloodType}`)
        if (p.allergies) parts.push(`过敏史：${p.allergies}`)
        if (p.medicalHistory) parts.push(`病史：${p.medicalHistory}`)
        if (p.medicationHistory) parts.push(`用药：${p.medicationHistory}`)
        if (p.familyHistory) parts.push(`家族史：${p.familyHistory}`)
        if (parts.length > 0) {
          healthProfile = parts.join('；')
        }
      }
    } catch (e) {
      // ignore profile fetch error
    }

    const res = await assistantApi.query(feature, healthProfile)
    if (res.data) {
      aiContent.value = res.data.content || ''
      aiDisclaimer.value = res.data.disclaimer || '以上内容由AI生成，仅供参考，不构成医疗诊断建议。'
    } else {
      aiError.value = '获取内容失败，请稍后再试'
    }
  } catch (err: any) {
    aiError.value = err.message || '网络错误，请稍后再试'
  } finally {
    aiLoading.value = false
  }
}

const retryAI = () => {
  if (currentFeature.value) {
    fetchAIResult(currentFeature.value.action)
  }
}

if (!useUserStore.isLoggedIn) {
  uni.navigateTo({ url: '/pages/login/index' })
}

const backToList = () => {
  currentView.value = 'list'
  aiContent.value = ''
  aiError.value = ''
}

const toggleTime = (t: string) => {
  const idx = newReminder.times.indexOf(t)
  if (idx >= 0) {
    newReminder.times.splice(idx, 1)
  } else {
    newReminder.times.push(t)
  }
}

const addReminder = () => {
  if (!newReminder.name.trim()) {
    uni.showToast({ title: '请输入药品名称', icon: 'none' })
    return
  }
  if (!newReminder.dosage.trim()) {
    uni.showToast({ title: '请输入用法用量', icon: 'none' })
    return
  }
  if (newReminder.times.length === 0) {
    uni.showToast({ title: '请选择提醒时间', icon: 'none' })
    return
  }

  reminders.value.push({
    name: newReminder.name.trim(),
    dosage: newReminder.dosage.trim(),
    times: [...newReminder.times].sort(),
    note: newReminder.note.trim()
  })
  saveReminders()

  newReminder.name = ''
  newReminder.dosage = ''
  newReminder.times = []
  newReminder.note = ''
  showAddReminder.value = false
  uni.showToast({ title: '添加成功', icon: 'success' })
}

const deleteReminder = (index: number) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除"${reminders.value[index].name}"的提醒吗？`,
    success: (res) => {
      if (res.confirm) {
        reminders.value.splice(index, 1)
        saveReminders()
        uni.showToast({ title: '已删除', icon: 'success' })
      }
    }
  })
}

function parseLineSegments(text: string): TextSegment[] {
  const segments: TextSegment[] = []
  const re = /(\*\*(.+?)\*\*)/g
  let lastIndex = 0
  let match: RegExpExecArray | null
  while ((match = re.exec(text)) !== null) {
    if (match.index > lastIndex) {
      segments.push({ text: text.slice(lastIndex, match.index), bold: false })
    }
    segments.push({ text: match[2], bold: true })
    lastIndex = match.index + match[0].length
  }
  if (lastIndex < text.length) {
    segments.push({ text: text.slice(lastIndex), bold: false })
  }
  if (segments.length === 0) {
    segments.push({ text, bold: false })
  }
  return segments
}

function parseMarkdown(md: string): ContentLine[] {
  const rawLines = md.split('\n')
  return rawLines
    .map((raw) => raw.trimEnd())
    .filter((raw) => raw !== '\r')
    .map((raw): ContentLine | null => {
      const trimmed = raw.trim()
      if (!trimmed) return { type: 'empty' }
      if (/^###\s/.test(trimmed)) return { type: 'h3', text: trimmed.replace(/^###\s*/, '') }
      if (/^##\s/.test(trimmed)) return { type: 'h2', text: trimmed.replace(/^##\s*/, '') }
      if (/^#\s/.test(trimmed)) return { type: 'h1', text: trimmed.replace(/^#\s*/, '') }
      if (/^[-*]\s/.test(trimmed)) {
        return { type: 'li', segments: parseLineSegments(trimmed.replace(/^[-*]\s*/, '')) }
      }
      const olMatch = trimmed.match(/^(\d+)\.\s(.+)/)
      if (olMatch) {
        return { type: 'oli', num: parseInt(olMatch[1]), segments: parseLineSegments(olMatch[2]) }
      }
      return { type: 'p', segments: parseLineSegments(trimmed) }
    })
    .filter((line): line is ContentLine => line !== null)
}
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; display: flex; flex-direction: column; }
.main-view { flex: 1; display: flex; flex-direction: column; }
.scroll-body { flex: 1; padding-top: 12px; }

.welcome-card {
  margin: 0 16px 16px;
  padding: 20px;
  background: linear-gradient(135deg, #667EEA, #764BA2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
}
.welcome-icon {
  width: 52px;
  height: 52px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.welcome-text { flex: 1; }
.welcome-title { font-size: 16px; font-weight: 600; color: #FFFFFF; display: block; margin-bottom: 4px; }
.welcome-desc { font-size: 12px; color: rgba(255,255,255,0.8); display: block; }

.section-title { font-size: 13px; color: #8F959E; padding: 4px 16px 10px; font-weight: 500; }

.list-row { display: flex; align-items: center; gap: 12px; }
.list-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.list-left { flex: 1; }
.list-title-row { display: flex; align-items: center; gap: 8px; }
.list-title { font-size: 15px; color: #1F2329; font-weight: 500; }
.list-tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}
.list-sub { font-size: 12px; color: #8F959E; margin-top: 2px; display: block; }
.list-arrow { font-size: 18px; color: #BBBFC4; }

.card-disabled { opacity: 0.5; }

.disclaimer-bar { margin: 16px; padding: 12px; background: rgba(74,144,217,0.06); border-radius: 8px; }
.disclaimer-text { font-size: 11px; color: #8F959E; line-height: 1.6; }

.result-view { flex: 1; display: flex; flex-direction: column; }
.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #FFFFFF;
  border-bottom: 1px solid #F0F1F5;
}
.result-back { display: flex; align-items: center; gap: 4px; width: 60px; }
.back-arrow { font-size: 22px; color: #4A90D9; }
.back-text { font-size: 14px; color: #4A90D9; }
.result-title { font-size: 16px; font-weight: 600; color: #1F2329; }
.add-btn { width: 60px; display: flex; justify-content: flex-end; }
.add-text { font-size: 14px; color: #4A90D9; font-weight: 500; }

.result-body { flex: 1; padding: 16px; }

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;
}
.loading-circle {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #E8F0FE, #D0E3FC);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}
.loading-title { font-size: 16px; font-weight: 500; color: #1F2329; margin-bottom: 6px; }
.loading-sub { font-size: 13px; color: #8F959E; }
.loading-dots { display: flex; gap: 8px; margin-top: 24px; }
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4A90D9;
  animation: bounce 1.4s ease-in-out infinite;
}
.dot1 { animation-delay: 0s; }
.dot2 { animation-delay: 0.2s; }
.dot3 { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 80%, 100% { opacity: 0.3; transform: scale(0.8); }
  40% { opacity: 1; transform: scale(1); }
}

.result-card { background: #FFFFFF; border-radius: 12px; padding: 16px; margin-bottom: 12px; }

.md-h1 { font-size: 18px; font-weight: 700; color: #1F2329; display: block; margin: 12px 0 8px; line-height: 1.5; }
.md-h2 { font-size: 16px; font-weight: 700; color: #1F2329; display: block; margin: 10px 0 6px; line-height: 1.5; }
.md-h3 { font-size: 15px; font-weight: 600; color: #1F2329; display: block; margin: 8px 0 4px; line-height: 1.5; }
.md-p { font-size: 14px; color: #1F2329; line-height: 1.8; display: block; margin-bottom: 6px; }
.md-li { display: flex; flex-direction: row; margin-bottom: 4px; padding-left: 4px; }
.md-bullet { font-size: 14px; color: #4A90D9; margin-right: 8px; line-height: 1.8; flex-shrink: 0; }
.md-ol-num { font-size: 14px; color: #4A90D9; margin-right: 8px; line-height: 1.8; flex-shrink: 0; font-weight: 600; min-width: 20px; }
.md-li-text { font-size: 14px; color: #1F2329; line-height: 1.8; flex: 1; }
.md-bold { font-weight: 700; color: #1F2329; }
.md-empty { display: block; height: 8px; }

.disclaimer-card {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: rgba(74,144,217,0.06);
  border-radius: 8px;
  margin-bottom: 12px;
}
.disclaimer-icon { font-size: 14px; color: #4A90D9; flex-shrink: 0; }
.disclaimer-msg { font-size: 12px; color: #8F959E; line-height: 1.6; }

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
}
.error-text { font-size: 14px; color: #646A73; margin-top: 12px; text-align: center; }
.retry-btn {
  margin-top: 16px;
  padding: 10px 32px;
  background: #4A90D9;
  border-radius: 20px;
}
.retry-text { font-size: 14px; color: #FFFFFF; font-weight: 500; }

.reminder-view { flex: 1; display: flex; flex-direction: column; }

.empty-reminders {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;
}
.empty-title { font-size: 15px; color: #8F959E; margin-top: 12px; }
.empty-desc { font-size: 12px; color: #BBBFC4; margin-top: 4px; }

.reminder-card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}
.reminder-row { display: flex; align-items: center; justify-content: space-between; }
.reminder-info { flex: 1; }
.reminder-name { font-size: 16px; font-weight: 600; color: #1F2329; display: block; }
.reminder-dosage { font-size: 13px; color: #646A73; margin-top: 4px; display: block; }
.reminder-delete { padding: 6px 12px; }
.delete-text { font-size: 13px; color: #FF4757; }
.reminder-times { display: flex; gap: 8px; margin-top: 10px; flex-wrap: wrap; }
.time-tag {
  padding: 4px 12px;
  background: #E8F4FD;
  border-radius: 12px;
  font-size: 13px;
  color: #4A90D9;
  font-weight: 500;
}
.reminder-note { font-size: 12px; color: #8F959E; margin-top: 8px; display: block; }

.modal-add-reminder { padding: 24px; }
.modal-title { font-size: 18px; font-weight: 600; color: #1F2329; text-align: center; display: block; margin-bottom: 20px; }
.modal-field { margin-bottom: 16px; }
.field-label { font-size: 13px; color: #8F959E; margin-bottom: 8px; display: block; }
.field-input {
  width: 100%;
  height: 44px;
  background: #F5F7FA;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
  color: #1F2329;
}
.time-picker-row { display: flex; gap: 10px; flex-wrap: wrap; }
.time-option {
  padding: 8px 16px;
  background: #F5F7FA;
  border-radius: 8px;
  font-size: 14px;
  color: #646A73;
  border: 2px solid transparent;
}
.time-option.selected {
  background: #E8F4FD;
  border-color: #4A90D9;
  color: #4A90D9;
}
.modal-actions { display: flex; gap: 12px; margin-top: 20px; }
.btn-cancel {
  flex: 1;
  background: #F5F7FA;
  color: #646A73;
  border: none;
  padding: 12px;
  border-radius: 8px;
  font-size: 15px;
}
.btn-confirm {
  flex: 1;
  background: #4A90D9;
  color: #FFFFFF;
  border: none;
  padding: 12px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
}
</style>
