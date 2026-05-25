<template>
  <view>
    <view :class="['nav-header', theme === 'blue' ? 'nav-header-blue' : 'nav-header-white']">
      <view class="nav-left" @click="handleBack">
        <slot name="left">
          <view v-if="showBack" class="nav-back-btn">
            <text class="nav-back-icon">‹</text>
          </view>
        </slot>
      </view>
      <view class="nav-center">
        <text :class="['nav-title', theme === 'blue' ? 'nav-title-light' : 'nav-title-dark']">{{ title }}</text>
      </view>
      <view class="nav-right">
        <slot name="right" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  title?: string
  showBack?: boolean
  theme?: 'blue' | 'white'
  fallbackUrl?: string
  handleBackBySelf?: boolean
}>(), {
  title: '',
  showBack: true,
  theme: 'white',
  fallbackUrl: '',
  handleBackBySelf: false
})

const emit = defineEmits<{ back: [] }>()

const TAB_PAGES = ['/pages/index/index', '/pages/services/index', '/pages/member/index', '/pages/profile/index']

const handleBack = () => {
  emit('back')
  
  if (props.handleBackBySelf) {
    return
  }
  
  const pages = getCurrentPages()
  if (pages.length <= 1) {
    if (props.fallbackUrl) {
      if (TAB_PAGES.includes(props.fallbackUrl)) {
        uni.switchTab({ url: props.fallbackUrl })
      } else {
        uni.redirectTo({ url: props.fallbackUrl })
      }
    }
  } else {
    uni.navigateBack()
  }
}
</script>

<style scoped>
.nav-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-sizing: border-box;
  flex-shrink: 0;
}
.nav-header-blue {
  background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
}
.nav-header-white {
  background: #FFFFFF;
  border-bottom: 1px solid #F0F1F5;
}
.nav-left, .nav-right {
  width: 60px;
  display: flex;
  align-items: center;
}
.nav-left { justify-content: flex-start; }
.nav-right { justify-content: flex-end; }
.nav-back-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}
.nav-back-icon {
  font-size: 28px;
  font-weight: 300;
  line-height: 1;
  color: #4A90D9;
}
.nav-header-blue .nav-back-icon {
  color: #FFFFFF;
}
.nav-center {
  flex: 1;
  text-align: center;
  overflow: hidden;
}
.nav-title {
  font-size: 17px;
  font-weight: 600;
}
.nav-title-dark { color: #1F2329; }
.nav-title-light { color: #FFFFFF; }
</style>
