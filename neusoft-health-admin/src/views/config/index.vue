<template>
  <div class="config-management">
    <el-card shadow="hover">
      <template #header>
        <span>系统配置</span>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础配置" name="basic">
          <el-form :model="basicConfig" label-width="120px" style="max-width: 600px">
            <el-form-item label="系统名称">
              <el-input v-model="basicConfig.systemName" />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="basicConfig.systemDesc" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="AI配置" name="ai">
          <el-form :model="aiConfig" label-width="120px" style="max-width: 600px">
            <el-form-item label="API地址">
              <el-input v-model="aiConfig.apiUrl" />
            </el-form-item>
            <el-form-item label="模型名称">
              <el-input v-model="aiConfig.model" />
            </el-form-item>
            <el-form-item label="Temperature">
              <el-slider v-model="aiConfig.temperature" :min="0" :max="1" :step="0.1" show-input />
            </el-form-item>
            <el-form-item label="Max Tokens">
              <el-input-number v-model="aiConfig.maxTokens" :min="100" :max="4000" :step="100" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveAiConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="限流配置" name="rateLimit">
          <el-form :model="rateLimitConfig" label-width="120px" style="max-width: 600px">
            <el-form-item label="每分钟限制">
              <el-input-number v-model="rateLimitConfig.perMinute" :min="1" :max="1000" />
              <span style="margin-left: 12px; color: #666">次/分钟</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveRateLimitConfig">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'

const activeTab = ref('basic')

const basicConfig = ref({
  systemName: '东软智慧健康咨询系统',
  systemDesc: '基于AI的健康咨询服务平台'
})

const aiConfig = ref({
  apiUrl: 'https://api.deepseek.com',
  model: 'deepseek-chat',
  temperature: 0.7,
  maxTokens: 2048
})

const rateLimitConfig = ref({
  perMinute: 60
})

async function fetchConfig(key: string) {
  try {
    const data = await adminApi.getSystemConfig(key)
    return data
  } catch (error) {
    return null
  }
}

async function saveBasicConfig() {
  try {
    await adminApi.updateSystemConfig({
      configKey: 'system.basic',
      configValue: JSON.stringify(basicConfig.value)
    })
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

async function saveAiConfig() {
  try {
    await adminApi.updateSystemConfig({
      configKey: 'system.ai',
      configValue: JSON.stringify(aiConfig.value)
    })
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

async function saveRateLimitConfig() {
  try {
    await adminApi.updateSystemConfig({
      configKey: 'system.rateLimit',
      configValue: JSON.stringify(rateLimitConfig.value)
    })
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(async () => {
  const basic = await fetchConfig('system.basic')
  if (basic) basicConfig.value = JSON.parse((basic as any).configValue || '{}')

  const ai = await fetchConfig('system.ai')
  if (ai) aiConfig.value = JSON.parse((ai as any).configValue || '{}')

  const rateLimit = await fetchConfig('system.rateLimit')
  if (rateLimit) rateLimitConfig.value = JSON.parse((rateLimit as any).configValue || '{}')
})
</script>

<style scoped>
.config-management {
  padding: 0;
}
</style>
