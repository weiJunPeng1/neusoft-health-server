<template>
  <view class="page">
    <NavHeader title="我的" theme="blue" :showBack="false" />

    <scroll-view scroll-y class="page-scroll">
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-avatar">
          <text class="avatar-icon">👤</text>
        </view>
        <view class="user-info">
          <text class="user-name">健康用户</text>
          <text class="user-phone">138****8888</text>
        </view>
        <view class="user-level">
          <text class="level-text">黄金会员</text>
        </view>
      </view>

      <!-- 功能列表 -->
      <Card>
        <view class="menu-list">
          <view class="menu-item" v-for="m in menus" :key="m.page" @click="navTo(m.page)">
            <text class="menu-icon">{{ m.icon }}</text>
            <text class="menu-label">{{ m.text }}</text>
            <text v-if="m.badge" class="menu-badge">{{ m.badge }}</text>
            <text class="menu-arrow">›</text>
          </view>
        </view>
      </Card>

      <!-- 底部菜单 -->
      <Card title="其他">
        <view class="menu-list">
          <view class="menu-item" v-for="m in bottomMenus" :key="m.page" @click="navTo(m.page)">
            <text class="menu-icon">{{ m.icon }}</text>
            <text class="menu-label">{{ m.text }}</text>
            <text class="menu-arrow">›</text>
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
import TabBar from '@/components/TabBar/TabBar.vue'

const menus = [
  { text: '我的收藏', page: 'collections', icon: '❤️', badge: '5' },
  { text: '咨询历史', page: 'consult-history', icon: '💬' },
  { text: '健康档案', page: 'health-record', icon: '📋' },
  { text: '我的订单', page: 'order', icon: '📦' },
  { text: '消息中心', page: 'messages', icon: '💌', badge: '3' },
  { text: '会员中心', page: 'member', icon: '👑' },
]

const bottomMenus = [
  { text: '设置', page: 'settings', icon: '⚙️' },
]

const navTo = (page: string) => {
  const tabs = ['index','services','member','profile']
  if (tabs.includes(page)) {
    uni.switchTab({ url: `/pages/${page}/index` })
  } else {
    uni.navigateTo({ url: `/pages/${page}/index` })
  }
}
</script>

<style scoped>
.page { height: 100vh; display: flex; flex-direction: column; background: #F5F7FA; }
.scroll-body { height: calc(100vh - 56px - 56px - 16px); padding-top: 16px; }

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
