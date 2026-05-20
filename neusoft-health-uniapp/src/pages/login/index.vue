<template>
  <view class="page">
    <NavHeader title="登录" showBack @back="goBack" />

    <view class="login-body">
      <view class="login-logo">
        <text class="logo-icon">🏥</text>
        <text class="logo-text">东软智慧健康</text>
        <text class="logo-sub">AI健康助手 · 让健康更简单</text>
      </view>

      <Card>
        <view class="form-group">
          <text class="form-label">手机号</text>
          <input
            v-model="phone"
            class="form-input"
            type="number"
            maxlength="11"
            placeholder="请输入手机号"
          />
        </view>
        <view class="form-group">
          <text class="form-label">验证码</text>
          <view class="code-row">
            <input
              v-model="code"
              class="form-input code-input"
              type="number"
              maxlength="6"
              placeholder="请输入验证码"
            />
            <button
              :class="['code-btn', countdown > 0 ? 'disabled' : '']"
              :disabled="countdown > 0"
              @click="sendCode"
            >
              {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
            </button>
          </view>
        </view>
      </Card>

      <button class="login-btn" :disabled="!canLogin" @click="doLogin">登录</button>

      <view class="login-agree">
        <text class="agree-text">登录即表示同意《用户协议》和《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import { ref, computed } from 'vue'

const phone = ref('')
const code = ref('')
const countdown = ref(0)

const canLogin = computed(() => phone.value.length === 11 && code.value.length >= 4)

const sendCode = () => {
  if (phone.value.length !== 11) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
  uni.showToast({ title: '验证码已发送', icon: 'none' })
}

const doLogin = () => {
  uni.setStorageSync('token', 'demo-token')
  uni.showToast({ title: '登录成功', icon: 'success' })
  setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 1000)
}

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }

.login-body { padding: 60px 16px 0; }

.login-logo { text-align: center; margin-bottom: 32px; }
.logo-icon { font-size: 56px; display: block; margin-bottom: 12px; }
.logo-text { font-size: 22px; font-weight: 700; color: #1F2329; display: block; }
.logo-sub { font-size: 13px; color: #8F959E; margin-top: 6px; display: block; }

.form-group { margin-bottom: 16px; }
.form-label { font-size: 13px; color: #8F959E; display: block; margin-bottom: 8px; }
.form-input { background: #F5F7FA; border-radius: 10px; padding: 12px 14px; font-size: 15px; width: 100%; box-sizing: border-box; }
.code-row { display: flex; gap: 12px; }
.code-input { flex: 1; }
.code-btn { padding: 12px 16px; background: #4A90D9; color: #FFFFFF; border-radius: 10px; font-size: 13px; border: none; white-space: nowrap; }
.code-btn.disabled { background: #E5E6EB; color: #8F959E; }

.login-btn {
  margin-top: 24px;
  width: 100%;
  background: linear-gradient(135deg, #4A90D9, #357ABD);
  color: #FFFFFF;
  border: none;
  padding: 14px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
}
.login-btn[disabled] { opacity: 0.5; }

.login-agree { text-align: center; margin-top: 20px; }
.agree-text { font-size: 12px; color: #BBBFC4; }
</style>
