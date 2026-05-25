<template>
  <view class="icon-wrapper">
    <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path 
        :fill="color" 
        d="M10 20V14H4V8H10V2L16 8V14H22V20H16V14H10V20Z"
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