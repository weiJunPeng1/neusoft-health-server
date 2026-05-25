<template>
  <view class="page">
    <NavHeader title="设置" showBack fallbackUrl="/pages/profile/index" />
    <scroll-view scroll-y class="scroll-body" :scroll-top="scrollTop">
      <Card v-for="section in sections" :key="section.key">
        <view v-for="item in section.items" :key="item.key" class="list-row" @click="item.action?.()">
          <view class="list-left">
            <view class="icon-circle" :style="{ background: item.bg }">
              <SvgIcon :name="item.icon" :size="18" :color="item.color" />
            </view>
            <view class="list-info">
              <text class="list-title">{{ item.title }}</text>
              <text class="list-sub">{{ item.sub }}</text>
            </view>
          </view>
          <switch v-if="item.switch !== undefined" :checked="item.switch" @change="item.onChange" color="#4A90D9" />
          <slider v-else-if="item.slider !== undefined" :value="item.slider" :min="5" :max="15" @change="item.onChange" activeColor="#4A90D9" backgroundColor="#E5E6EB" class="list-slider" />
          <SvgIcon v-else name="chevron-right" :size="18" color="#BBBFC4" />
        </view>
      </Card>

      <view style="height: 40px;" />
      <view class="logout-btn-wrap">
        <button class="logout-btn" @click="logout">退出登录</button>
      </view>
    </scroll-view>

    <Modal :visible="showAboutModal" @close="showAboutModal = false">
      <view class="modal-content">
        <text class="modal-title">关于我们</text>
        <view class="about-content">
          <text class="about-version">东软智慧健康 v2.0</text>
          <text class="about-desc">东软智慧健康是一款基于人工智能的健康咨询服务平台，致力于为用户提供专业、便捷的健康咨询服务。</text>
          <text class="about-copyright">© 2026 东软集团股份有限公司</text>
        </view>
        <button class="btn-primary" @click="showAboutModal = false">我知道了</button>
      </view>
    </Modal>

    <Modal :visible="showDisclaimerModal" @close="showDisclaimerModal = false">
      <view class="modal-content">
        <text class="modal-title">免责声明</text>
        <scroll-view scroll-y class="disclaimer-content">
          <text class="disclaimer-text">本平台提供的AI健康咨询服务仅供参考，不能替代专业医生的诊断和治疗。

在使用本平台服务前，请您仔细阅读以下条款：

1. AI健康助手提供的建议是基于大数据和机器学习算法生成的，不代表专业医疗诊断；
2. 如您有紧急医疗需求，请立即拨打120急救电话或前往最近的医疗机构；
3. 本平台不承担因使用AI建议而产生的任何医疗风险；
4. 健康档案信息仅供AI助手参考，不构成医疗记录；
5. 本平台保留随时修改服务条款的权利。

继续使用本平台即表示您同意以上条款。</text>
        </scroll-view>
        <button class="btn-primary" @click="showDisclaimerModal = false">我知道了</button>
      </view>
    </Modal>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import Modal from '@/components/Modal/Modal.vue'
import { ref, reactive, computed } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { userApi } from '@/api/user'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import type { UserSettings } from '@/types'

const { scrollTop } = useScrollToTop()

const settings = reactive<UserSettings>({
  notificationEnabled: 1, voiceEnabled: 1, voiceSpeed: 1.0,
  voiceVolume: 80, voiceTone: 'default', anonymousMode: 0,
  privacyMode: 0, recommendEnabled: 1, autoSyncHealthProfile: 1
})

const showAboutModal = ref(false)
const showDisclaimerModal = ref(false)

interface SettingItem {
  key: string
  icon: string
  title: string
  sub: string
  bg: string
  color: string
  action?: () => void
  switch?: boolean
  slider?: number
  onChange?: (e: any) => void
}

interface SettingSection {
  key: string
  items: SettingItem[]
}

const update = (key: keyof UserSettings, value: any) => {
  (settings as any)[key] = value
  userApi.updateSettings({ [key]: value }).catch(console.error)
}

const toggle = (key: keyof UserSettings) => (e: any) => update(key, e.detail.value ? 1 : 0)

const sections = computed<SettingSection[]>(() => [
  {
    key: 'security', items: [
      { key: 'pwd', icon: 'lock', title: '修改密码', sub: '修改登录密码', bg: '#EBF5FF', color: '#4A90D9', action: () => uni.navigateTo({ url: '/pages/forgot-password/index' }) }
    ]
  },
  {
    key: 'privacy', items: [
      { key: 'anon', icon: 'shield', title: '匿名模式', sub: '开启后咨询内容将匿名化', bg: '#F0EFFF', color: '#7B68EE', switch: !!settings.privacyMode, onChange: toggle('privacyMode') }
    ]
  },
  {
    key: 'notify', items: [
      { key: 'notify', icon: 'bell', title: '消息通知', sub: '接收系统消息和咨询回复', bg: '#FFF4E6', color: '#F5A623', switch: !!settings.notificationEnabled, onChange: toggle('notificationEnabled') },
      { key: 'voice', icon: 'volume', title: '语音播报', sub: 'AI回复自动语音播报', bg: '#E8F8F0', color: '#34C759', switch: !!settings.voiceEnabled, onChange: toggle('voiceEnabled') },
      { key: 'speed', icon: 'zap', title: '语音速度', sub: `${settings.voiceSpeed}x`, bg: '#FFF0F0', color: '#FF6B6B', slider: settings.voiceSpeed * 10, onChange: (e: any) => update('voiceSpeed', e.detail.value / 10) }
    ]
  },
  {
    key: 'health', items: [
      { key: 'sync', icon: 'clipboard', title: '健康档案同步', sub: '问诊时自动携带健康档案信息', bg: '#E8F8F0', color: '#34C759', switch: !!settings.autoSyncHealthProfile, onChange: toggle('autoSyncHealthProfile') }
    ]
  },
  {
    key: 'about', items: [
      { key: 'about', icon: 'info', title: '关于我们', sub: '东软智慧健康 v2.0', bg: '#EBF5FF', color: '#4A90D9', action: () => showAboutModal.value = true },
      { key: 'disclaimer', icon: 'file-text', title: '免责声明', sub: 'AI医疗建议仅供参考', bg: '#FFF4E6', color: '#F5A623', action: () => showDisclaimerModal.value = true }
    ]
  }
])

const logout = () => {
  uni.showModal({
    title: '退出登录', content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        try { await authApi.logout() } catch {}
        uni.removeStorageSync('token')
        uni.showToast({ title: '已退出登录', icon: 'success' })
        setTimeout(() => uni.reLaunch({ url: '/pages/login/index' }), 1000)
      }
    }
  })
}

useUserStore.isLoggedIn || uni.navigateTo({ url: '/pages/login/index' })
userApi.getSettings().then((res) => res.data && Object.assign(settings, res.data)).catch(console.error)
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }
.scroll-body { padding-top: 12px; }
.list-row { display: flex; align-items: center; justify-content: space-between; padding: 16px 0; border-bottom: 1px solid #F0F1F5; }
.list-row:last-child { border-bottom: none; }
.list-left { display: flex; align-items: center; gap: 12px; flex: 1; }
.icon-circle { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.list-info { flex: 1; }
.list-title { font-size: 15px; color: #1F2329; font-weight: 500; display: block; }
.list-sub { font-size: 12px; color: #8F959E; margin-top: 2px; display: block; }
.list-slider { flex: 1; margin-left: 12px; }
.logout-btn-wrap { padding: 16px; }
.logout-btn { width: 100%; background: #FFFFFF; color: #FF4757; border: none; padding: 14px; border-radius: 12px; font-size: 15px; font-weight: 500; }
.modal-content { padding: 24px; }
.modal-title { font-size: 18px; font-weight: 600; color: #1F2329; text-align: center; display: block; margin-bottom: 20px; }
.about-content { text-align: center; }
.about-version { font-size: 16px; color: #4A90D9; font-weight: 500; display: block; margin-bottom: 12px; }
.about-desc { font-size: 14px; color: #646A73; line-height: 1.6; display: block; margin-bottom: 16px; }
.about-copyright { font-size: 12px; color: #BBBFC4; display: block; }
.disclaimer-content { max-height: 400px; margin-bottom: 20px; }
.disclaimer-text { font-size: 14px; color: #646A73; line-height: 1.8; white-space: pre-wrap; }
.btn-primary { width: 100%; background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%); color: #FFFFFF; border: none; padding: 14px; border-radius: 12px; font-size: 15px; font-weight: 500; }
</style>
