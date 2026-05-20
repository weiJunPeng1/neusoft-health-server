<template>
  <view v-if="visible" class="modal-mask" @click="closeOnMask && $emit('close')">
    <view class="modal-content" @click.stop>
      <slot />
    </view>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  visible: boolean
  closeOnMask?: boolean
}>(), {
  closeOnMask: true
})

defineEmits<{ close: [] }>()
</script>

<style scoped>
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-content {
  width: 85%;
  max-height: 80vh;
  background: #FFF;
  border-radius: 16px;
  padding: 24px;
  overflow-y: auto;
}
</style>
