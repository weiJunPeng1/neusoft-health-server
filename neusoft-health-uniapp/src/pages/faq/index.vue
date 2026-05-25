<template>
  <view class="page">
    <NavHeader :title="categoryName" showBack @back="goBack" handleBackBySelf="true" />
    <scroll-view scroll-y class="scroll-body" :scroll-top="scrollTop">
      <!-- 分类列表视图 -->
      <template v-if="!currentCategoryId">
        <Card v-for="(item, i) in items" :key="i">
          <view class="list-row" @click="showCategoryFaqs(item)">
            <view class="list-left">
              <text class="list-title">{{ item.name }}</text>
              <text v-if="item.icon" class="list-sub">{{ item.icon }}</text>
            </view>
            <text v-if="item.faqList?.length" class="list-extra">{{ item.faqList.length }}条</text>
            <text class="list-arrow">›</text>
          </view>
        </Card>
        <view v-if="items.length === 0" class="empty-hint">
          <SvgIcon name="question" :size="48" color="#BBBFC4" />
          <text class="empty-text">暂无分类</text>
        </view>
      </template>

      <!-- FAQ详情视图 -->
      <template v-else>
        <Card v-for="(faq, i) in faqList" :key="i">
          <view class="faq-item">
            <text class="faq-question">{{ faq.question }}</text>
            <text class="faq-answer">{{ faq.presetAnswer || '暂无详细解答，请咨询AI健康助手' }}</text>
          </view>
        </Card>
        <view v-if="faqList.length === 0" class="empty-hint">
          <SvgIcon name="inbox" :size="48" color="#BBBFC4" />
          <text class="empty-text">该分类暂无FAQ</text>
        </view>
      </template>

      <view style="height: 40px;" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import { ref, onMounted } from 'vue'
import { useScrollToTop } from '@/composables/useScrollToTop'
import { consultApi } from '@/api/consult'
import type { FaqCategory, FaqItem } from '@/types'

const { scrollTop } = useScrollToTop()

const items = ref<FaqCategory[]>([])
const faqList = ref<FaqItem[]>([])
const currentCategoryId = ref<number | null>(null)
const categoryName = ref('常见问题')
const isDirectEntry = ref(false)

const goBack = () => {
  if (currentCategoryId.value && !isDirectEntry.value) {
    currentCategoryId.value = null
    categoryName.value = '常见问题'
  } else {
    const pages = getCurrentPages()
    if (pages.length <= 1) {
      uni.switchTab({ url: '/pages/services/index' })
    } else {
      uni.navigateBack()
    }
  }
}

const showCategoryFaqs = async (item: FaqCategory) => {
  currentCategoryId.value = item.id
  categoryName.value = item.name
  // 从items中获取该分类的FAQ（已经在首页数据中包含了）
  const category = items.value.find(c => c.id === item.id)
  if (category && category.faqList) {
    faqList.value = category.faqList
  } else {
    // 备用：从API获取
    try {
      const res = await consultApi.getFaqsByCategory(item.id)
      faqList.value = res.data || []
    } catch (err) {
      console.error('获取FAQ列表失败', err)
    }
  }
}

onMounted(async () => {
  try {
    // 获取URL参数
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const options = (currentPage as any).options || {}
    
    const res = await consultApi.getFaqCategories()
    items.value = res.data || []
    
    // 如果URL中传入了categoryId，直接显示该分类的FAQ
    if (options.categoryId) {
      isDirectEntry.value = true
      const categoryId = parseInt(options.categoryId)
      const category = items.value.find(c => c.id === categoryId)
      if (category) {
        showCategoryFaqs(category)
      }
    }
  } catch (err) {
    console.error('获取FAQ分类失败', err)
  }
})
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }
.scroll-body { padding-top: 12px; }
.list-row { display: flex; align-items: center; gap: 12px; }
.list-left { flex: 1; }
.list-title { font-size: 15px; color: #1F2329; font-weight: 500; display: block; }
.list-sub { font-size: 12px; color: #8F959E; margin-top: 2px; display: block; }
.list-extra { font-size: 12px; color: #4A90D9; }
.list-arrow { font-size: 18px; color: #BBBFC4; }

.faq-item { padding: 16px 0; border-bottom: 1px solid #F5F5F5; }
.faq-item:last-child { border-bottom: none; }
.faq-question { font-size: 15px; color: #1F2329; font-weight: 500; display: block; margin-bottom: 12px; }
.faq-answer { font-size: 14px; color: #646A73; line-height: 1.8; display: block; }

.empty-hint { padding: 80px 0; text-align: center; }
.empty-icon { font-size: 48px; display: block; margin-bottom: 12px; }
.empty-text { font-size: 14px; color: #BBBFC4; }
</style>