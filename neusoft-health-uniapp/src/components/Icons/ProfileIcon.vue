<template>
  <view class="icon-wrapper">
    <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle 
        cx="12" 
        cy="8" 
        :r="5" 
        :fill="color"
      />
      <path 
        :fill="color" 
        d="M12 14C17.5 14 22 16.25 22 19.5C22 20.75 21.5 21.75 20.75 22.5L12 22L3.25 22.5C2.5 21.75 2 20.75 2 19.5C2 16.25 6.5 14 12 14Z"
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