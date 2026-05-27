<template>
  <view class="page">
    <NavHeader title="支付结算" showBack fallbackUrl="/pages/member/index" />

    <scroll-view scroll-y class="scroll-body" :scroll-top="scrollTop">
      <!-- 订单信息 -->
      <Card v-if="order">
        <view class="order-info">
          <text class="order-label">商品名称</text>
          <text class="order-val">{{ escapeHtml(order.planName) }}</text>
        </view>
        <view class="order-info">
          <text class="order-label">订单编号</text>
          <text class="order-val">{{ escapeHtml(order.orderNo) }}</text>
        </view>
        <view class="order-info">
          <text class="order-label">创建时间</text>
          <text class="order-val">{{ escapeHtml(order.createdTime) }}</text>
        </view>
        <view class="order-divider" />
        <view class="order-total">
          <text class="order-label">应付金额</text>
          <text class="total-price">¥{{ order.amount }}</text>
        </view>
      </Card>

      <!-- 支付按钮 -->
      <view class="pay-section">
        <button class="pay-btn" :disabled="!canPay || isSubmitting" @click="handlePay">去支付</button>
      </view>

      <!-- 检查支付状态按钮 -->
      <button v-if="order && !isPaid" class="check-btn" :disabled="isChecking" @click="checkPayment">检查支付状态</button>

      <view style="height: 40px;" />
    </scroll-view>

    <!-- 支付成功弹窗 -->
    <Modal :visible="showSuccess" @close="goOrders">
      <view class="success-inner">
        <view class="success-icon-wrap"><SvgIcon name="check" :size="56" color="#52C41A" /></view>
        <text class="success-title">支付成功！</text>
        <text class="success-desc">您已成功订阅 {{ escapeHtml(order?.planName || '') }}</text>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { paymentApi } from '@/api/payment'
import { useUserStore } from '@/stores/user'
import type { PaymentOrder } from '@/types'

const { scrollTop } = useScrollToTop()
const userStore = useUserStore

// 状态管理
const showSuccess = ref(false)
const order = ref<PaymentOrder | null>(null)
const pagePayUrl = ref('')
const pollingTimer = ref<number | null>(null)
const pollingStarted = ref(false)
const isSubmitting = ref(false)
const isChecking = ref(false)

// 订单号正则（字母数字组合，长度限制）
const ORDER_NO_REGEX = /^[A-Za-z0-9]{16,32}$/

// 计算属性：是否可以支付
const canPay = computed(() => {
  return order.value && order.value.payStatus === 0 && !!pagePayUrl.value
})

// 计算属性：是否已支付
const isPaid = computed(() => {
  return order.value && order.value.payStatus === 1
})

// XSS防护：HTML转义
const escapeHtml = (str: string): string => {
  const div = document.createElement('div')
  div.textContent = str
  return div.innerHTML
}

// 订单号格式验证
const validateOrderNo = (orderNo: string): boolean => {
  if (!orderNo || typeof orderNo !== 'string') {
    return false
  }
  // 长度和格式验证
  if (!ORDER_NO_REGEX.test(orderNo)) {
    return false
  }
  // 防止SQL注入和XSS字符
  const dangerousChars = ['<', '>', '"', '\'', ';', '--', '/*', '*/']
  return !dangerousChars.some(char => orderNo.includes(char))
}

// 加载订单
const loadOrder = async (orderNo: string) => {
  // 验证订单号格式
  if (!validateOrderNo(orderNo)) {
    uni.showToast({ title: '订单号格式错误', icon: 'none' })
    return
  }

  try {
    const res = await paymentApi.queryOrder(orderNo)
    
    // 验证响应数据
    if (!res.data || !res.data.orderNo || res.data.orderNo !== orderNo) {
      uni.showToast({ title: '订单数据异常', icon: 'none' })
      return
    }

    order.value = res.data
    
    // 验证订单状态只能由后端决定
    if (order.value.payStatus === 1) {
      // 再次确认订单状态（防止篡改）
      confirmPaymentStatus(order.value.orderNo)
    }
  } catch (err) {
    console.error('加载订单失败', err)
    uni.showToast({ title: '加载订单失败', icon: 'none' })
  }
}

// 二次确认支付状态（防止前端篡改）
const confirmPaymentStatus = async (orderNo: string) => {
  try {
    const res = await paymentApi.queryOrder(orderNo)
    if (res.data && res.data.payStatus === 1) {
      showSuccess.value = true
    }
  } catch (err) {
    console.error('确认支付状态失败', err)
  }
}

// 生成支付链接
const generatePagePayUrl = async (orderNo: string) => {
  if (!validateOrderNo(orderNo)) return

  try {
    uni.showLoading({ title: '生成支付链接...' })
    const res = await paymentApi.createPagePayUrl(orderNo)
    
    // 验证支付链接
    if (!res.data || !res.data.pagePayUrl || !res.data.pagePayUrl.startsWith('http')) {
      throw new Error('支付链接无效')
    }
    
    pagePayUrl.value = res.data.pagePayUrl
    uni.hideLoading()
  } catch (err: any) {
    uni.hideLoading()
    console.error('生成支付链接失败', err)
    uni.showToast({ title: '生成支付链接失败，请重试', icon: 'none' })
  }
}

// 处理支付请求
const handlePay = () => {
  if (!canPay.value || isSubmitting.value) return
  
  isSubmitting.value = true
  
  setTimeout(() => {
    openPaymentInBrowser()
    isSubmitting.value = false
  }, 500)
}

// 打开支付页面
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
    uni.setClipboardData({
      data: pagePayUrl.value,
      success: () => {
        uni.showModal({
          title: '支付提示',
          content: '支付链接已复制到剪贴板，请打开浏览器粘贴链接完成支付',
          showCancel: false,
          confirmText: '我知道了',
          success: () => {
            startPolling()
          }
        })
      },
      fail: () => {
        uni.showToast({ title: '复制失败，请手动复制', icon: 'none' })
        startPolling()
      }
    })
  }
}

// 轮询查询支付状态
const startPolling = () => {
  if (pollingStarted.value || !order.value) return
  
  pollingStarted.value = true
  
  // 设置最大轮询次数（防止无限轮询）
  let maxPollCount = 60 // 最多轮询60次，约2分钟
  
  pollingTimer.value = window.setInterval(async () => {
    if (maxPollCount <= 0) {
      stopPolling()
      uni.showToast({ title: '查询超时，请手动检查', icon: 'none' })
      return
    }
    
    maxPollCount--
    
    try {
      const res = await paymentApi.queryOrder(order.value!.orderNo)
      
      // 验证响应数据完整性
      if (!res.data || res.data.orderNo !== order.value!.orderNo) {
        console.warn('订单数据验证失败')
        return
      }
      
      order.value = res.data
      
      // 支付状态只能由后端决定
      if (order.value.payStatus === 1) {
        stopPolling()
        // 再次确认支付状态
        confirmPaymentStatus(order.value.orderNo)
      }
    } catch (err) {
      console.error('轮询查询订单失败', err)
    }
  }, 2000)
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer.value !== null) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
  pollingStarted.value = false
}

// 检查支付状态
const checkPayment = async () => {
  if (!order.value || isChecking.value) return
  
  isChecking.value = true
  
  try {
    uni.showLoading({ title: '查询支付状态...' })
    const res = await paymentApi.queryOrder(order.value.orderNo)
    uni.hideLoading()
    
    // 验证数据完整性
    if (!res.data || res.data.orderNo !== order.value.orderNo) {
      uni.showToast({ title: '数据验证失败', icon: 'none' })
      return
    }
    
    order.value = res.data
    
    // 支付状态只能由后端决定
    if (order.value.payStatus === 1) {
      stopPolling()
      confirmPaymentStatus(order.value.orderNo)
    } else {
      uni.showToast({ title: '订单尚未支付', icon: 'none' })
    }
  } catch (err) {
    uni.hideLoading()
    console.error('查询订单状态失败', err)
    uni.showToast({ title: '查询失败', icon: 'none' })
  } finally {
    isChecking.value = false
  }
}

// 刷新会员状态
const refreshMemberStatus = async () => {
  try {
    await userStore.loadUserInfo()
    console.log('会员状态已刷新')
  } catch (err) {
    console.error('刷新会员状态失败', err)
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = (currentPage as any).options || {}
  
  // 验证订单号参数
  if (options.orderNo) {
    loadOrder(options.orderNo).then(() => {
      if (order.value && order.value.payStatus === 0) {
        generatePagePayUrl(options.orderNo)
      }
    })
  }
})

onUnmounted(() => {
  stopPolling()
})

const goBack = () => uni.navigateBack()
const goMember = () => {
  showSuccess.value = false
  uni.switchTab({ url: '/pages/member/index' })
}
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
.pay-btn[disabled] {
  background: #E5E6EB;
  color: #BBBFC4;
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
.check-btn[disabled] {
  opacity: 0.6;
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
