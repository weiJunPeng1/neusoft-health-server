<template>
  <view class="icon-wrapper">
    <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path 
        :fill="color" 
        d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2ZM12 15.4V6.6L9.27 10.15L12 11.77L14.73 10.15L12 6.6V15.4Z"
      />
    </svg>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  size?: number
  color?: string
}>(), {
  size: 22,
  color: '#8F959E'
})
</script>

<style scoped>
.icon-wrapper { display: flex; align-items: center; justify-content: center; }
</style>