<template>
  <view class="login-container">
    <view class="login-header">
      <view class="logo-icon-wrap"><SvgIcon name="hospital" :size="60" color="#4A90D9" /></view>
      <text class="logo-text">东软智慧健康</text>
      <text class="logo-sub">AI健康助手 · 让健康更简单</text>
    </view>

    <view class="login-tabs">
      <view
        :class="['tab-item', loginType === 'sms' ? 'active' : '']"
        @click="loginType = 'sms'"
      >
        短信登录
      </view>
      <view
        :class="['tab-item', loginType === 'password' ? 'active' : '']"
        @click="loginType = 'password'"
      >
        密码登录
      </view>
    </view>

    <view class="login-form">
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input
          :value="phone"
          type="number"
          maxlength="11"
          placeholder="请输入手机号"
          class="form-input"
          @input="handlePhoneInput"
        />
      </view>

      <view class="form-item">
        <text class="form-label">{{ loginType === 'sms' ? '验证码' : '密码' }}</text>
        <view v-if="loginType === 'sms'" class="code-container">
          <input
            :value="code"
            type="number"
            maxlength="6"
            placeholder="请输入验证码"
            class="form-input code-input"
            @input="handleCodeInput"
          />
          <button
            :class="['code-btn', countdown > 0 ? 'disabled' : '']"
            :disabled="countdown > 0"
            @click="sendCode"
          >
            {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
          </button>
        </view>
        <input
          v-else
          :value="password"
          type="password"
          placeholder="请输入密码"
          class="form-input"
          @input="handlePasswordInput"
        />
      </view>

      <view v-if="loginType === 'password'" class="forgot-password">
        <text class="forgot-link" @click="goForgotPassword">忘记密码？</text>
      </view>

      <button class="login-btn" :disabled="!canLogin" @click="doLogin">登录</button>

      <text class="agree-text">登录即表示同意《用户协议》和《隐私政策》</text>

      <view class="skip-login">
        <text class="skip-link" @click="skipLogin">暂不登录，返回首页</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const loginType = ref<'sms' | 'password'>('sms')
const phone = ref('')
const code = ref('')
const password = ref('')
const countdown = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

const canLogin = computed(() => {
  if (phone.value.length !== 11) return false
  if (loginType.value === 'sms') {
    return code.value.length >= 4
  }
  return password.value.length >= 6
})

const handlePhoneInput = (e: any) => {
  phone.value = e.detail.value
}

const handleCodeInput = (e: any) => {
  code.value = e.detail.value
}

const handlePasswordInput = (e: any) => {
  password.value = e.detail.value
}

const sendCode = async () => {
  if (phone.value.length !== 11) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  try {
    await authApi.sendSmsCode(phone.value)
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0 && timer) {
        clearInterval(timer)
        timer = null
      }
    }, 1000)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
  } catch (err: any) {
    uni.showToast({ title: err.message || '发送失败', icon: 'none' })
  }
}

const doLogin = async () => {
  try {
    let res
    if (loginType.value === 'sms') {
      res = await authApi.loginBySms(phone.value, code.value)
    } else {
      res = await authApi.loginByPassword(phone.value, password.value)
    }
    const token = res.data.accessToken
    uni.setStorageSync('token', token)
    uni.setStorageSync('refreshToken', res.data.refreshToken)
    useUserStore.login(token)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 1000)
  } catch (err: any) {
    uni.showToast({ title: err.message || '登录失败', icon: 'none' })
  }
}

const goForgotPassword = () => {
  uni.navigateTo({ url: '/pages/forgot-password/index' })
}

const skipLogin = () => {
  uni.switchTab({ url: '/pages/index/index' })
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})

onShow(() => {
  uni.pageScrollTo({ scrollTop: 0, duration: 0 })
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: #F5F7FA;
  padding: 40px 20px;
  box-sizing: border-box;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #1F2329;
  display: block;
}

.logo-sub {
  font-size: 14px;
  color: #8F959E;
  margin-top: 8px;
  display: block;
}

.login-tabs {
  display: flex;
  background: #FFFFFF;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px;
  font-size: 15px;
  color: #8F959E;
  border-radius: 10px;
  transition: all 0.3s;
}

.tab-item.active {
  background: #4A90D9;
  color: #FFFFFF;
  font-weight: 500;
}

.login-form {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  font-size: 14px;
  color: #646A73;
  display: block;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  height: 48px;
  background: #F5F7FA;
  border-radius: 12px;
  padding: 0 16px;
  font-size: 15px;
  border: none;
  outline: none;
}

.code-container {
  display: flex;
  gap: 12px;
}

.code-input {
  flex: 1;
}

.code-btn {
  width: 110px;
  height: 48px;
  background: #4A90D9;
  color: #FFFFFF;
  border-radius: 12px;
  font-size: 13px;
  border: none;
}

.code-btn.disabled {
  background: #E5E6EB;
  color: #8F959E;
}

.forgot-password {
  text-align: right;
  margin-bottom: 8px;
}

.forgot-link {
  font-size: 13px;
  color: #4A90D9;
}

.login-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #4A90D9, #357ABD);
  color: #FFFFFF;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  margin-top: 8px;
}

.login-btn[disabled] {
  opacity: 0.5;
}

.agree-text {
  display: block;
  text-align: center;
  font-size: 12px;
  color: #BBBFC4;
  margin-top: 16px;
}

.skip-login {
  text-align: center;
  margin-top: 20px;
}

.skip-link {
  font-size: 14px;
  color: #4A90D9;
}
</style>