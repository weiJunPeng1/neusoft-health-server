<template>
  <view class="page">
    <NavHeader title="会员中心" theme="blue" :showBack="false" />

    <scroll-view scroll-y class="page-scroll" :scroll-top="scrollTop">
      <!-- 会员状态卡片 -->
      <view class="member-status-card">
        <view class="status-top">
          <view class="level-badge">
            <SvgIcon name="crown" :size="28" color="#FFFFFF" />
          </view>
          <view class="status-info">
            <text class="level-name">{{ memberStatus.levelName || '普通用户' }}</text>
            <text class="level-expire">有效期至 {{ memberStatus.expireTime || '未开通' }}</text>
          </view>
          <view class="lv-tag">{{ memberStatus.levelCode || 'L0' }}</view>
        </view>
        <view class="status-quotas">
          <view class="quota-item">
            <text class="quota-val">{{ memberStatus.dailyQuota || 0 }}</text>
            <text class="quota-label">次/日</text>
          </view>
          <view class="quota-item">
            <text class="quota-val">{{ memberStatus.contextRounds || 0 }}</text>
            <text class="quota-label">轮上下文</text>
          </view>
          <view class="quota-item">
            <text class="quota-val">{{ memberStatus.autoSync ? '✓' : '—' }}</text>
            <text class="quota-label">自动建档</text>
          </view>
          <view class="quota-item">
            <text class="quota-val">{{ memberStatus.deepAnalysis ? '✓' : '—' }}</text>
            <text class="quota-label">深度分析</text>
          </view>
        </view>
      </view>

      <!-- 订阅方案 -->
      <view class="section-title">订阅会员</view>
      <view class="plan-list">
        <view class="plan-card" v-for="p in plans" :key="p.planCode">
          <view class="plan-left">
            <text class="plan-name">{{ p.planName }}</text>
            <view class="plan-price-row">
              <text class="plan-price">¥{{ p.price }}</text>
              <text v-if="p.originalPrice" class="plan-original">¥{{ p.originalPrice }}</text>
            </view>
            <text class="plan-quota">{{ p.durationDays }}天 · {{ p.levelCode }}</text>
          </view>
          <view class="plan-right">
            <button class="subscribe-btn" @click="doSubscribe(p.planCode)">立即开通</button>
          </view>
        </view>
      </view>

      <!-- 权益对比表 -->
      <view class="section-title">权益对比</view>
      <view class="benefits-table">
        <view class="benefit-row" v-for="(row,i) in benefits" :key="i">
          <text class="benefit-name">{{ row.name }}</text>
          <text v-for="(val,j) in row.values" :key="j" :class="['benefit-val', val === '✓' ? 'green' : val === '—' ? 'gray' : '']">{{ val }}</text>
        </view>
      </view>

      <!-- 订阅记录 -->
      <view class="section-title">订阅记录</view>
      <Card>
        <view class="history-item" v-for="h in history" :key="h.id">
          <view class="history-left">
            <text class="history-name">{{ h.planName || h.levelName || '订阅' }}</text>
            <text class="history-time">{{ h.createdTime || h.startTime || '' }}</text>
          </view>
          <view :class="['history-status', h.status === 'active' || h.status === 'PAID' ? 's-active' : 's-expired']">{{ h.statusText || h.status }}</view>
        </view>
        <view v-if="history.length === 0" class="empty-hint">
          <text class="empty-text">暂无订阅记录</text>
        </view>
      </Card>

      <view style="height: 80px;" />
    </scroll-view>
    <TabBar current="member" />
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import { reactive, onMounted } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { memberApi } from '@/api/member'
import { paymentApi } from '@/api/payment'
import { useUserStore } from '@/stores/user'
import type { MemberStatus, MemberLevelVO, SubscriptionPlan } from '@/types'

const { scrollTop } = useScrollToTop()

const memberStatus = reactive<MemberStatus>({
  levelCode: '',
  levelName: '',
  dailyQuota: 0,
  contextRounds: 0,
  autoSync: false,
  deepAnalysis: false,
  exportEnabled: false,
  expireTime: '',
  remainingDays: 0,
  inGracePeriod: false
})

const levels = reactive<MemberLevelVO[]>([])
const plans = reactive<SubscriptionPlan[]>([])
const history = reactive<any[]>([])

const benefits = [
  { name: '每日咨询次数', key: 'dailyQuota', values: [3, 20, 50, '∞'] },
  { name: '上下文轮数', key: 'contextRounds', values: [5, 15, 30, 50] },
  { name: '自动建档', key: 'autoSync', values: [false, true, true, true] },
  { name: '深度分析', key: 'deepAnalysis', values: [false, false, false, true] },
  { name: '导出记录', key: 'exportEnabled', values: [false, true, true, true] },
]

const doSubscribe = async (code: string) => {
  try {
    uni.showLoading({ title: '创建订单中...' })
    const res = await paymentApi.createOrder(code)
    uni.hideLoading()
    uni.navigateTo({ url: `/pages/payment/index?orderNo=${res.data.orderNo}` })
  } catch (err) {
    uni.hideLoading()
    console.error('创建订单失败', err)
    uni.showToast({ title: '创建订单失败', icon: 'none' })
  }
}

onMounted(async () => {
  if (!useUserStore.isLoggedIn) {
    console.log('用户未登录，跳转到登录页')
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  
  try {
    const [statusRes, levelsRes] = await Promise.all([
      memberApi.getStatus(),
      memberApi.getLevels()
    ])
    Object.assign(memberStatus, statusRes.data)
    levels.splice(0, levels.length, ...levelsRes.data)
  } catch (err: any) {
    console.error('获取会员信息失败', err)
    if (err.code === 401) {
      uni.removeStorageSync('token')
      useUserStore.logout()
    }
  }

  try {
    const plansRes = await paymentApi.getPlans()
    plans.splice(0, plans.length, ...plansRes.data)
  } catch (err) {
    console.error('获取订阅方案失败', err)
  }

  try {
    const historyRes = await memberApi.getHistory()
    if (historyRes.data) {
      history.splice(0, history.length, ...historyRes.data)
    }
  } catch (err: any) {
    console.error('获取订阅历史失败', err)
    if (err.code === 401) {
      uni.removeStorageSync('token')
      useUserStore.logout()
    }
  }
})
</script>

<style scoped>
.page { min-height: 100vh; display: flex; flex-direction: column; background: #F5F7FA; box-sizing: border-box; }
.page-scroll { flex: 1; }

.member-status-card {
  margin: 16px;
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 12px 40px rgba(102,126,234,0.4);
  color: #FFFFFF;
}
.status-top { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
.level-badge {
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.level-icon { font-size: 28px; }
.status-info { flex: 1; }
.level-name { font-size: 18px; font-weight: 600; display: block; }
.level-expire { font-size: 12px; opacity: 0.8; margin-top: 2px; display: block; }
.lv-tag {
  background: rgba(255,255,255,0.25);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}
.status-quotas { display: flex; justify-content: space-around; text-align: center; }
.quota-item { display: flex; flex-direction: column; }
.quota-val { font-size: 22px; font-weight: 700; }
.quota-label { font-size: 11px; opacity: 0.8; margin-top: 2px; }

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1F2329;
  padding: 16px 16px 12px;
}

.plan-list { padding: 0 16px; display: flex; flex-direction: column; gap: 10px; }
.plan-card {
  background: #FFFFFF;
  border-radius: 14px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  border: 1px solid #F0F1F5;
}
.plan-left { flex: 1; }
.plan-name { font-size: 14px; font-weight: 500; color: #1F2329; display: block; }
.plan-price-row { margin-top: 4px; }
.plan-price { font-size: 20px; font-weight: 700; color: #FF4757; }
.plan-original { font-size: 12px; color: #BBBFC4; text-decoration: line-through; margin-left: 8px; }
.plan-quota { font-size: 11px; color: #8F959E; margin-top: 4px; display: block; }
.subscribe-btn {
  background: linear-gradient(135deg, #4A90D9, #357ABD);
  color: #FFFFFF;
  font-size: 13px;
  padding: 8px 18px;
  border-radius: 20px;
  border: none;
  font-weight: 500;
}

.benefits-table { margin: 0 16px; background: #FFFFFF; border-radius: 14px; overflow: hidden; }
.benefit-row {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid #F5F5F5;
}
.benefit-row:last-child { border-bottom: none; }
.benefit-name { flex: 2; font-size: 13px; color: #646A73; }
.benefit-val { flex: 1; text-align: center; font-size: 13px; font-weight: 500; color: #1F2329; }
.benefit-val.green { color: #52C41A; }
.benefit-val.gray { color: #BBBFC4; }

.history-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; }
.history-item + .history-item { border-top: 1px solid #F5F5F5; }
.history-name { font-size: 14px; color: #1F2329; display: block; }
.history-time { font-size: 12px; color: #8F959E; margin-top: 2px; display: block; }
.history-status { font-size: 12px; padding: 3px 10px; border-radius: 10px; }
.s-active { background: #F0FFF4; color: #52C41A; }
.s-expired { background: #F5F7FA; color: #BBBFC4; }

.empty-hint { padding: 40px 0; text-align: center; }
.empty-text { font-size: 14px; color: #BBBFC4; }
</style>