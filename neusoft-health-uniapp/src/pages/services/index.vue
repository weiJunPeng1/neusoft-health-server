<template>
  <view class="page">
    <NavHeader title="健康服务" theme="blue" :showBack="false" />

    <scroll-view scroll-y class="page-scroll" :scroll-top="scrollTop">
      <!-- AI核心服务 -->
      <view class="section-label">AI 核心服务</view>
      <view class="service-grid">
        <view class="svc-card" v-for="s in aiServices" :key="s.page" @click="navTo(s.page)">
          <view :class="['svc-icon-wrap', s.color]">
            <SvgIcon :name="s.icon" :size="24" color="#FFFFFF" />
          </view>
          <text class="svc-title">{{ s.name }}</text>
          <text class="svc-desc">{{ s.desc }}</text>
          <text v-if="s.badge" :class="['svc-badge', s.badgeType]">{{ s.badge }}</text>
        </view>
      </view>

      <!-- 健康管理服务 -->
      <view class="section-label">健康管理服务</view>
      <view class="service-grid">
        <view class="svc-card" v-for="s in healthServices" :key="s.page" @click="navTo(s.page)">
          <view :class="['svc-icon-wrap', s.color]">
            <SvgIcon :name="s.icon" :size="24" color="#FFFFFF" />
          </view>
          <text class="svc-title">{{ s.name }}</text>
          <text class="svc-desc">{{ s.desc }}</text>
        </view>
      </view>

      <!-- 快捷入口 -->
      <view class="section-label">快捷入口</view>
      <view class="quick-row">
        <view class="quick-card" v-for="q in quickLinks" :key="q.page" @click="navTo(q.page)">
          <SvgIcon :name="q.icon" :size="20" color="#4A90D9" />
          <text class="quick-label">{{ q.text }}</text>
        </view>
      </view>

      <view style="height: 80px;" />
    </scroll-view>
    <TabBar current="services" />
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import { useScrollToTop } from '@/composables/useScrollToTop'

const { scrollTop } = useScrollToTop()

const aiServices = [
  { name: 'AI健康咨询', page: 'consult', icon: 'message', desc: '智能对话问诊，症状分析', badge: '热门', badgeType: 'hot', color: 'bg-purple' },
  { name: '健康助手', page: 'health-assistant', icon: 'globe', desc: '症状自查、每日健康', badge: '热门', badgeType: 'hot', color: 'bg-blue' },
  { name: 'AI报告解读', page: 'health-report', icon: 'file', desc: '上传报告，智能分析', badge: '新功能', badgeType: 'new', color: 'bg-gold' },
  { name: '健康搜索', page: 'search', icon: 'search', desc: '搜索症状、药品、知识', color: 'bg-green' },
]

const healthServices = [
  { name: '会员中心', page: 'member', icon: 'crown', desc: '订阅会员、查看权益', color: 'bg-gold' },
  { name: '我的订单', page: 'order', icon: 'package', desc: '订单管理、退款申请', color: 'bg-red' },
  { name: '支付结算', page: 'payment', icon: 'credit-card', desc: '扫码支付、订单结算', color: 'bg-blue' },
  { name: '健康档案', page: 'health-record', icon: 'clipboard', desc: '个人健康档案管理', color: 'bg-green' },
  { name: '药品查询', page: 'medicine-search', icon: 'pill', desc: '分类筛选、药品详情', color: 'bg-purple' },
  { name: '常见问题', page: 'faq', icon: 'question', desc: 'FAQ分类、快速解答', color: 'bg-teal' },
  { name: '消息通知', page: 'notification', icon: 'bell', desc: '系统/会员/咨询通知', color: 'bg-red' },
]

const quickLinks = [
  { text: '咨询历史', page: 'consult-history', icon: 'clock' },
  { text: '我的收藏', page: 'collections', icon: 'heart' },
  { text: '健康资讯', page: 'news', icon: 'newspaper' },
]

const navTo = (page: string) => {
  uni.navigateTo({ url: `/pages/${page}/index` })
}
</script>

<style scoped>
.page { min-height: 100vh; display: flex; flex-direction: column; background: #F5F7FA; box-sizing: border-box; }
.page-scroll { flex: 1; }

.section-label {
  font-size: 13px;
  color: #8F959E;
  font-weight: 500;
  padding: 16px 16px 8px;
}

.service-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 0 16px;
}

.svc-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 18px 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}
.svc-icon-wrap {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}
.svc-icon { font-size: 24px; }
.bg-purple { background: linear-gradient(135deg, #667EEA, #764BA2); }
.bg-blue { background: linear-gradient(135deg, #4A90D9, #357ABD); }
.bg-gold { background: linear-gradient(135deg, #FAAD14, #FFD700); }
.bg-green { background: linear-gradient(135deg, #52C41A, #3CB371); }
.bg-red { background: linear-gradient(135deg, #FF6B6B, #FF4757); }
.bg-teal { background: linear-gradient(135deg, #1ABC9C, #16A085); }
.svc-title { font-size: 14px; font-weight: 600; color: #1F2329; }
.svc-desc { font-size: 11px; color: #8F959E; margin-top: 4px; }
.svc-badge { margin-top: 6px; font-size: 10px; padding: 1px 8px; border-radius: 10px; }
.svc-badge.hot { background: #FFF1F0; color: #FF4757; }
.svc-badge.new { background: #E8F0FE; color: #4A90D9; }

.quick-row {
  display: flex;
  gap: 12px;
  padding: 0 16px;
}
.quick-card {
  flex: 1;
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.quick-icon { font-size: 20px; }
.quick-label { font-size: 12px; color: #646A73; }
</style>