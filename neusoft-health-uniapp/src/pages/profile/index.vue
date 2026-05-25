<template>
  <view class="page">
    <NavHeader title="我的" theme="blue" :showBack="false" />

    <scroll-view scroll-y class="page-scroll" :scroll-top="scrollTop">
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-avatar">
          <SvgIcon v-if="!userInfo.avatar" name="user" :size="28" color="#FFFFFF" />
          <text v-else class="avatar-icon">{{ userInfo.avatar }}</text>
        </view>
        <view class="user-info">
          <text class="user-name">{{ userInfo.nickname || '健康用户' }}</text>
          <text class="user-phone">{{ userInfo.phone }}</text>
        </view>
        <view class="user-level">
          <text class="level-text">{{ memberStatus.levelName || '普通用户' }}</text>
        </view>
      </view>

      <!-- 功能列表 -->
      <Card>
        <view class="menu-list">
          <view class="menu-item" v-for="m in menus" :key="m.page" @click="navTo(m.page)">
            <SvgIcon :name="m.icon" :size="20" color="#4A90D9" />
            <text class="menu-label">{{ m.text }}</text>
            <text v-if="m.badge" class="menu-badge">{{ m.badge }}</text>
            <SvgIcon name="chevron-right" :size="18" color="#BBBFC4" />
          </view>
        </view>
      </Card>

      <!-- 底部菜单 -->
      <Card title="其他">
        <view class="menu-list">
          <view class="menu-item" v-for="m in bottomMenus" :key="m.page" @click="navTo(m.page)">
            <SvgIcon :name="m.icon" :size="20" color="#4A90D9" />
            <text class="menu-label">{{ m.text }}</text>
            <SvgIcon name="chevron-right" :size="18" color="#BBBFC4" />
          </view>
        </view>
      </Card>

      <view style="height: 80px;" />
    </scroll-view>
    <TabBar current="profile" />
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import { reactive, onMounted } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { userApi } from '@/api/user'
import { memberApi } from '@/api/member'
import { useUserStore } from '@/stores/user'
import type { UserProfile, MemberStatus } from '@/types'

const { scrollTop } = useScrollToTop()

const userInfo = reactive<UserProfile>({
  id: 0,
  phone: '',
  nickname: '',
  avatar: '',
  gender: 0,
  birthday: '',
  memberLevel: '',
  memberExpireTime: '',
  status: 1
})

const memberStatus = reactive<MemberStatus>({
  levelCode: '',
  levelName: '',
  dailyQuota: 0,
  contextRounds: 0,
  autoSync: false,
  deepAnalysis: false,
  exportEnabled: false,
  expireTime: '',
  remainingDays: 0,
  inGracePeriod: false
})

const menus = [
  { text: '我的收藏', page: 'collections', icon: 'heart' },
  { text: '咨询历史', page: 'consult-history', icon: 'message' },
  { text: '健康档案', page: 'health-record', icon: 'clipboard' },
  { text: '我的订单', page: 'order', icon: 'package' },
  { text: '消息中心', page: 'messages', icon: 'mail' },
  { text: '会员中心', page: 'member', icon: 'crown' },
]

const bottomMenus = [
  { text: '设置', page: 'settings', icon: 'settings' },
]

const navTo = (page: string) => {
  const tabs = ['index','services','member','profile']
  if (tabs.includes(page)) {
    uni.switchTab({ url: `/pages/${page}/index` })
  } else {
    uni.navigateTo({ url: `/pages/${page}/index` })
  }
}

const loadUserData = async () => {
  if (!useUserStore.isLoggedIn) {
    console.log('用户未登录，跳转到登录页')
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  try {
    const [profileRes, memberRes] = await Promise.all([
      userApi.getProfile(),
      memberApi.getStatus()
    ])
    Object.assign(userInfo, profileRes.data)
    Object.assign(memberStatus, memberRes.data)
  } catch (err: any) {
    console.error('获取用户信息失败', err)
    if (err.code === 401) {
      uni.removeStorageSync('token')
      useUserStore.logout()
    }
  }
}

onMounted(() => {
  loadUserData()
})
</script>

<style scoped>
.page { min-height: 100vh; display: flex; flex-direction: column; background: #F5F7FA; box-sizing: border-box; }
.page-scroll { flex: 1; }

.user-card {
  margin: 0 16px 16px;
  background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
}
.user-avatar {
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-icon { font-size: 28px; }
.user-info { flex: 1; }
.user-name { font-size: 17px; font-weight: 600; color: #FFFFFF; display: block; }
.user-phone { font-size: 13px; color: rgba(255,255,255,0.7); margin-top: 4px; display: block; }
.user-level {
  background: rgba(255,255,255,0.25);
  padding: 4px 12px;
  border-radius: 12px;
}
.level-text { font-size: 12px; color: #FFFFFF; font-weight: 500; }

.menu-list { padding: 4px 0; }
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #F5F5F5;
}
.menu-item:last-child { border-bottom: none; }
.menu-icon { font-size: 20px; }
.menu-label { flex: 1; font-size: 14px; color: #1F2329; }
.menu-badge {
  background: #FF4757;
  color: #FFFFFF;
  font-size: 11px;
  padding: 1px 7px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}
.menu-arrow { font-size: 20px; color: #BBBFC4; font-weight: 300; }
</style>