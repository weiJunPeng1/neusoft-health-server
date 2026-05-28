<template>
  <div class="data-screen" ref="screenRef">
    <ParticleNetwork :particle-count="80" :connection-distance="140" :move-speed="0.3" />
    <div class="screen-header">
      <div class="header-left">
        <span class="header-date">{{ currentDate }}</span>
      </div>
      <h1 class="header-title">东软智慧健康 · 数据运营中心</h1>
      <div class="header-right">
        <span class="header-time">{{ currentTime }}</span>
        <button class="fullscreen-btn" @click="toggleFullscreen">
          {{ isFullscreen ? '退出全屏' : '全屏' }}
        </button>
      </div>
    </div>

    <div class="screen-body">
      <div class="kpi-row">
          <div class="kpi-card" v-for="(item, idx) in kpiCards" :key="item.label" :style="{ animationDelay: idx * 80 + 'ms' }">
            <div class="kpi-top">
              <span class="kpi-label">
                <span class="kpi-icon" :style="{color: item.color}">●</span>
                {{ item.label }}
              </span>
            </div>
            <div class="kpi-value">
              <span class="flip-num" v-for="(ch, ci) in formatNumber(item.value).split('')" :key="ci">
                <span class="flip-digit" :style="{ animationDelay: (idx * 80 + ci * 50) + 'ms' }">{{ ch }}</span>
              </span>
            </div>
            <div class="kpi-desc">{{ item.desc }}</div>
          </div>
        </div>

      <div class="grid-row">
        <div class="col col-left anim-panel" style="animation-delay: 200ms">
          <div class="panel">
            <div class="panel-header">
              <span class="panel-title">用户增长趋势</span>
            </div>
            <div class="panel-body">
              <div ref="trendChartRef" class="chart-box"></div>
            </div>
          </div>
          <div class="panel">
            <div class="panel-header">
              <span class="panel-title">健康咨询分类</span>
            </div>
            <div class="panel-body">
              <div ref="categoryChartRef" class="chart-box"></div>
            </div>
          </div>
        </div>

        <div class="col col-center anim-panel" style="animation-delay: 350ms">
          <div class="panel panel-member">
            <div class="panel-header">
              <span class="panel-title">会员等级分布</span>
            </div>
            <div class="panel-body">
              <div ref="memberChartRef" class="chart-box"></div>
            </div>
          </div>
          <div class="col-center-bottom">
            <div class="panel">
              <div class="panel-header">
                <span class="panel-title">AI性能指标</span>
              </div>
              <div class="panel-body">
                <div ref="gaugeChartRef" class="chart-box"></div>
              </div>
            </div>
            <div class="panel">
              <div class="panel-header">
                <span class="panel-title">咨询量趋势</span>
              </div>
              <div class="panel-body">
                <div ref="consultChartRef" class="chart-box"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="col col-right anim-panel" style="animation-delay: 500ms">
          <div class="panel panel-bar">
            <div class="panel-header">
              <span class="panel-title">咨询量趋势</span>
            </div>
            <div class="panel-body">
              <div ref="consultTrendChartRef" class="chart-box"></div>
            </div>
          </div>
          <div class="panel panel-live">
            <div class="panel-header">
              <span class="panel-title">
                <span class="live-dot"></span>
                实时咨询动态
              </span>
              <span class="live-count">{{ recentConsults.length }} 条</span>
            </div>
            <div class="panel-body">
              <div class="live-scroll" :style="{ '--scroll-duration': (recentConsults.length * 1.5) + 's' }">
                <div class="live-scroll-inner">
                  <div
                    v-for="(item, idx) in [...recentConsults, ...recentConsults]"
                    :key="'live-' + idx"
                    class="live-item"
                  >
                    <div class="live-avatar">{{ item.userId }}</div>
                    <div class="live-info">
                      <div class="live-msg">{{ item.content }}</div>
                    </div>
                    <div class="live-time">{{ formatTime(item.createdAt) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="panel panel-rank">
            <div class="panel-header">
              <span class="panel-title">热门咨询排行</span>
            </div>
            <div class="panel-body">
              <div class="rank-scroll">
                <div v-for="(item, index) in hotConsults" :key="index" class="rank-item">
                  <span :class="['rank-no', index < 3 ? 'r' + (index + 1) : 'r4']">{{ index + 1 }}</span>
                  <span class="rank-name">{{ item.keyword }}</span>
                  <span class="rank-bar-w">
                    <span class="rank-bar" :style="{ width: getRankWidth(item.hitCount) }"></span>
                  </span>
                  <span class="rank-val">{{ item.hitCount }}次</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '@/api/admin'
import type { DashboardStats, TrendData, HotConsult, RecentConsult } from '@/api/admin'
import ParticleNetwork from './ParticleNetwork.vue'

const screenRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()
const memberChartRef = ref<HTMLElement>()
const gaugeChartRef = ref<HTMLElement>()
const consultChartRef = ref<HTMLElement>()
const consultTrendChartRef = ref<HTMLElement>()

const isFullscreen = ref(false)
const stats = ref<DashboardStats>({
  totalUsers: 0,
  todayUsers: 0,
  totalSessions: 0,
  totalMessages: 0,
  todayMessages: 0,
  todaySessions: 0,
  pendingReviews: 0,
  emergencyCount: 0,
  categoryStats: [],
  memberStats: { totalMembers: 0, L0: 0, L1: 0, L2: 0, L3: 0 },
  hotConsults: [],
  recentConsults: []
})

const userTrend = ref<TrendData[]>([])
const consultTrend = ref<TrendData[]>([])
const hotConsults = ref<HotConsult[]>([])
const recentConsults = ref<RecentConsult[]>([])
const aiPerformance = ref({ avgResponseTime: 0, maxResponseTime: 0, minResponseTime: 0, totalAiCalls: 0 })

const currentDate = ref('')
const currentTime = ref('')
let timeInterval: ReturnType<typeof setInterval>
let dataInterval: ReturnType<typeof setInterval>

const charts: echarts.ECharts[] = []
const animatedValues = ref<number[]>([0, 0, 0, 0, 0, 0])
let animFrameId: number | null = null
let chartsInitialized = false
const chartInstances: Record<string, echarts.ECharts> = {}

const kpiTargets = computed(() => [
  stats.value.totalUsers,
  stats.value.todayUsers,
  stats.value.totalMessages,
  stats.value.todayMessages,
  stats.value.pendingReviews,
  stats.value.emergencyCount
])

function animateNumbers() {
  if (animFrameId) cancelAnimationFrame(animFrameId)
  const startValues = [...animatedValues.value]
  const endValues = kpiTargets.value.map(v => v || 0)
  const duration = 1200
  const startTime = performance.now()

  function tick(now: number) {
    const elapsed = now - startTime
    const progress = Math.min(elapsed / duration, 1)
    const ease = 1 - Math.pow(1 - progress, 3)
    for (let i = 0; i < 6; i++) {
      animatedValues.value[i] = Math.round(startValues[i] + (endValues[i] - startValues[i]) * ease)
    }
    if (progress < 1) {
      animFrameId = requestAnimationFrame(tick)
    }
  }
  animFrameId = requestAnimationFrame(tick)
}

const kpiCards = computed(() => [
  { label: '累计用户', value: animatedValues.value[0], color: '#00d4ff', desc: '系统注册用户总数' },
  { label: '今日新增', value: animatedValues.value[1], color: '#00ff88', desc: '今日新注册用户' },
  { label: '总咨询量', value: animatedValues.value[2], color: '#ffb400', desc: '累计咨询消息数' },
  { label: '今日咨询', value: animatedValues.value[3], color: '#a855f7', desc: '今日咨询消息数' },
  { label: '待审核', value: animatedValues.value[4], color: '#ff4d5a', desc: '待审核消息数' },
  { label: '紧急情况', value: animatedValues.value[5], color: '#ff6b6b', desc: '紧急咨询总数' }
])

function formatNumber(num: number): string {
  if (num >= 10000) return (num / 10000).toFixed(1) + '万'
  return num.toLocaleString()
}

function formatTime(time: string): string {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString('zh-CN')
}

function getRankWidth(count: number): string {
  const max = hotConsults.value[0]?.hitCount || 1
  return (count / max * 100).toFixed(0) + '%'
}

function updateTime() {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit', weekday: 'long'
  })
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
}

function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

function getOrCreateChart(name: string, el: HTMLElement | undefined): echarts.ECharts | null {
  if (!el) return null
  if (chartInstances[name]) return chartInstances[name]
  const chart = echarts.init(el)
  chartInstances[name] = chart
  charts.push(chart)
  return chart
}

function updateTrendChart(data: TrendData[]) {
  const chart = getOrCreateChart('trend', trendChartRef.value)
  if (!chart) return
  chart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    grid: { top: 30, right: 16, bottom: 28, left: 42 },
    xAxis: {
      type: 'category', data: data.map(d => d.date),
      axisLabel: { color: '#8ba7c7', fontSize: 10 },
      axisLine: { lineStyle: { color: '#1a3a5c' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#8ba7c7', fontSize: 10 },
      splitLine: { lineStyle: { color: 'rgba(0,145,255,0.1)' } }
    },
    series: [{
      type: 'line', data: data.map(d => d.count), smooth: true, symbol: 'none',
      lineStyle: { color: '#00d4ff', width: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(0,212,255,0.3)' },
          { offset: 1, color: 'rgba(0,212,255,0.02)' }
        ])
      }
    }]
  })
}

function updateCategoryChart() {
  const chart = getOrCreateChart('category', categoryChartRef.value)
  if (!chart) return
  const data = stats.value.categoryStats || []
  const colors = ['#00d4ff', '#00ff88', '#ffb400', '#a855f7', '#ff4d5a', '#36cfc9', '#f97316', '#ec4899', '#8b5cf6', '#14b8a6', '#6366f1', '#ef4444', '#84cc16', '#eab308']
  const total = data.reduce((s, d) => s + d.value, 0) || 1
  chart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 8,
      top: 'middle',
      textStyle: { color: '#8ba7c7', fontSize: 10 },
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 6
    },
    series: [{
      type: 'pie',
      radius: ['35%', '65%'],
      center: ['38%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 4, borderColor: 'rgba(6,30,65,0.8)', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 12, fontWeight: 'bold', color: '#fff' },
        itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' }
      },
      data: data.slice(0, 10).map((item, index) => ({
        name: item.name,
        value: item.value,
        itemStyle: { color: colors[index % colors.length] }
      }))
    }]
  })
}

function updateMemberChart() {
  const chart = getOrCreateChart('member', memberChartRef.value)
  if (!chart) return
  const m = stats.value.memberStats
  chart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'vertical', right: 20, top: 'center',
      textStyle: { color: '#8ba7c7', fontSize: 11 }
    },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['40%', '50%'],
      avoidLabelOverlap: false, label: { show: false },
      data: [
        { value: m.L0, name: 'L0 普通用户', itemStyle: { color: '#6B7280' } },
        { value: m.L1, name: 'L1 基础会员', itemStyle: { color: '#3B82F6' } },
        { value: m.L2, name: 'L2 高级会员', itemStyle: { color: '#F59E0B' } },
        { value: m.L3, name: 'L3 专业会员', itemStyle: { color: '#EAB308' } }
      ]
    }]
  })
}

function updateGaugeChart() {
  const chart = getOrCreateChart('gauge', gaugeChartRef.value)
  if (!chart) return
  const avgTime = (aiPerformance.value.avgResponseTime || 0) / 1000
  chart.setOption({
    backgroundColor: 'transparent',
    series: [{
      type: 'gauge', center: ['50%', '55%'], radius: '80%',
      startAngle: 200, endAngle: -20, min: 0, max: 5,
      axisLine: {
        lineStyle: {
          width: 15,
          color: [[0.4, '#00ff88'], [0.7, '#ffb400'], [1, '#ff4d5a']]
        }
      },
      pointer: { itemStyle: { color: '#e0ecff' } },
      axisTick: { show: false }, splitLine: { show: false }, axisLabel: { show: false },
      detail: {
        valueAnimation: true, formatter: '{value}s', color: '#00d4ff',
        fontSize: 20, fontWeight: 700, offsetCenter: [0, '40%']
      },
      title: { offsetCenter: [0, '70%'], color: '#8ba7c7', fontSize: 12 },
      data: [{ value: avgTime.toFixed(1), name: '平均响应' }]
    }]
  })
}

function updateConsultChart(data: TrendData[]) {
  const chart = getOrCreateChart('consult', consultChartRef.value)
  if (!chart) return
  chart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['总会话', '总消息'], textStyle: { color: '#8ba7c7', fontSize: 11 },
      top: 0, right: 0, itemWidth: 12, itemHeight: 8
    },
    grid: { top: 30, right: 16, bottom: 28, left: 42 },
    xAxis: {
      type: 'category', data: data.map(d => d.date),
      axisLabel: { color: '#8ba7c7', fontSize: 9, interval: 4 },
      axisLine: { lineStyle: { color: '#1a3a5c' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#8ba7c7', fontSize: 10 },
      splitLine: { lineStyle: { color: 'rgba(0,145,255,0.1)' } }
    },
    series: [
      {
        name: '总会话', type: 'line', smooth: true, showSymbol: false,
        lineStyle: { color: '#00d4ff', width: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(0,212,255,0.2)' },
            { offset: 1, color: 'rgba(0,212,255,0.01)' }
          ])
        },
        data: data.map(d => d.sessions)
      },
      {
        name: '总消息', type: 'line', smooth: true, showSymbol: false,
        lineStyle: { color: '#00ff88', width: 1.5 },
        data: data.map(d => d.messages)
      }
    ]
  })
}

function updateConsultTrendChart(data: TrendData[]) {
  const chart = getOrCreateChart('consultTrend', consultTrendChartRef.value)
  if (!chart) return
  chart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    grid: { top: 20, right: 16, bottom: 28, left: 42 },
    xAxis: {
      type: 'category', data: data.map(d => d.date),
      axisLabel: { color: '#8ba7c7', fontSize: 9, interval: 4 },
      axisLine: { lineStyle: { color: '#1a3a5c' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#8ba7c7', fontSize: 10 },
      splitLine: { lineStyle: { color: 'rgba(0,145,255,0.1)' } }
    },
    series: [{
      type: 'bar', data: data.map(d => d.sessions),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#00d4ff' },
          { offset: 1, color: 'rgba(0,212,255,0.2)' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
}

async function fetchAllData() {
  adminApi.getDashboardStats().then(data => {
    stats.value = data
    hotConsults.value = data.hotConsults || []
    recentConsults.value = data.recentConsults || []
    nextTick(() => {
      updateCategoryChart()
      updateMemberChart()
      animateNumbers()
    })
  }).catch(() => {})

  adminApi.getUserTrend(30).then(data => {
    userTrend.value = data
    nextTick(() => updateTrendChart(data))
  }).catch(() => {})

  adminApi.getConsultTrend(30).then(data => {
    consultTrend.value = data
    nextTick(() => {
      updateConsultChart(data)
      updateConsultTrendChart(data)
    })
  }).catch(() => {})

  adminApi.getAiPerformance().then(data => {
    aiPerformance.value = data
    nextTick(() => updateGaugeChart())
  }).catch(() => {})

  chartsInitialized = true
}

function handleResize() {
  charts.forEach(chart => chart.resize())
}

function handleFullscreenChange() {
  setTimeout(() => handleResize(), 100)
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  fetchAllData()
  dataInterval = setInterval(fetchAllData, 30000)
  window.addEventListener('resize', handleResize)
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onUnmounted(() => {
  clearInterval(timeInterval)
  clearInterval(dataInterval)
  if (animFrameId) cancelAnimationFrame(animFrameId)
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
  charts.forEach(chart => chart.dispose())
})
</script>

<style scoped>
.data-screen {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
  color: #e0ecff;
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(ellipse at 50% 0%, rgba(0,80,200,0.12) 0%, transparent 60%),
    radial-gradient(ellipse at 20% 80%, rgba(0,100,255,0.06) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 80%, rgba(0,100,255,0.06) 0%, transparent 50%),
    linear-gradient(180deg, #030b1a 0%, #061328 50%, #030b1a 100%);
}

.data-screen::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(0,145,255,0.03) 1px, transparent 1px),
    linear-gradient(rgba(0,145,255,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  pointer-events: none;
}

.screen-header {
  flex-shrink: 0;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(0,80,200,0.25) 0%, transparent 100%);
  border-bottom: 1px solid rgba(0,145,255,0.2);
  position: relative;
}

.screen-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 120px;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0,212,255,0.08), transparent);
  animation: scanLine 5s ease-in-out infinite;
}
@keyframes scanLine {
  0% { left: -120px; }
  100% { left: calc(100% + 120px); }
}
.screen-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 30vw;
  height: 2px;
  background: #00d4ff;
  box-shadow: 0 0 20px #00d4ff, 0 0 60px rgba(0,212,255,0.3);
}

.header-title {
  font-size: clamp(18px, 2vw, 32px);
  font-weight: 700;
  letter-spacing: 0.5vw;
  background: linear-gradient(90deg, #a0d8ff, #fff, #00d4ff, #fff, #a0d8ff);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleFlow 6s linear infinite;
}

@keyframes titleFlow {
  0% { background-position: 0% 0%; }
  100% { background-position: 200% 0%; }
}

.header-left, .header-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 1.5vw;
  z-index: 1;
}
.header-left { left: 2vw; }
.header-right { right: 2vw; }

.header-date, .header-time {
  font-size: clamp(11px, 0.8vw, 14px);
  color: rgba(180, 210, 255, 0.6);
}
.header-time {
  font-family: 'Courier New', monospace;
  color: #00d4ff;
  letter-spacing: 2px;
  font-size: clamp(14px, 1.2vw, 22px);
  text-shadow: 0 0 10px rgba(0,212,255,0.4);
}

.fullscreen-btn {
  padding: 0.3vh 1vw;
  background: rgba(0,212,255,0.1);
  border: 1px solid rgba(0,145,255,0.3);
  color: #00d4ff;
  border-radius: 4px;
  cursor: pointer;
  font-size: clamp(11px, 0.75vw, 14px);
  transition: all 0.3s;
}
.fullscreen-btn:hover {
  background: rgba(0,212,255,0.25);
  box-shadow: 0 0 15px rgba(0,212,255,0.3);
  transform: scale(1.05);
}

.screen-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 12px 16px;
  gap: 10px;
  min-height: 0;
  position: relative;
  z-index: 1;
}
.screen-body::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(0,212,255,0.15), transparent);
  animation: bodyScan 8s linear infinite;
  pointer-events: none;
}
@keyframes bodyScan {
  0% { top: 0; opacity: 0; }
  5% { opacity: 1; }
  95% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

.kpi-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
  flex-shrink: 0;
}

.kpi-card {
  background: rgba(6, 30, 65, 0.65);
  border: 1px solid rgba(0,145,255,0.2);
  border-radius: 8px;
  padding: 12px 14px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  opacity: 0;
  animation: cardSlideUp 0.5s ease-out forwards;
}
@keyframes cardSlideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.kpi-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
}
.kpi-card:hover {
  border-color: rgba(0,212,255,0.5);
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 24px rgba(0,212,255,0.15), 0 0 30px rgba(0,212,255,0.08);
}
.kpi-card:hover .kpi-icon {
  animation: iconPulse 1s ease-in-out infinite;
}
@keyframes iconPulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0.6; }
}

.kpi-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.kpi-label {
  font-size: clamp(11px, 0.75vw, 14px);
  color: rgba(180, 210, 255, 0.6);
  display: flex;
  align-items: center;
  gap: 0.4vw;
}
.kpi-icon { font-size: 8px; }
.kpi-value {
  font-size: clamp(20px, 1.8vw, 32px);
  font-weight: 700;
  color: #fff;
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
  display: flex;
  align-items: baseline;
  text-shadow: 0 0 10px rgba(0,212,255,0.3);
  transition: text-shadow 0.3s;
}
.kpi-card:hover .kpi-value {
  text-shadow: 0 0 20px rgba(0,212,255,0.6), 0 0 40px rgba(0,212,255,0.3);
}
.flip-num {
  display: inline-block;
  overflow: hidden;
  height: 1.2em;
  line-height: 1.2em;
}
.flip-digit {
  display: inline-block;
  animation: flipIn 0.6s cubic-bezier(0.17, 0.67, 0.29, 1.01) both;
}
@keyframes flipIn {
  0% { opacity: 0; transform: translateY(-100%) rotateX(-80deg); }
  60% { opacity: 1; transform: translateY(10%) rotateX(10deg); }
  100% { opacity: 1; transform: translateY(0) rotateX(0deg); }
}
.kpi-desc {
  font-size: clamp(10px, 0.65vw, 12px);
  color: rgba(180, 210, 255, 0.6);
  margin-top: 4px;
}

.grid-row {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1.2fr 1fr;
  gap: 12px;
  min-height: 0;
}

.col {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

.anim-panel {
  opacity: 0;
  animation: panelFadeIn 0.7s ease-out forwards;
}
@keyframes panelFadeIn {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.col-left .panel {
  flex: 1;
  min-height: 0;
}

.col-center {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}
.panel-member {
  flex: 1;
  min-height: 0;
}
.col-center-bottom {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  flex: 1;
  min-height: 0;
}
.col-center-bottom .panel {
  min-height: 0;
}

.col-right {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}
.panel-bar { flex: 0 0 25%; min-height: 0; }
.panel-live { flex: 1; min-height: 0; }
.panel-rank { flex: 1; min-height: 0; }

.panel {
  background: rgba(6, 30, 65, 0.65);
  border: 1px solid rgba(0,145,255,0.2);
  border-radius: 8px;
  padding: 10px 14px;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: border-color 0.4s, box-shadow 0.4s;
}
.panel::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, #00d4ff 50%, transparent 100%);
  background-size: 200% 100%;
  animation: titleFlow 3s linear infinite;
}
.panel::after {
  content: '';
  position: absolute;
  inset: -1px;
  border-radius: 8px;
  border: 1px solid transparent;
  background: linear-gradient(135deg, rgba(0,212,255,0.15), transparent, rgba(0,255,136,0.1)) border-box;
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask-composite: exclude;
  -webkit-mask-composite: xor;
  animation: borderBreathe 4s ease-in-out infinite;
  pointer-events: none;
}
@keyframes borderBreathe {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}
.panel:hover {
  border-color: rgba(0,212,255,0.5);
  box-shadow: 0 0 20px rgba(0,212,255,0.15), inset 0 0 20px rgba(0,212,255,0.05);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  flex-shrink: 0;
}
.panel-title {
  font-size: clamp(12px, 0.85vw, 16px);
  font-weight: 600;
  padding-left: 0.8vw;
  border-left: 3px solid #00d4ff;
  display: flex;
  align-items: center;
  gap: 0.5vw;
  background: linear-gradient(90deg, #fff 0%, #00d4ff 50%, #fff 100%);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: titleShimmer 4s ease-in-out infinite;
}
@keyframes titleShimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
.panel-title::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, rgba(0,145,255,0.3), transparent);
  margin-left: 0.8vw;
}

.live-count {
  font-size: clamp(10px, 0.7vw, 13px);
  color: #00ff88;
}

.panel-body {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.chart-box {
  width: 100%;
  height: 100%;
}

.live-scroll {
  height: 100%;
  overflow: hidden;
}
.live-scroll-inner {
  animation: liveScrollAnim var(--scroll-duration, 20s) linear infinite;
}
.live-scroll:hover .live-scroll-inner {
  animation-play-state: paused;
}
@keyframes liveScrollAnim {
  0% { transform: translateY(0); }
  100% { transform: translateY(-50%); }
}

.live-item {
  display: flex;
  align-items: center;
  padding: 6px 4px;
  border-bottom: 1px solid rgba(0,145,255,0.08);
  border-radius: 4px;
  transition: all 0.25s;
  animation: liveFadeIn 0.4s ease-out both;
}
.live-item:nth-child(1) { animation-delay: 0.02s; }
.live-item:nth-child(2) { animation-delay: 0.04s; }
.live-item:nth-child(3) { animation-delay: 0.06s; }
.live-item:nth-child(4) { animation-delay: 0.08s; }
.live-item:nth-child(5) { animation-delay: 0.1s; }
.live-item:nth-child(6) { animation-delay: 0.12s; }
.live-item:nth-child(7) { animation-delay: 0.14s; }
.live-item:nth-child(8) { animation-delay: 0.16s; }
.live-item:nth-child(9) { animation-delay: 0.18s; }
.live-item:nth-child(10) { animation-delay: 0.2s; }
@keyframes liveFadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.live-item:hover {
  background: rgba(0,212,255,0.08);
}

.live-avatar {
  width: clamp(24px, 2vw, 32px);
  height: clamp(24px, 2vw, 32px);
  border-radius: 50%;
  background: linear-gradient(135deg, #00d4ff, #0066cc);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  font-size: clamp(10px, 0.6vw, 12px);
  font-weight: 600;
  color: #fff;
  flex-shrink: 0;
  position: relative;
}
.live-avatar::after {
  content: '';
  position: absolute;
  inset: -3px;
  border-radius: 50%;
  border: 1.5px solid rgba(0,212,255,0.4);
  animation: avatarPulse 2.5s ease-out infinite;
}
@keyframes avatarPulse {
  0% { transform: scale(1); opacity: 1; }
  100% { transform: scale(1.5); opacity: 0; }
}
.live-info { flex: 1; min-width: 0; }
.live-msg {
  font-size: clamp(10px, 0.65vw, 12px);
  color: rgba(180, 210, 255, 0.8);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.live-time {
  font-size: clamp(9px, 0.6vw, 11px);
  color: rgba(180, 210, 255, 0.6);
  margin-left: 8px;
  flex-shrink: 0;
}

.live-dot {
  width: 8px;
  height: 8px;
  background: #00ff88;
  border-radius: 50%;
  display: inline-block;
  margin-right: 0.3vw;
  animation: dotPulse 1.5s ease-out infinite;
  box-shadow: 0 0 6px rgba(0,255,136,0.6);
}
@keyframes dotPulse {
  0% { box-shadow: 0 0 0 0 rgba(0,255,136,0.6); }
  70% { box-shadow: 0 0 0 8px rgba(0,255,136,0); }
  100% { box-shadow: 0 0 0 0 rgba(0,255,136,0); }
}

.rank-scroll {
  height: 100%;
  overflow-y: auto;
  scrollbar-width: none;
}
.rank-scroll::-webkit-scrollbar {
  display: none;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 5px 0;
  border-bottom: 1px solid rgba(0,145,255,0.06);
  transition: all 0.25s;
  opacity: 0;
  animation: rankSlideIn 0.5s ease-out forwards;
}
.rank-item:nth-child(1) { animation-delay: 0.05s; }
.rank-item:nth-child(2) { animation-delay: 0.1s; }
.rank-item:nth-child(3) { animation-delay: 0.15s; }
.rank-item:nth-child(4) { animation-delay: 0.2s; }
.rank-item:nth-child(5) { animation-delay: 0.25s; }
.rank-item:nth-child(6) { animation-delay: 0.3s; }
.rank-item:nth-child(7) { animation-delay: 0.35s; }
.rank-item:nth-child(8) { animation-delay: 0.4s; }
@keyframes rankSlideIn {
  from { opacity: 0; transform: translateX(12px); }
  to { opacity: 1; transform: translateX(0); }
}
.rank-item:hover {
  padding-left: 3px;
  background: rgba(0,212,255,0.06);
}

.rank-no {
  width: clamp(18px, 1.3vw, 22px);
  height: clamp(18px, 1.3vw, 22px);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: clamp(10px, 0.65vw, 12px);
  font-weight: 700;
  margin-right: 8px;
  flex-shrink: 0;
}
.rank-no.r1 { background: linear-gradient(135deg, #ff6b35, #ff4d5a); color: #fff; box-shadow: 0 0 8px rgba(255,77,90,0.4); }
.rank-no.r2 { background: linear-gradient(135deg, #ffb400, #ff8c00); color: #fff; box-shadow: 0 0 8px rgba(255,180,0,0.4); }
.rank-no.r3 { background: linear-gradient(135deg, #00d4ff, #0088ff); color: #fff; box-shadow: 0 0 8px rgba(0,212,255,0.4); }
.rank-no.r4 { background: rgba(0,212,255,0.15); color: rgba(180, 210, 255, 0.6); }

.rank-name {
  flex: 1;
  font-size: clamp(11px, 0.7vw, 13px);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.rank-bar-w {
  width: 4vw;
  height: 5px;
  background: rgba(0,145,255,0.1);
  border-radius: 3px;
  margin: 0 8px;
  overflow: hidden;
  flex-shrink: 0;
}
.rank-bar {
  height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, #00d4ff, #00ff88);
  animation: rankBarGrow 1s ease-out forwards;
  transform-origin: left;
}
@keyframes rankBarGrow {
  from { transform: scaleX(0); }
  to { transform: scaleX(1); }
}
.rank-val {
  font-size: clamp(11px, 0.7vw, 13px);
  color: #00d4ff;
  font-family: 'Courier New', monospace;
  min-width: 24px;
  text-align: right;
  flex-shrink: 0;
}
</style>
