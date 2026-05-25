<template>
  <view class="page">
    <NavHeader title="搜索" showBack @back="goBack" handleBackBySelf="true" />

    <view class="search-section">
      <view class="search-bar">
        <SvgIcon name="search" :size="18" color="#8F959E" />
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

    <scroll-view scroll-y class="search-body" :scroll-top="scrollTop">
      <view v-if="!keyword && !result" class="hot-section">
        <text class="section-label">热门搜索</text>
        <view class="hot-tags">
          <text v-for="h in hotTags" :key="h" class="hot-tag" @click="searchTag(h)">{{ h }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading-state">
        <view class="loading-spinner" />
        <text class="loading-text">AI正在搜索健康知识...</text>
      </view>

      <view v-if="result && !loading" class="result-section">
        <text class="section-label">
          搜索结果: "{{ result.keyword }}"
          <text class="source-tag">{{ result.source === 2 ? '来源：知识库' : '来源：AI生成' }}</text>
        </text>
        <Card>
          <view class="result-content">
            <template v-for="(line, i) in contentLines" :key="i">
              <text v-if="line.type === 'h1'" class="md-h1">{{ line.text }}</text>
              <text v-if="line.type === 'h2'" class="md-h2">{{ line.text }}</text>
              <text v-if="line.type === 'h3'" class="md-h3">{{ line.text }}</text>
              <view v-if="line.type === 'li'" class="md-li">
                <text class="md-li-bullet">•</text>
                <text class="md-li-text">
                  <template v-for="(seg, j) in line.segments" :key="j">
                    <text v-if="seg.bold" class="md-bold">{{ seg.text }}</text>
                    <text v-else>{{ seg.text }}</text>
                  </template>
                </text>
              </view>
              <text v-if="line.type === 'empty'" class="md-empty" />
              <text v-if="line.type === 'p'" class="md-p">
                <template v-for="(seg, j) in line.segments" :key="j">
                  <text v-if="seg.bold" class="md-bold">{{ seg.text }}</text>
                  <text v-else>{{ seg.text }}</text>
                </template>
              </text>
            </template>
          </view>
        </Card>
        <view v-if="result.disclaimer" class="disclaimer">
          <text>{{ result.disclaimer }}</text>
        </view>
      </view>

      <view v-if="showEmpty" class="empty-hint">未找到相关结果</view>

      <view style="height: 40px;" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import { consultApi } from '@/api/consult'
import type { HealthSearchResult } from '@/types'
import { ref, watch, computed } from 'vue'

interface TextSegment {
  text: string
  bold: boolean
}

interface ContentLine {
  type: 'h1' | 'h2' | 'h3' | 'p' | 'li' | 'empty'
  text?: string
  segments?: TextSegment[]
}

const keyword = ref('')
const result = ref<HealthSearchResult | null>(null)
const loading = ref(false)
const showEmpty = ref(false)
const scrollTop = ref(0)

const hotTags = ['感冒', '高血压', '失眠', '糖尿病', '过敏', '颈椎病', '减肥', '中医养生']

const contentLines = computed<ContentLine[]>(() => {
  if (!result.value) return []
  return parseMarkdown(result.value.content || '')
})

const searchTag = (tag: string) => {
  keyword.value = tag
  doSearch()
}

const doSearch = () => {
  const kw = keyword.value.trim()
  if (!kw) return

  loading.value = true
  result.value = null
  showEmpty.value = false

  consultApi.healthSearch(kw).then((res) => {
    loading.value = false
    if (res.data) {
      result.value = res.data
      showEmpty.value = false
    } else {
      showEmpty.value = true
    }
  }).catch(() => {
    loading.value = false
    showEmpty.value = true
  })
}

function parseLineSegments(text: string): TextSegment[] {
  const segments: TextSegment[] = []
  const re = /(\*\*(.+?)\*\*)/g
  let lastIndex = 0
  let match: RegExpExecArray | null
  while ((match = re.exec(text)) !== null) {
    if (match.index > lastIndex) {
      segments.push({ text: text.slice(lastIndex, match.index), bold: false })
    }
    segments.push({ text: match[2], bold: true })
    lastIndex = match.index + match[0].length
  }
  if (lastIndex < text.length) {
    segments.push({ text: text.slice(lastIndex), bold: false })
  }
  return segments
}

function parseMarkdown(md: string): ContentLine[] {
  const rawLines = md.split('\n')
  return rawLines
    .map((raw) => raw.trimEnd())
    .filter((raw) => raw !== '\r')
    .map((raw): ContentLine | null => {
      const trimmed = raw.trim()
      if (!trimmed) return { type: 'empty' }

      if (/^###\s/.test(trimmed)) {
        return { type: 'h3', text: trimmed.replace(/^###\s*/, '') }
      }
      if (/^##\s/.test(trimmed)) {
        return { type: 'h2', text: trimmed.replace(/^##\s*/, '') }
      }
      if (/^#\s/.test(trimmed)) {
        return { type: 'h1', text: trimmed.replace(/^#\s*/, '') }
      }

      if (/^[-*]\s/.test(trimmed)) {
        const liText = trimmed.replace(/^[-*]\s*/, '')
        return { type: 'li', segments: parseLineSegments(liText) }
      }
      if (/^\d+\.\s/.test(trimmed)) {
        const liText = trimmed.replace(/^\d+\.\s*/, '')
        return { type: 'li', segments: parseLineSegments(liText) }
      }

      return { type: 'p', segments: parseLineSegments(trimmed) }
    })
    .filter((line): line is ContentLine => line !== null)
}

watch(keyword, (v) => {
  if (!v) {
    result.value = null
    showEmpty.value = false
  }
})

const goBack = () => {
  const pages = getCurrentPages()
  if (pages.length <= 1) {
    uni.switchTab({ url: '/pages/index/index' })
  } else {
    uni.navigateBack()
  }
}
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
.source-tag { font-size: 11px; color: #4A90D9; margin-left: 8px; }

.hot-section { padding-bottom: 12px; }
.hot-tags { padding: 0 16px; display: flex; flex-wrap: wrap; gap: 10px; }
.hot-tag { padding: 8px 16px; background: #FFFFFF; border-radius: 20px; font-size: 13px; color: #646A73; border: 1px solid #E5E6EB; }

.loading-state { display: flex; flex-direction: column; align-items: center; padding: 60px 20px; }
.loading-spinner { width: 36px; height: 36px; border: 3px solid #F0F1F5; border-top: 3px solid #4A90D9; border-radius: 50%; animation: spin 0.8s linear infinite; }
.loading-text { margin-top: 12px; font-size: 14px; color: #8F959E; }

.result-content { padding: 4px 0; }

.md-h1 { font-size: 18px; font-weight: 700; color: #1F2329; display: block; margin: 16px 0 8px; line-height: 1.5; }
.md-h2 { font-size: 16px; font-weight: 700; color: #1F2329; display: block; margin: 14px 0 6px; line-height: 1.5; }
.md-h3 { font-size: 15px; font-weight: 700; color: #1F2329; display: block; margin: 12px 0 4px; line-height: 1.5; }

.md-p { font-size: 14px; color: #1F2329; line-height: 1.8; display: block; margin-bottom: 6px; }

.md-li { display: flex; flex-direction: row; margin-bottom: 4px; padding-left: 4px; }
.md-li-bullet { font-size: 14px; color: #4A90D9; margin-right: 8px; line-height: 1.8; flex-shrink: 0; }
.md-li-text { font-size: 14px; color: #1F2329; line-height: 1.8; flex: 1; }

.md-bold { font-weight: 700; color: #1F2329; }

.md-empty { display: block; height: 8px; }

.disclaimer { margin: 12px 16px 0; padding: 12px; background: rgba(74,144,217,0.06); border-radius: 8px; }
.disclaimer text { font-size: 12px; color: #8F959E; line-height: 1.6; }

.empty-hint { text-align: center; padding: 60px 20px; color: #BBBFC4; font-size: 14px; }

@keyframes spin { to { transform: rotate(360deg); } }
</style>
