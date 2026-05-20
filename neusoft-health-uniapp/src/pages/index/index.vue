<template>
  <view class="page">
    <NavHeader title="智慧健康" theme="blue" :showBack="false">
      <template #right>
        <view class="avatar-btn" @click="navTo('profile')">
          <text class="avatar-text">👤</text>
        </view>
      </template>
    </NavHeader>

    <scroll-view scroll-y class="page-scroll">
      <!-- 顶部蓝色区域：搜索 + AI入口 -->
      <view class="header-area">
        <view class="search-bar" @click="navTo('search')">
          <text class="search-icon">🔍</text>
          <text class="search-placeholder">搜索健康问题、症状、疾病...</text>
        </view>

        <view class="ai-card" @click="navTo('health-assistant')">
          <view class="ai-card-inner">
            <view class="ai-icon-box">🤖</view>
            <view class="ai-info">
              <text class="ai-title">AI健康助手</text>
              <text class="ai-desc">健康报告解读、个性化建议</text>
            </view>
            <text class="ai-arrow">→</text>
          </view>
        </view>
      </view>

      <!-- 下方内容区 -->
      <view class="content-area">
        <Card title="健康服务">
          <view class="service-grid">
            <view class="service-item" v-for="svc in services" :key="svc.name" @click="navTo(svc.page)">
              <view :class="['svc-icon-box', svc.color]">
                <text class="svc-icon">{{ svc.icon }}</text>
              </view>
              <text class="svc-name">{{ svc.name }}</text>
            </view>
          </view>
        </Card>

        <Card title="健康资讯" extra="更多" @extra="navTo('news')">
          <view class="news-list">
            <view class="news-item" v-for="n in news" :key="n.id" @click="navTo('news')">
              <text class="news-title">{{ n.title }}</text>
              <text class="news-time">{{ n.time }}</text>
            </view>
          </view>
        </Card>

        <view class="quick-links">
          <view class="quick-item" v-for="q in quickLinks" :key="q.page" @click="navTo(q.page)">
            <text class="quick-icon">{{ q.icon }}</text>
            <text class="quick-text">{{ q.text }}</text>
          </view>
        </view>

        <view style="height: 80px;" />
      </view>
    </scroll-view>

    <TabBar current="index" />
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import TabBar from '@/components/TabBar/TabBar.vue'
import { reactive } from 'vue'

const services = [
  { name: '智能问诊', page: 'consult', icon: '💬', color: 'c-blue' },
  { name: '健康档案', page: 'health-record', icon: '📋', color: 'c-green' },
  { name: '会员中心', page: 'member', icon: '👑', color: 'c-gold' },
  { name: '我的订单', page: 'order', icon: '📦', color: 'c-red' },
]

const news = reactive([
  { id: 1, title: '夏季防暑降温小贴士', time: '05-20' },
  { id: 2, title: '如何正确测量血压？', time: '05-19' },
  { id: 3, title: '健康饮食金字塔最新指南', time: '05-18' },
])

const quickLinks = [
  { text: '药品查询', page: 'medicine-search', icon: '💊' },
  { text: '常见问题', page: 'faq', icon: '❓' },
  { text: '报告解读', page: 'health-report', icon: '📄' },
  { text: '消息通知', page: 'notification', icon: '🔔' },
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
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #F5F7FA;
}
.page-scroll { flex: 1; }

/* 顶部蓝色区域 */
.header-area {
  background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
  padding: 16px 16px 24px;
  border-radius: 0 0 24px 24px;
}

.search-bar {
  background: rgba(255,255,255,0.95);
  border-radius: 25px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}
.search-icon { font-size: 18px; margin-right: 10px; opacity: 0.5; }
.search-placeholder { font-size: 14px; color: #BBBFC4; }

.ai-card {
  margin-top: 16px;
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 30px rgba(102,126,234,0.35);
}
.ai-card-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}
.ai-icon-box {
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}
.ai-info { flex: 1; }
.ai-title { font-size: 17px; font-weight: 600; color: #FFFFFF; display: block; }
.ai-desc { font-size: 13px; color: rgba(255,255,255,0.8); margin-top: 4px; display: block; }
.ai-arrow { font-size: 20px; color: rgba(255,255,255,0.6); }

.avatar-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-text { font-size: 18px; }

/* 下方内容区 */
.content-area { padding-top: 12px; }

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 8px 0;
}
.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.svc-icon-box {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.svc-icon { font-size: 24px; }
.c-blue { background: linear-gradient(135deg, #4A90D9, #357ABD); }
.c-green { background: linear-gradient(135deg, #52C41A, #3CB371); }
.c-gold { background: linear-gradient(135deg, #FAAD14, #FFD700); }
.c-red { background: linear-gradient(135deg, #FF6B6B, #FF4757); }
.svc-name { font-size: 12px; color: #646A73; }

.news-list { padding: 4px 0; }
.news-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #F5F5F5;
}
.news-item:last-child { border-bottom: none; }
.news-title { font-size: 14px; color: #1F2329; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.news-time { font-size: 12px; color: #BBBFC4; margin-left: 12px; }

.quick-links {
  display: flex;
  gap: 12px;
  padding: 0 16px;
  margin-top: 4px;
}
.quick-item {
  flex: 1;
  background: #FFFFFF;
  border-radius: 12px;
  padding: 14px 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.quick-icon { font-size: 22px; }
.quick-text { font-size: 11px; color: #646A73; }
</style>
