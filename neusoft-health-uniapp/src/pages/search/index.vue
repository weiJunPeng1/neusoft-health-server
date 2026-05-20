<template>
  <view class="page">
    <NavHeader title="搜索" showBack @back="goBack" />

    <view class="search-section">
      <view class="search-bar">
        <text class="search-icon">🔍</text>
        <input
          v-model="keyword"
          class="search-input"
          placeholder="搜索健康问题、症状、药品..."
          :adjust-position="false"
          @confirm="doSearch"
        />
        <text v-if="keyword" class="search-clear" @click="keyword = ''">✕</text>
      </view>
    </view>

    <scroll-view scroll-y class="search-body">
      <!-- 热门搜索 -->
      <view v-if="!keyword" class="hot-section">
        <text class="section-label">热门搜索</text>
        <view class="hot-tags">
          <text v-for="h in hotTags" :key="h" class="hot-tag" @click="keyword = h">{{ h }}</text>
        </view>
      </view>

      <!-- 搜索结果 -->
      <view v-if="keyword" class="result-section">
        <text class="section-label">搜索结果: "{{ keyword }}"</text>
        <Card v-for="(r, i) in results" :key="i">
          <text class="result-title">{{ r.title }}</text>
          <text class="result-desc">{{ r.desc }}</text>
        </Card>
        <view v-if="keyword && results.length === 0" class="empty-hint">未找到相关结果</view>
      </view>

      <view style="height: 40px;" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import { ref, watch } from 'vue'

const keyword = ref('')
const results = ref<Array<{title: string; desc: string}>>([])

const hotTags = ['感冒', '高血压', '失眠', '糖尿病', '过敏', '颈椎病', '减肥', '中医养生']

const doSearch = () => {
  if (!keyword.value.trim()) return
  results.value = [
    { title: `${keyword.value}的症状与诊断`, desc: `${keyword.value}常见症状包括...，建议及时就医。` },
    { title: `${keyword.value}的家庭护理方法`, desc: `针对${keyword.value}，您可以尝试以下家庭护理...` },
    { title: `${keyword.value}的饮食注意事项`, desc: `患有${keyword.value}时，饮食方面需要注意...` },
  ]
}

watch(keyword, (v) => {
  if (!v) results.value = []
})

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }

.search-section { padding: 16px; background: #FFFFFF; border-bottom: 1px solid #F0F1F5; }
.search-bar { display: flex; align-items: center; gap: 10px; background: #F5F7FA; border-radius: 12px; padding: 10px 14px; }
.search-icon { font-size: 18px; }
.search-input { flex: 1; font-size: 14px; background: transparent; }
.search-clear { font-size: 16px; color: #BBBFC4; padding: 4px; }

.search-body { padding-top: 12px; }
.section-label { font-size: 13px; color: #8F959E; padding: 0 16px 12px; display: block; }

.hot-section { padding-bottom: 12px; }
.hot-tags { padding: 0 16px; display: flex; flex-wrap: wrap; gap: 10px; }
.hot-tag { padding: 8px 16px; background: #FFFFFF; border-radius: 20px; font-size: 13px; color: #646A73; border: 1px solid #E5E6EB; }

.result-title { font-size: 15px; font-weight: 500; color: #1F2329; display: block; margin-bottom: 4px; }
.result-desc { font-size: 13px; color: #8F959E; line-height: 1.5; display: block; }

.empty-hint { text-align: center; padding: 40px; color: #BBBFC4; font-size: 14px; }
</style>
