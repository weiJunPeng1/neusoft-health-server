<template>
  <view class="page">
    <NavHeader title="修改密码" showBack fallbackUrl="/pages/settings/index" />
    
    <view class="form-wrap">
      <view class="form-item">
        <text class="form-label">旧密码</text>
        <input
          v-model="oldPassword"
          class="form-input"
          type="password"
          placeholder="请输入旧密码"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">新密码</text>
        <input
          v-model="newPassword"
          class="form-input"
          type="password"
          placeholder="请输入新密码（6-20位）"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">确认新密码</text>
        <input
          v-model="confirmPassword"
          class="form-input"
          type="password"
          placeholder="请再次输入新密码"
        />
      </view>
      
      <button class="submit-btn" @click="submit">确认修改</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import { ref } from 'vue'
import { authApi } from '@/api/auth'

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const submit = async () => {
  if (!oldPassword.value) {
    uni.showToast({ title: '请输入旧密码', icon: 'none' })
    return
  }
  if (!newPassword.value) {
    uni.showToast({ title: '请输入新密码', icon: 'none' })
    return
  }
  if (newPassword.value.length < 6) {
    uni.showToast({ title: '密码长度至少6位', icon: 'none' })
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
    return
  }
  
  try {
    await authApi.changePassword(oldPassword.value, newPassword.value)
    uni.showToast({ title: '修改成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (err: any) {
    uni.showToast({ title: err.message || '修改失败', icon: 'none' })
  }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }
.form-wrap { padding: 20px; }
.form-item { background: #FFFFFF; padding: 16px; margin-bottom: 12px; border-radius: 12px; }
.form-label { font-size: 14px; color: #8F959E; margin-bottom: 8px; display: block; }
.form-input { width: 100%; font-size: 15px; color: #1F2329; }
.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
  color: #FFFFFF;
  border: none;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  margin-top: 20px;
}
</style>