<template>
  <view class="icon-wrapper">
    <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path 
        :fill="color" 
        d="M12 2L2 7V17L12 22L22 17V7L12 2ZM12 18L4 13V9L12 4L20 9V13L12 18ZM12 15.5L17 12V9L12 6L7 9V12L12 15.5Z"
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