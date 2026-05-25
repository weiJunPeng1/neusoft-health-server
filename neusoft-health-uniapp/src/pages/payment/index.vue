<template>
  <view class="page">
    <NavHeader title="支付结算" showBack fallbackUrl="/pages/member/index" />

    <scroll-view scroll-y class="scroll-body" :scroll-top="scrollTop">
      <!-- 订单信息 -->
      <Card v-if="order">
        <view class="order-info">
          <text class="order-label">商品名称</text>
          <text class="order-val">{{ order.planName }}</text>
        </view>
        <view class="order-info">
          <text class="order-label">订单编号</text>
          <text class="order-val">{{ order.orderNo }}</text>
        </view>
        <view class="order-info">
          <text class="order-label">创建时间</text>
          <text class="order-val">{{ order.createdTime }}</text>
        </view>
        <view class="order-divider" />
        <view class="order-total">
          <text class="order-label">应付金额</text>
          <text class="total-price">¥{{ order.amount }}</text>
        </view>
      </Card>

      <!-- 支付按钮 -->
      <view class="pay-section">
        <button class="pay-btn" @click="openPaymentInBrowser">去支付</button>
      </view>

      <!-- 检查支付状态按钮 -->
      <button v-if="order" class="check-btn" @click="checkPayment">检查支付状态</button>

      <!-- 模拟支付按钮（仅测试用） -->
      <button v-if="order" class="simulate-btn" @click="simulatePaid">模拟支付成功（演示）</button>

      <view style="height: 40px;" />
    </scroll-view>

    <!-- 支付成功弹窗 -->
    <Modal :visible="showSuccess" @close="goOrders">
      <view class="success-inner">
        <view class="success-icon-wrap"><SvgIcon name="check" :size="56" color="#52C41A" /></view>
        <text class="success-title">支付成功！</text>
        <text class="success-desc">您已成功订阅 {{ order?.planName }}</text>
        <view class="success-btns">
          <button class="s-btn outline" @click="goMember">会员中心</button>
          <button class="s-btn primary" @click="goOrders">查看订单</button>
        </view>
      </view>
    </Modal>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import Modal from '@/components/Modal/Modal.vue'
import { ref, onMounted, onUnmounted } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { paymentApi } from '@/api/payment'
import { useUserStore } from '@/stores/user'
import type { PaymentOrder } from '@/types'

const { scrollTop } = useScrollToTop()
const userStore = useUserStore

const showSuccess = ref(false)
const order = ref<PaymentOrder | null>(null)
const pagePayUrl = ref('')

const loadOrder = async (orderNo: string) => {
  try {
    const res = await paymentApi.queryOrder(orderNo)
    order.value = res.data
    if (order.value.payStatus === 1) {
      showSuccess.value = true
    }
  } catch (err) {
    console.error('加载订单失败', err)
    uni.showToast({ title: '加载订单失败', icon: 'none' })
  }
}

const generatePagePayUrl = async (orderNo: string) => {
  try {
    uni.showLoading({ title: '生成支付链接...' })
    const res = await paymentApi.createPagePayUrl(orderNo)
    pagePayUrl.value = res.data.pagePayUrl
    uni.hideLoading()
  } catch (err: any) {
    uni.hideLoading()
    console.error('生成支付链接失败', err)
    uni.showToast({ title: '生成支付链接失败，请重试', icon: 'none' })
  }
}

const openPaymentInBrowser = () => {
  if (!pagePayUrl.value) {
    uni.showToast({ title: '支付链接生成中，请稍候', icon: 'none' })
    return
  }
  
  const isApp = typeof plus !== 'undefined'
  if (isApp) {
    plus.runtime.openURL(pagePayUrl.value, (result: number) => {
      if (result) {
        uni.showToast({ title: '打开失败，请手动复制链接', icon: 'none' })
      }
    })
  } else {
    window.open(pagePayUrl.value, '_blank')
    uni.showToast({ title: '正在跳转支付页面...', icon: 'none' })
  }
}

const checkPayment = async () => {
  if (!order.value) return
  try {
    uni.showLoading({ title: '查询支付状态...' })
    const res = await paymentApi.queryOrder(order.value.orderNo)
    uni.hideLoading()
    order.value = res.data
    if (order.value.payStatus === 1) {
      showSuccess.value = true
      refreshMemberStatus()
    } else {
      uni.showToast({ title: '订单尚未支付', icon: 'none' })
    }
  } catch (err) {
    uni.hideLoading()
    console.error('查询订单状态失败', err)
    uni.showToast({ title: '查询失败', icon: 'none' })
  }
}

const refreshMemberStatus = async () => {
  try {
    await userStore.loadUserInfo()
    console.log('会员状态已刷新')
  } catch (err) {
    console.error('刷新会员状态失败', err)
  }
}

const simulatePaid = async () => {
  if (!order.value) return
  try {
    uni.showLoading({ title: '支付中...' })
    await paymentApi.simulatePayment(order.value.orderNo)
    uni.hideLoading()
    showSuccess.value = true
    refreshMemberStatus()
  } catch (err) {
    uni.hideLoading()
    console.error('支付失败', err)
    uni.showToast({ title: '支付失败', icon: 'none' })
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = (currentPage as any).options || {}
  
  if (options.orderNo) {
    loadOrder(options.orderNo).then(() => {
      if (order.value && order.value.payStatus === 0) {
        generatePagePayUrl(options.orderNo)
      }
    })
  }
})

onUnmounted(() => {})

const goBack = () => uni.navigateBack()
const goMember = () => uni.switchTab({ url: '/pages/member/index' })
const goOrders = () => {
  showSuccess.value = false
  uni.navigateTo({ url: '/pages/order/index' })
}
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }
.scroll-body { padding-top: 16px; }

.order-info { display: flex; justify-content: space-between; padding: 8px 0; }
.order-label { font-size: 13px; color: #8F959E; }
.order-val { font-size: 13px; color: #1F2329; }
.order-divider { border-top: 1px dashed #E5E6EB; margin: 10px 0; }
.order-total { display: flex; justify-content: space-between; align-items: center; }
.total-price { font-size: 28px; font-weight: 700; color: #FF4757; }

.pay-section {
  margin: 16px;
}
.pay-btn {
  width: 100%;
  background: linear-gradient(135deg, #4A90D9, #357ABD);
  color: #FFFFFF;
  border: none;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
}

.check-btn {
  margin: 0 16px;
  background: #F5F7FA;
  color: #646A73;
  border: 1px solid #E5E6EB;
  padding: 14px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  text-align: center;
}

.simulate-btn {
  margin: 0 16px;
  background: #F5F7FA;
  color: #8F959E;
  border: 1px solid #E5E6EB;
  padding: 12px;
  border-radius: 12px;
  font-size: 13px;
  text-align: center;
}

.success-inner { text-align: center; }
.success-icon { font-size: 56px; display: block; margin-bottom: 12px; }
.success-title { font-size: 20px; font-weight: 700; color: #1F2329; display: block; margin-bottom: 8px; }
.success-desc { font-size: 14px; color: #8F959E; display: block; margin-bottom: 24px; }
.success-btns { display: flex; gap: 12px; }
.s-btn { flex: 1; padding: 12px; border-radius: 10px; font-size: 14px; font-weight: 500; border: none; }
.s-btn.outline { background: #F5F7FA; color: #646A73; }
.s-btn.primary { background: linear-gradient(135deg, #4A90D9, #357ABD); color: #FFFFFF; }
</style>