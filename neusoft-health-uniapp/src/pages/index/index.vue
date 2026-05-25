<template>
  <view class="page">
    <!-- 顶部蓝色区域 -->
    <view class="header-area">
      <view class="header-top">
        <view class="search-bar" @click="navTo('search')">
          <SvgIcon name="search" :size="18" color="#8F959E" />
          <text class="search-placeholder">搜索健康问题、症状...</text>
        </view>
      </view>

      <!-- AI 咨询卡片 -->
      <view class="ai-card" @click="navTo('health-assistant')">
        <view class="ai-card-inner">
          <view class="ai-icon-box"><SvgIcon name="robot" :size="28" color="#FFFFFF" /></view>
          <view class="ai-info">
            <text class="ai-title">AI健康助手</text>
            <text class="ai-desc">健康知识咨询，个性化建议</text>
          </view>
          <text class="ai-arrow">→</text>
        </view>
      </view>
    </view>

    <!-- 下方滚动区 -->
    <view class="content-area">
      <!-- 快捷入口 -->
      <Card>
        <view class="section-header">
          <text class="section-title">健康服务</text>
        </view>
        <view class="service-grid">
          <view v-for="s in services" :key="s.page" class="service-item" @click="navTo(s.page)">
            <view :class="['svc-icon-box', s.color]">
              <SvgIcon :name="s.icon" :size="24" color="#FFFFFF" />
            </view>
            <text class="svc-name">{{ s.name }}</text>
          </view>
        </view>
      </Card>

      <!-- FAQ分类 -->
      <Card v-if="homeData.faqCategories.length > 0">
        <view class="section-header">
          <text class="section-title">常见问题</text>
          <text class="section-more" @click="navTo('faq')">更多 ›</text>
        </view>
        <view class="faq-grid">
          <view
            v-for="cat in homeData.faqCategories"
            :key="cat.id"
            class="faq-card"
            @click="openCategory(cat)"
          >
            <view :class="['faq-icon-box', getCategoryColor(cat.name)]">
              <SvgIcon :name="getCategoryIcon(cat.name)" :size="26" color="#FFFFFF" />
            </view>
            <text class="faq-name">{{ cat.name }}</text>
            <text class="faq-count">{{ cat.faqList?.length || 0 }}条</text>
          </view>
        </view>
      </Card>

      <!-- 健康资讯 -->
      <Card v-if="homeData.articles.length > 0">
        <view class="section-header">
          <text class="section-title">健康资讯</text>
        </view>
        <view class="news-list">
          <view v-for="art in homeData.articles" :key="art.id" class="news-item" @click="openArticle(art)">
            <text class="news-title">{{ art.title }}</text>
            <text class="news-time">{{ formatTime(art.createdTime) }}</text>
          </view>
        </view>
      </Card>

      <!-- 免责声明 -->
      <Card>
        <view class="disclaimer">
          <SvgIcon name="warning" :size="20" color="#F5A623" />
          <text class="disclaimer-text">{{ homeData.disclaimer || '【健康咨询仅供参考，不能替代专业医生诊断和治疗】' }}</text>
        </view>
      </Card>

      <view style="height: 100px;" />
    </view>
    <TabBar current="index" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import Card from '@/components/Card/Card.vue'
import { systemApi } from '@/api/user'
import type { HomeData } from '@/types'

const homeData = ref<HomeData>({
  faqCategories: [],
  articles: [],
  disclaimer: ''
})

const services = [
  { name: '智能问诊', page: 'consult', icon: 'message', color: 'c-blue' },
  { name: '健康档案', page: 'health-record', icon: 'clipboard', color: 'c-green' },
  { name: '会员中心', page: 'member', icon: 'crown', color: 'c-gold' },
  { name: '我的订单', page: 'order', icon: 'package', color: 'c-red' },
]

const categoryIcons: Record<string, string> = {
  '感冒发烧': 'thermometer',
  '肠胃不适': 'droplet',
  '睡眠问题': 'moon',
  '运动健康': 'runner',
  '饮食营养': 'salad',
  '皮肤问题': 'lotion',
  '心理问题': 'brain',
  '妇科问题': 'woman',
  '儿科问题': 'baby',
  '老年健康': 'elder',
}

const categoryColors: Record<string, string> = {
  '感冒发烧': 'c-red',
  '肠胃不适': 'c-orange',
  '睡眠问题': 'c-purple',
  '运动健康': 'c-green',
  '饮食营养': 'c-teal',
  '皮肤问题': 'c-pink',
  '心理问题': 'c-blue',
  '妇科问题': 'c-rose',
  '儿科问题': 'c-yellow',
  '老年健康': 'c-gray',
}

const getCategoryIcon = (name: string) => {
  return categoryIcons[name] || 'question'
}

const getCategoryColor = (name: string) => {
  return categoryColors[name] || 'c-blue'
}

const formatTime = (time: string) => {
  if (!time) return ''
  return time.substring(5, 10)
}

const navTo = (page: string) => {
  const tabs = ['index', 'services', 'member', 'profile']
  if (tabs.includes(page)) {
    uni.switchTab({ url: `/pages/${page}/index` })
  } else {
    uni.navigateTo({ url: `/pages/${page}/index` })
  }
}

const openCategory = (cat: any) => {
  uni.navigateTo({ url: '/pages/faq/index?categoryId=' + cat.id })
}

const openArticle = (art: any) => {
  uni.showToast({ title: '查看文章功能开发中', icon: 'none' })
}

onMounted(async () => {
  try {
    const res = await systemApi.getHomeData()
    homeData.value = res.data
  } catch (err) {
    console.error('获取首页数据失败', err)
  }
})

onShow(() => {
  uni.pageScrollTo({ scrollTop: 0, duration: 0 })
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #F5F7FA;
}

/* 顶部蓝色区域 */
.header-area {
  background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
  padding: 16px 16px 24px;
  border-radius: 0 0 24px 24px;
}

.search-bar {
  background: rgba(255,255,255,0.95);
  border-radius: 50px;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.search-icon { font-size: 18px; }
.search-placeholder { font-size: 14px; color: #8F959E; flex: 1; }

.header-top {
  display: flex;
  align-items: center;
  gap: 12px;
}
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

/* 下方内容区 */
.content-area { padding: 12px 16px; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.section-title { font-size: 16px; font-weight: 600; color: #1F2329; }
.section-more { font-size: 13px; color: #4A90D9; }

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

/* FAQ 网格 */
.faq-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 12px 0;
}
.faq-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.faq-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.faq-icon { font-size: 26px; }
.faq-name { font-size: 14px; color: #1F2329; font-weight: 500; }
.faq-count { font-size: 12px; color: #8F959E; }

/* FAQ分类颜色 */
.c-orange { background: linear-gradient(135deg, #FF9F43, #F39C12); }
.c-purple { background: linear-gradient(135deg, #A55EEA, #8854D0); }
.c-teal { background: linear-gradient(135deg, #26C6DA, #00ACC1); }
.c-pink { background: linear-gradient(135deg, #FF6B6B, #F8B500); }
.c-rose { background: linear-gradient(135deg, #FF6B9D, #C44569); }
.c-yellow { background: linear-gradient(135deg, #FFD93D, #F9CA24); }
.c-gray { background: linear-gradient(135deg, #636E72, #2D3436); }

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

.disclaimer {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
}
.disclaimer-icon { font-size: 20px; }
.disclaimer-text {
  font-size: 12px;
  color: #8F959E;
  line-height: 1.6;
  flex: 1;
}
</style>