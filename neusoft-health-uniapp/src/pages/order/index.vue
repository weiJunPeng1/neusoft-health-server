<template>
  <view class="page">
    <NavHeader title="我的订单" showBack @back="goBack" />

    <!-- 状态筛选 -->
    <view class="order-tabs">
      <view v-for="t in statusTabs" :key="t.key" :class="['otab', activeStatus === t.key ? 'otab-active' : '']" @click="activeStatus = t.key">{{ t.label }}</view>
    </view>

    <scroll-view scroll-y class="order-body">
      <view v-for="o in filteredOrders" :key="o.id" class="order-card">
        <view class="order-hd">
          <text class="order-plan">{{ o.plan }}</text>
          <text :class="['order-status', 's-' + o.status]">{{ o.statusText }}</text>
        </view>
        <view class="order-meta">
          <text class="order-time">{{ o.time }}</text>
          <text class="order-no">No. {{ o.orderNo }}</text>
        </view>
        <view class="order-foot">
          <text class="order-amount">¥{{ o.amount }}</text>
          <view class="order-actions">
            <button v-if="o.status === 'paid'" class="oact refund" @click="doRefund(o)">申请退款</button>
            <button v-if="o.status === 'pending'" class="oact pay" @click="goPay(o)">去支付</button>
          </view>
        </view>
      </view>

      <view v-if="filteredOrders.length === 0" class="empty-hint">暂无订单</view>

      <view style="height: 40px;" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import { ref, reactive, computed } from 'vue'

const activeStatus = ref('all')

const statusTabs = [
  { key: 'all', label: '全部' },
  { key: 'pending', label: '待支付' },
  { key: 'paid', label: '已支付' },
  { key: 'refunding', label: '退款中' },
]

const orders = reactive([
  { id: 1, plan: '黄金会员月卡', orderNo: '202605212230', time: '2026-05-21 22:30', amount: '39.90', status: 'paid', statusText: '已支付' },
  { id: 2, plan: '白银会员月卡', orderNo: '202605201500', time: '2026-05-20 15:00', amount: '19.90', status: 'refunding', statusText: '退款中' },
  { id: 3, plan: '铂金会员月卡', orderNo: '202605190900', time: '2026-05-19 09:00', amount: '69.90', status: 'pending', statusText: '待支付' },
])

const filteredOrders = computed(() => {
  if (activeStatus.value === 'all') return orders
  return orders.filter(o => o.status === activeStatus.value)
})

const goPay = (order: any) => {
  uni.navigateTo({ url: '/pages/payment/index' })
}

const doRefund = (order: any) => {
  order.status = 'refunding'
  order.statusText = '退款中'
  uni.showToast({ title: '退款申请已提交', icon: 'none' })
}

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; display: flex; flex-direction: column; }

.order-tabs { display: flex; background: #FFFFFF; border-bottom: 1px solid #E5E6EB; }
.otab { flex: 1; padding: 13px; text-align: center; font-size: 13px; color: #8F959E; }
.otab-active { color: #4A90D9; border-bottom: 2px solid #4A90D9; margin-bottom: -1px; font-weight: 500; }

.order-body { flex: 1; padding: 16px; }
.order-card { background: #FFFFFF; border-radius: 14px; padding: 16px; margin-bottom: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.order-hd { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.order-plan { font-size: 15px; font-weight: 500; color: #1F2329; }
.order-status { font-size: 12px; padding: 2px 10px; border-radius: 10px; }
.s-paid { background: #F0FFF4; color: #52C41A; }
.s-pending { background: #FFF7E6; color: #FAAD14; }
.s-refunding { background: #F0F7FF; color: #4A90D9; }
.order-meta { display: flex; gap: 12px; margin-bottom: 12px; }
.order-time, .order-no { font-size: 12px; color: #BBBFC4; }
.order-foot { display: flex; justify-content: space-between; align-items: center; border-top: 1px dashed #F0F1F5; padding-top: 12px; }
.order-amount { font-size: 20px; font-weight: 700; color: #FF4757; }
.order-actions { display: flex; gap: 8px; }
.oact { padding: 6px 14px; border-radius: 16px; font-size: 12px; border: none; }
.oact.refund { background: #FFF1F0; color: #FF4757; }
.oact.pay { background: linear-gradient(135deg, #4A90D9, #357ABD); color: #FFFFFF; }
.empty-hint { text-align: center; padding: 60px 0; color: #BBBFC4; font-size: 14px; }
</style>
