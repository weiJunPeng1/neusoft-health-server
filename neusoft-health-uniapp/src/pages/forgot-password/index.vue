<template>
  <view class="page">
    <NavHeader :title="hasPassword ? '修改密码' : '设置密码'" showBack fallbackUrl="/pages/settings/index" />
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrap">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 表单 -->
    <view v-else class="form-wrap">
      <!-- 旧密码（只有用户已有密码时显示） -->
      <view v-if="hasPassword" class="form-item">
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
      
      <button class="submit-btn" @click="submit">{{ hasPassword ? '确认修改' : '确认设置' }}</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import { ref, onMounted } from 'vue'
import { authApi } from '@/api/auth'
import { userApi } from '@/api/user'

const loading = ref(true)
const hasPassword = ref(true)
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const loadUserInfo = async () => {
  try {
    const res = await userApi.getProfile()
    if (res && res.data && 'hasPassword' in res.data) {
      hasPassword.value = res.data.hasPassword || false
    }
  } catch {
    hasPassword.value = true
  } finally {
    loading.value = false
  }
}

const submit = async () => {
  if (hasPassword.value && !oldPassword.value) {
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
    await authApi.updatePassword(oldPassword.value, newPassword.value)
    uni.showToast({ title: hasPassword.value ? '修改成功' : '设置成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (err: any) {
    uni.showToast({ title: err.message || (hasPassword.value ? '修改失败' : '设置失败'), icon: 'none' })
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }

.loading-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #E0E5EB;
  border-top-color: #4A90D9;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 16px;
  font-size: 14px;
  color: #8F959E;
}

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
