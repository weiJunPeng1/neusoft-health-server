<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-aside">
      <div class="logo-container">
        <img src="@/assets/vue.svg" alt="Logo" class="logo-img" />
        <span v-show="!isCollapse" class="logo-text">东软智慧健康</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="currentRoute"
          :collapse="isCollapse"
          :router="true"
          background-color="#001529"
          text-color="#ffffffa6"
          active-text-color="#1890ff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Monitor /></el-icon>
            <span>工作台</span>
          </el-menu-item>
          <el-menu-item v-if="authStore.hasAnyRole(['R002', 'R003'])" index="/screen" @click="openScreen">
            <el-icon><DataLine /></el-icon>
            <span>数据大屏</span>
          </el-menu-item>
          <el-menu-item v-if="authStore.hasAnyRole(['R002', 'R003'])" index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item v-if="authStore.hasAnyRole(['R002', 'R004'])" index="/review">
            <el-icon><Document /></el-icon>
            <span>内容审核</span>
          </el-menu-item>
          <el-menu-item v-if="authStore.hasAnyRole(['R002', 'R003'])" index="/members">
            <el-icon><UserFilled /></el-icon>
            <span>会员管理</span>
          </el-menu-item>
          <el-menu-item v-if="authStore.hasAnyRole(['R002', 'R003'])" index="/orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-sub-menu v-if="authStore.hasAnyRole(['R002', 'R004'])" index="content">
            <template #title>
              <el-icon><Files /></el-icon>
              <span>内容管理</span>
            </template>
            <el-menu-item index="/content/faq">常见问题</el-menu-item>
            <el-menu-item index="/content/sensitive-words">敏感词管理</el-menu-item>
          </el-sub-menu>
          <el-menu-item v-if="authStore.hasAnyRole(['R002', 'R003'])" index="/config">
            <el-icon><Setting /></el-icon>
            <span>系统配置</span>
          </el-menu-item>
          <el-sub-menu v-if="authStore.hasAnyRole(['R002', 'R003'])" index="permission">
            <template #title>
              <el-icon><Lock /></el-icon>
              <span>权限管理</span>
            </template>
            <el-menu-item index="/permission/roles">角色管理</el-menu-item>
            <el-menu-item index="/permission/permissions">权限配置</el-menu-item>
          </el-sub-menu>
          <el-menu-item v-if="authStore.hasAnyRole(['R002', 'R003'])" index="/logs">
            <el-icon><Notebook /></el-icon>
            <span>操作日志</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="authStore.user?.avatarUrl || ''">
                {{ authStore.user?.nickname?.charAt(0) || 'A' }}
              </el-avatar>
              <span class="username">{{ authStore.user?.nickname || '管理员' }}</span>
              <el-tag v-if="authStore.isSuperAdmin()" type="danger" size="small" effect="dark">超管</el-tag>
              <el-tag v-else-if="authStore.isSystemAdmin()" type="primary" size="small" effect="dark">系统管理员</el-tag>
              <el-tag v-else-if="authStore.isContentReviewer()" type="warning" size="small" effect="dark">审核员</el-tag>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <keep-alive :max="10">
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  Monitor,
  DataLine,
  User,
  Document,
  UserFilled,
  ShoppingCart,
  Files,
  Setting,
  Lock,
  Notebook,
  Fold,
  Expand
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)

const currentRoute = computed(() => route.path)

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(
    (item) => item.meta && item.meta.title && item.path !== '/'
  )
  return matched.map((item) => ({
    path: item.path,
    title: item.meta.title as string
  }))
})

function openScreen() {
  window.open('/screen', '_blank')
}

function handleCommand(command: string) {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
}

.admin-aside {
  background: #001529;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-img {
  width: 32px;
  height: 32px;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  margin-left: 12px;
  white-space: nowrap;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 24px;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #333;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #333;
}

.admin-main {
  background: #f0f2f5;
  padding: 24px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
