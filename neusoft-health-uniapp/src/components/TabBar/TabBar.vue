<template>
  <view class="tab-bar">
    <view
      v-for="item in tabs"
      :key="item.page"
      :class="['tab-item', current === item.page ? 'tab-item-active' : '']"
      @click="switchTab(item.page)"
    >
      <component 
        :is="item.icon" 
        :size="22"
        :color="current === item.page ? '#4A90D9' : '#8F959E'"
      />
      <text :class="['tab-text', current === item.page ? 'tab-text-active' : '']">{{ item.text }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { markRaw, onMounted } from 'vue'
import HomeIcon from '@/components/Icons/HomeIcon.vue'
import ServicesIcon from '@/components/Icons/ServicesIcon.vue'
import MemberIcon from '@/components/Icons/MemberIcon.vue'
import ProfileIcon from '@/components/Icons/ProfileIcon.vue'

const props = withDefaults(defineProps<{
  current?: string
}>(), {
  current: 'index'
})

onMounted(() => {
  uni.hideTabBar()
})

const emit = defineEmits<{
  change: [page: string]
}>()

const tabs = [
  { page: 'index', text: '首页', icon: markRaw(HomeIcon) },
  { page: 'services', text: '服务', icon: markRaw(ServicesIcon) },
  { page: 'member', text: '会员', icon: markRaw(MemberIcon) },
  { page: 'profile', text: '我的', icon: markRaw(ProfileIcon) },
]

const switchTab = (page: string) => {
  if (page === props.current) return
  emit('change', page)
  uni.switchTab({ url: `/pages/${page}/index` })
}
</script>

<style scoped>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 1000;
}
.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  width: 25%;
  height: 100%;
  cursor: pointer;
}
.tab-icon {
  font-size: 22px;
  transition: transform 0.2s;
}
.tab-icon-active {
  transform: scale(1.1);
}
.tab-text {
  font-size: 11px;
  color: #8F959E;
  transition: color 0.2s;
}
.tab-text-active {
  color: #4A90D9;
  font-weight: 500;
}
</style>