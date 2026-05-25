<template>
  <view class="page">
    <NavHeader title="健康档案" showBack fallbackUrl="/pages/profile/index" />
    
    <scroll-view scroll-y class="scroll-body" :scroll-top="scrollTop">
      <view class="form-section">
        <view class="form-group">
          <text class="form-label">基本信息</text>
          
          <view class="form-row">
            <view class="form-item">
              <text class="item-label">身高</text>
              <view class="input-wrap">
                <input 
                  v-model="formData.height" 
                  type="digit" 
                  class="form-input" 
                  placeholder="请输入身高(cm)"
                />
                <text class="input-unit">cm</text>
              </view>
            </view>
            <view class="form-item">
              <text class="item-label">体重</text>
              <view class="input-wrap">
                <input 
                  v-model="formData.weight" 
                  type="digit" 
                  class="form-input" 
                  placeholder="请输入体重(kg)"
                />
                <text class="input-unit">kg</text>
              </view>
            </view>
          </view>
          
          <view class="form-item">
            <text class="item-label">血型</text>
            <view class="picker-wrap" @click="showBloodTypePicker = true">
              <text class="picker-value">{{ formData.bloodType || '请选择血型' }}</text>
              <text class="picker-arrow">›</text>
            </view>
          </view>
        </view>
        
        <view class="form-group">
          <text class="form-label">健康状况</text>
          
          <view class="form-item">
            <text class="item-label">过敏史</text>
            <textarea 
              v-model="formData.allergies" 
              class="form-textarea" 
              placeholder="请输入您的过敏史，如：青霉素、花粉等"
              :maxlength="500"
            />
          </view>
          
          <view class="form-item">
            <text class="item-label">既往病史</text>
            <textarea 
              v-model="formData.medicalHistory" 
              class="form-textarea" 
              placeholder="请输入您的既往病史，如：高血压、糖尿病等"
              :maxlength="500"
            />
          </view>
          
          <view class="form-item">
            <text class="item-label">用药历史</text>
            <textarea 
              v-model="formData.medicationHistory" 
              class="form-textarea" 
              placeholder="请输入您正在使用的药物，如：阿司匹林、降压药等"
              :maxlength="500"
            />
          </view>
          
          <view class="form-item">
            <text class="item-label">家族病史</text>
            <textarea 
              v-model="formData.familyHistory" 
              class="form-textarea" 
              placeholder="请输入家族中是否有遗传病史，如：高血压、糖尿病等"
              :maxlength="500"
            />
          </view>
        </view>
      </view>
      
      <view class="tips">
        <SvgIcon name="lightbulb" :size="16" color="#8B7355" />
        <text class="tips-text">填写健康档案后，AI问诊时会考虑您的健康状况，提供更精准的建议</text>
      </view>
      
      <view style="height: 40px;" />
      
      <view class="submit-wrap">
        <button class="submit-btn" @click="saveProfile">保存档案</button>
      </view>
    </scroll-view>
    
    <!-- 血型选择弹窗 -->
    <Modal :visible="showBloodTypePicker" @close="showBloodTypePicker = false">
      <view class="picker-modal">
        <text class="picker-title">选择血型</text>
        <view class="picker-list">
          <view 
            v-for="option in bloodTypes" 
            :key="option" 
            class="picker-option"
            :class="{ 'selected': formData.bloodType === option }"
            @click="selectBloodType(option)"
          >
            {{ option }}
          </view>
        </view>
        <button class="picker-close-btn" @click="showBloodTypePicker = false">取消</button>
      </view>
    </Modal>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Modal from '@/components/Modal/Modal.vue'
import { ref, reactive, onMounted } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import type { HealthProfile } from '@/types'

const { scrollTop } = useScrollToTop()

const showBloodTypePicker = ref(false)

const bloodTypes = ['A', 'B', 'AB', 'O', '未知']

const formData = reactive<Partial<HealthProfile>>({
  height: '',
  weight: '',
  bloodType: '',
  allergies: '',
  medicalHistory: '',
  medicationHistory: '',
  familyHistory: ''
})

const selectBloodType = (type: string) => {
  formData.bloodType = type
  showBloodTypePicker.value = false
}

const saveProfile = async () => {
  try {
    uni.showLoading({ title: '保存中...' })
    
    const data: Partial<HealthProfile> = {
      height: formData.height ? parseFloat(formData.height) : undefined,
      weight: formData.weight ? parseFloat(formData.weight) : undefined,
      bloodType: formData.bloodType || undefined,
      allergies: formData.allergies || undefined,
      medicalHistory: formData.medicalHistory || undefined,
      medicationHistory: formData.medicationHistory || undefined,
      familyHistory: formData.familyHistory || undefined
    }
    
    await userApi.updateHealthProfile(data)
    uni.hideLoading()
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (err: any) {
    uni.hideLoading()
    uni.showToast({ title: err.message || '保存失败', icon: 'none' })
  }
}

useUserStore.isLoggedIn || uni.navigateTo({ url: '/pages/login/index' })

userApi.getHealthProfile().then((res) => {
  if (res.data) {
    const profile = res.data
    formData.height = profile.height?.toString() || ''
    formData.weight = profile.weight?.toString() || ''
    formData.bloodType = profile.bloodType || ''
    formData.allergies = profile.allergies || ''
    formData.medicalHistory = profile.medicalHistory || ''
    formData.medicationHistory = profile.medicationHistory || ''
    formData.familyHistory = profile.familyHistory || ''
  }
}).catch(console.error)
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }
.scroll-body { padding-top: 12px; }

.form-section { padding: 0 16px; }
.form-group { background: #FFFFFF; border-radius: 12px; padding: 16px; margin-bottom: 12px; }
.form-label { font-size: 14px; font-weight: 600; color: #1F2329; margin-bottom: 16px; display: block; }

.form-row { display: flex; gap: 12px; }
.form-row .form-item { flex: 1; }

.form-item { margin-bottom: 16px; }
.form-item:last-child { margin-bottom: 0; }
.item-label { font-size: 13px; color: #8F959E; margin-bottom: 8px; display: block; }

.input-wrap { display: flex; align-items: center; background: #F5F7FA; border-radius: 8px; padding: 0 12px; }
.form-input { flex: 1; height: 44px; font-size: 14px; color: #1F2329; background: transparent; }
.input-unit { font-size: 14px; color: #8F959E; margin-left: 8px; }

.picker-wrap { display: flex; align-items: center; justify-content: space-between; background: #F5F7FA; border-radius: 8px; padding: 12px; }
.picker-value { font-size: 14px; color: #1F2329; }
.picker-arrow { font-size: 18px; color: #BBBFC4; }

.form-textarea { width: 100%; height: 80px; background: #F5F7FA; border-radius: 8px; padding: 12px; font-size: 14px; color: #1F2329; }

.tips { display: flex; align-items: flex-start; gap: 8px; padding: 12px 16px; margin: 0 16px; background: #FFFBE6; border-radius: 8px; }
.tips-icon { font-size: 16px; }
.tips-text { font-size: 12px; color: #8B7355; flex: 1; }

.submit-wrap { padding: 16px; }
.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
  color: #FFFFFF;
  border: none;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
}

.picker-modal { padding: 24px; }
.picker-title { font-size: 18px; font-weight: 600; color: #1F2329; text-align: center; display: block; margin-bottom: 20px; }
.picker-list { display: flex; flex-wrap: wrap; gap: 12px; }
.picker-option {
  flex: 1;
  min-width: calc(25% - 9px);
  padding: 12px;
  background: #F5F7FA;
  border-radius: 8px;
  text-align: center;
  font-size: 14px;
  color: #1F2329;
  border: 2px solid transparent;
}
.picker-option.selected {
  background: #E8F4FD;
  border-color: #4A90D9;
  color: #4A90D9;
}
.picker-close-btn {
  width: 100%;
  margin-top: 20px;
  background: #F5F7FA;
  color: #646A73;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 15px;
}
</style>