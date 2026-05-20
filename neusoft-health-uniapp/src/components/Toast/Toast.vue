<template>
  <view v-if="visible" class="toast-overlay">
    <view class="toast-content">{{ message }}</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const visible = ref(false)
const message = ref('')
let timer: ReturnType<typeof setTimeout> | null = null

const show = (msg: string, duration = 2000) => {
  message.value = msg
  visible.value = true
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => { visible.value = false }, duration)
}

defineExpose({ show })
</script>

<style scoped>
.toast-overlay {
  position: fixed;
  top: 60%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
}
.toast-content {
  padding: 12px 24px;
  background: rgba(0,0,0,0.75);
  color: #FFF;
  font-size: 14px;
  border-radius: 8px;
  text-align: center;
  white-space: nowrap;
}
</style>
