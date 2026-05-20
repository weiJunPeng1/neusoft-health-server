<template>
  <view class="card" :style="cardStyle">
    <view v-if="title" class="card-header">
      <text class="card-title">{{ title }}</text>
      <text v-if="extra" class="card-extra" @click="$emit('extra')">{{ extra }}</text>
    </view>
    <slot />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  title?: string
  extra?: string
  padding?: string
  marginBottom?: string
}>(), {
  padding: '16px',
  marginBottom: '12px'
})

defineEmits<{ extra: [] }>()

const cardStyle = computed(() => ({
  padding: props.padding,
  marginBottom: props.marginBottom
}))
</script>

<style scoped lang="scss">
.card {
  background: #FFFFFF;
  border-radius: 16px;
  margin: 0 16px 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1F2329;
}
.card-extra {
  font-size: 13px;
  color: #4A90D9;
}
</style>
