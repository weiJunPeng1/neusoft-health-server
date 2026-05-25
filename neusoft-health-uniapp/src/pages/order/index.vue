<template>
  <view class="page">
    <NavHeader title="我的订单" showBack @back="goBack" handleBackBySelf="true" />

    <!-- 状态筛选 -->
    <view class="order-tabs">
      <view v-for="t in statusTabs" :key="t.key" :class="['otab', activeStatus === t.key ? 'otab-active' : '']" @click="activeStatus = t.key">{{ t.label }}</view>
    </view>

    <scroll-view scroll-y class="order-body" :scroll-top="scrollTop">
      <view v-for="o in filteredOrders" :key="o.orderNo" class="order-card">
        <view class="order-hd">
          <text class="order-plan">{{ o.planName }}</text>
          <text :class="['order-status', getStatusClass(o.payStatus)]">{{ o.payStatusDesc || getStatusText(o.payStatus) }}</text>
        </view>
        <view class="order-meta">
          <text class="order-time">{{ o.createdTime }}</text>
          <text class="order-no">No. {{ o.orderNo }}</text>
        </view>
        <view class="order-foot">
          <text class="order-amount">¥{{ o.amount }}</text>
          <view class="order-actions">
            <button v-if="o.payStatus === 1" class="oact refund" @click="doRefund(o)">申请退款</button>
            <button v-if="o.payStatus === 0" class="oact pay" @click="goPay(o)">去支付</button>
            <button v-if="o.payStatus === 0" class="oact cancel" @click="doCancel(o)">取消订单</button>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { paymentApi } from '@/api/payment'
import { useUserStore } from '@/stores/user'
import type { PaymentOrder } from '@/types'

const { scrollTop } = useScrollToTop()

const activeStatus = ref('all')

const statusTabs = [
  { key: 'all', label: '全部' },
  { key: 0, label: '待支付' },
  { key: 1, label: '已支付' },
  { key: 3, label: '退款中' },
  { key: 4, label: '已到账' },
  { key: 5, label: '到账失败' },
]

const orders = reactive<PaymentOrder[]>([])

const filteredOrders = computed(() => {
  if (activeStatus.value === 'all') return orders
  return orders.filter(o => o.payStatus === activeStatus.value)
})

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '退款中',
    4: '已到账',
    5: '到账失败'
  }
  return map[status] || ''
}

const getStatusClass = (status: number) => {
  const map: Record<number, string> = {
    0: 's-pending',
    1: 's-paid',
    2: 's-cancelled',
    3: 's-refund',
    4: 's-refunded',
    5: 's-refund-fail'
  }
  return map[status] || ''
}

const goPay = (order: PaymentOrder) => {
  uni.navigateTo({ url: `/pages/payment/index?orderNo=${order.orderNo}` })
}

const doRefund = async (order: PaymentOrder) => {
  try {
    await paymentApi.applyRefund(order.orderNo, '用户主动退款')
    order.payStatus = 3
    uni.showToast({ title: '退款申请已提交', icon: 'success' })
  } catch (err: any) {
    uni.showToast({ title: err.message || '退款申请失败', icon: 'none' })
  }
}

const doCancel = async (order: PaymentOrder) => {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消该订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await paymentApi.cancelOrder(order.orderNo)
          order.payStatus = 2
          uni.showToast({ title: '订单已取消', icon: 'success' })
        } catch (err: any) {
          uni.showToast({ title: err.message || '取消订单失败', icon: 'none' })
        }
      }
    }
  })
}

const goBack = () => {
  const pages = getCurrentPages()
  if (pages.length <= 1) {
    uni.switchTab({ url: '/pages/index/index' })
  } else {
    uni.navigateBack()
  }
}

onMounted(async () => {
  if (!useUserStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  try {
    const res = await paymentApi.getOrders()
    orders.splice(0, orders.length, ...(res.data || []))
  } catch (err) {
    console.error('获取订单列表失败', err)
  }
})
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
.s-refund { background: #F0F7FF; color: #4A90D9; }
.s-refunded { background: #F6FFED; color: #52C41A; }
.s-refund-fail { background: #FFF2F0; color: #FF4D4F; }
.s-cancelled { background: #F5F7FA; color: #BBBFC4; }
.order-meta { display: flex; gap: 12px; margin-bottom: 12px; }
.order-time, .order-no { font-size: 12px; color: #BBBFC4; }
.order-foot { display: flex; justify-content: space-between; align-items: center; border-top: 1px dashed #F0F1F5; padding-top: 12px; }
.order-amount { font-size: 20px; font-weight: 700; color: #FF4757; }
.order-actions { display: flex; gap: 8px; }
.oact { padding: 6px 14px; border-radius: 16px; font-size: 12px; border: none; }
.oact.refund { background: #FFF1F0; color: #FF4757; }
.oact.pay { background: linear-gradient(135deg, #4A90D9, #357ABD); color: #FFFFFF; }
.oact.cancel { background: #F5F7FA; color: #8F959E; border: 1px solid #E5E6EB; }
.empty-hint { text-align: center; padding: 60px 0; color: #BBBFC4; font-size: 14px; }
</style>
