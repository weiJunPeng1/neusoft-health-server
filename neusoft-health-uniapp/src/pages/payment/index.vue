<template>
  <view class="page">
    <NavHeader title="支付结算" showBack @back="goBack" />

    <scroll-view scroll-y class="scroll-body">
      <!-- 倒计时提醒 -->
      <view class="timeout-banner">
        <text class="timeout-text">支付剩余时间：</text>
        <text class="timeout-val">{{ minutes }}:{{ seconds.toString().padStart(2, '0') }}</text>
      </view>

      <!-- 订单信息 -->
      <Card>
        <view class="order-info">
          <text class="order-label">商品名称</text>
          <text class="order-val">{{ order.product }}</text>
        </view>
        <view class="order-info">
          <text class="order-label">订单编号</text>
          <text class="order-val">{{ order.orderNo }}</text>
        </view>
        <view class="order-info">
          <text class="order-label">创建时间</text>
          <text class="order-val">{{ order.time }}</text>
        </view>
        <view class="order-divider" />
        <view class="order-total">
          <text class="order-label">应付金额</text>
          <text class="total-price">¥{{ order.amount }}</text>
        </view>
      </Card>

      <!-- 支付方式 -->
      <Card title="选择支付方式">
        <view class="pay-methods">
          <view
            :class="['pay-method', activePay === 'alipay' ? 'pm-active' : '']"
            @click="activePay = 'alipay'"
          >
            <text class="pm-icon">📱</text>
            <text class="pm-name">支付宝</text>
          </view>
          <view
            :class="['pay-method', activePay === 'wechat' ? 'pm-active' : '']"
            @click="activePay = 'wechat'"
          >
            <text class="pm-icon">💚</text>
            <text class="pm-name">微信支付</text>
          </view>
        </view>
      </Card>

      <!-- 扫码支付区 -->
      <view class="qr-section">
        <text class="qr-hint">请使用{{ activePay === 'alipay' ? '支付宝' : '微信' }}扫描二维码</text>
        <view class="qr-box">
          <text class="qr-placeholder">📱
扫码支付</text>
        </view>
        <text class="qr-sub">支付完成后将自动跳转</text>
      </view>

      <!-- 模拟支付按钮 -->
      <button class="pay-btn" @click="simulatePaid">模拟支付成功（演示）</button>

      <view style="height: 40px;" />
    </scroll-view>

    <!-- 支付成功弹窗 -->
    <Modal :visible="showSuccess" @close="goOrders">
      <view class="success-inner">
        <text class="success-icon">✅</text>
        <text class="success-title">支付成功！</text>
        <text class="success-desc">您已成功订阅 {{ order.product }}</text>
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'

const activePay = ref('alipay')
const showSuccess = ref(false)
const minutes = ref(4)
const seconds = ref(59)
let timer: ReturnType<typeof setInterval> | null = null

const order = reactive({
  product: '铂金会员月卡',
  orderNo: '20260521223001',
  time: '2026-05-21 22:30:00',
  amount: '19.90',
})

onMounted(() => {
  timer = setInterval(() => {
    if (seconds.value > 0) {
      seconds.value--
    } else if (minutes.value > 0) {
      minutes.value--
      seconds.value = 59
    } else {
      if (timer) clearInterval(timer)
    }
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const simulatePaid = () => {
  if (timer) clearInterval(timer)
  showSuccess.value = true
}

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

.timeout-banner {
  margin: 0 16px 16px;
  background: linear-gradient(135deg, #FFF3E0, #FFE0B2);
  border-radius: 12px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.timeout-text { font-size: 13px; color: #E65100; }
.timeout-val { font-size: 18px; font-weight: 700; color: #E65100; }

.order-info { display: flex; justify-content: space-between; padding: 8px 0; }
.order-label { font-size: 13px; color: #8F959E; }
.order-val { font-size: 13px; color: #1F2329; }
.order-divider { border-top: 1px dashed #E5E6EB; margin: 10px 0; }
.order-total { display: flex; justify-content: space-between; align-items: center; }
.total-price { font-size: 28px; font-weight: 700; color: #FF4757; }

.pay-methods { display: flex; gap: 12px; }
.pay-method {
  flex: 1;
  padding: 16px 10px;
  text-align: center;
  border-radius: 12px;
  border: 2px solid #E5E6EB;
  background: #FAFBFC;
}
.pm-active { border-color: #4A90D9; background: rgba(74,144,217,0.04); }
.pm-icon { font-size: 28px; display: block; margin-bottom: 6px; }
.pm-name { font-size: 13px; color: #1F2329; font-weight: 500; }

.qr-section { padding: 24px 16px; text-align: center; }
.qr-hint { font-size: 14px; color: #646A73; display: block; margin-bottom: 16px; }
.qr-box {
  width: 200px;
  height: 200px;
  margin: 0 auto;
  background: #FFFFFF;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}
.qr-placeholder { font-size: 18px; color: #BBBFC4; text-align: center; white-space: pre-line; }
.qr-sub { font-size: 12px; color: #BBBFC4; margin-top: 12px; display: block; }

.pay-btn {
  margin: 0 16px;
  background: linear-gradient(135deg, #4A90D9, #357ABD);
  color: #FFFFFF;
  border: none;
  padding: 14px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
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
