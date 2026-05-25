<template>
  <view class="page">
    <NavHeader title="消息通知" showBack fallbackUrl="/pages/profile/index" />

    <!-- 分类Tab -->
    <view class="notif-tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        :class="['notif-tab', activeTab === tab.key ? 'tab-active' : '']"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
        <text v-if="tab.badge" class="tab-badge">{{ tab.badge }}</text>
      </view>
    </view>

    <!-- 通知列表 -->
    <scroll-view scroll-y class="notif-body" :scroll-top="scrollTop">
      <view
        v-for="item in filteredNotifs"
        :key="item.id"
        :class="['notif-item', item.read ? '' : 'unread']"
        @click="openDetail(item)"
      >
        <view :class="['notif-icon-box', item.iconBg]">
          <SvgIcon :name="item.icon" :size="20" color="#4A90D9" />
        </view>
        <view class="notif-content">
          <text class="notif-title">{{ item.title }}</text>
          <text class="notif-desc">{{ item.desc }}</text>
          <text class="notif-time">{{ item.time }}</text>
        </view>
        <view v-if="!item.read" class="unread-dot" />
      </view>

      <view v-if="filteredNotifs.length === 0" class="empty-hint">暂无通知</view>

      <view style="height: 40px;" />
    </scroll-view>

    <!-- 详情弹窗 -->
    <Modal :visible="!!detail" @close="detail = null">
      <view v-if="detail" class="detail-inner">
        <text class="detail-title">{{ detail.title }}</text>
        <text class="detail-time">{{ detail.time }}</text>
        <text class="detail-body">{{ detail.body }}</text>
        <button class="detail-close-btn" @click="detail = null">关闭</button>
      </view>
    </Modal>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Modal from '@/components/Modal/Modal.vue'
import { ref, reactive, computed } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'

const { scrollTop } = useScrollToTop()

const activeTab = ref('all')
const detail = ref<any>(null)

const tabs = [
  { key: 'all', label: '全部', badge: 0 },
  { key: 'system', label: '系统', badge: 2 },
  { key: 'member', label: '会员', badge: 0 },
  { key: 'consult', label: '咨询', badge: 1 },
]

const notifs = reactive([
  { id: 1, category: 'system', title: '系统升级公告', desc: 'V2.0版本将于5月25日凌晨升级维护', time: '05-20 18:00', read: false, icon: 'speaker', iconBg: 'bg-blue', body: '东软智慧健康V2.0版本将于2026年5月25日凌晨2:00-6:00进行升级维护。维护期间所有服务将暂停使用。升级完成后我们将为您带来更优质的AI健康服务体验！' },
  { id: 2, category: 'system', title: '隐私政策更新通知', desc: '我们更新了《用户隐私政策》，请查阅', time: '05-18 10:30', read: false, icon: 'file-text', iconBg: 'bg-orange', body: '我们更新了《用户隐私政策》，主要更新内容：明确了AI服务的数据处理方式，增加了健康档案数据的加密存储说明。继续使用即表示同意更新后的隐私政策。' },
  { id: 3, category: 'system', title: '欢迎使用东软智慧健康', desc: '开始您的第一次健康咨询吧', time: '05-10 08:00', read: true, icon: 'wave', iconBg: 'bg-green', body: '感谢您选择东软智慧健康！AI建议仅供参考，不能替代专业医生诊断。' },
  { id: 4, category: 'member', title: '会员续费提醒', desc: '黄金会员将于5月27日到期', time: '05-20 09:00', read: true, icon: 'crown', iconBg: 'bg-gold', body: '您的黄金会员（L2）将于2026年5月27日到期，剩余7天。到期后您将降为L0普通用户，咨询次数限制为每日3次。请及时续费。' },
  { id: 5, category: 'member', title: '会员升级成功', desc: '恭喜成功升级为黄金会员', time: '05-20 14:25', read: true, icon: 'celebration', iconBg: 'bg-gold', body: '恭喜您成功升级为黄金会员（L2）！有效期：2026年5月20日-2026年6月20日。' },
  { id: 6, category: 'consult', title: '您有新的AI回复', desc: '感冒发热38度的处理建议已回复', time: '05-20 16:45', read: false, icon: 'message', iconBg: 'bg-purple', body: 'AI健康助手已回复了您在"感冒症状咨询"会话中的新消息。包括体温管理建议、用药指导以及何时需要就医的判断标准。' },
])

const filteredNotifs = computed(() => {
  if (activeTab.value === 'all') return notifs
  return notifs.filter(n => n.category === activeTab.value)
})

const openDetail = (item: any) => {
  item.read = true
  detail.value = item
}

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; display: flex; flex-direction: column; }

.notif-tabs { display: flex; background: #FFFFFF; border-bottom: 1px solid #E5E6EB; }
.notif-tab { flex: 1; padding: 14px; text-align: center; font-size: 14px; color: #8F959E; font-weight: 500; }
.tab-active { color: #4A90D9; border-bottom: 2px solid #4A90D9; margin-bottom: -1px; }
.tab-badge { background: #FF4757; color: #FFFFFF; font-size: 10px; padding: 1px 6px; border-radius: 10px; margin-left: 4px; }

.notif-body { flex: 1; }
.notif-item { padding: 16px; background: #FFFFFF; border-bottom: 1px solid #F5F5F5; display: flex; gap: 12px; align-items: flex-start; position: relative; }
.notif-item.unread { background: #F0F7FF; }
.notif-icon-box { width: 42px; height: 42px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.notif-icon { font-size: 20px; }
.bg-blue { background: linear-gradient(135deg, #E8F0FE, #D0E3FC); }
.bg-orange { background: linear-gradient(135deg, #FFF4E6, #FFE0B2); }
.bg-green { background: linear-gradient(135deg, #F0FFF4, #C8F0D8); }
.bg-gold { background: linear-gradient(135deg, #FFF7E6, #FFE8B0); }
.bg-purple { background: linear-gradient(135deg, #F0E6FF, #E0CCFF); }
.notif-content { flex: 1; min-width: 0; }
.notif-title { font-size: 15px; font-weight: 500; color: #1F2329; display: block; margin-bottom: 2px; }
.notif-desc { font-size: 13px; color: #8F959E; display: block; line-height: 1.4; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.notif-time { font-size: 11px; color: #BBBFC4; display: block; margin-top: 6px; }
.unread-dot { width: 8px; height: 8px; background: #FF4757; border-radius: 50%; margin-top: 4px; flex-shrink: 0; }
.empty-hint { text-align: center; padding: 40px; color: #BBBFC4; font-size: 14px; }

.detail-inner { display: flex; flex-direction: column; }
.detail-title { font-size: 17px; font-weight: 600; color: #1F2329; display: block; margin-bottom: 4px; }
.detail-time { font-size: 12px; color: #BBBFC4; display: block; margin-bottom: 16px; }
.detail-body { font-size: 14px; color: #646A73; line-height: 1.8; display: block; margin-bottom: 20px; }
.detail-close-btn { background: #F5F7FA; border: none; padding: 12px; border-radius: 10px; font-size: 14px; color: #646A73; width: 100%; }
</style>
