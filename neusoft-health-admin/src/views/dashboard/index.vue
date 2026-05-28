<template>
  <div class="dashboard">
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-info">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-trend" :class="item.trend > 0 ? 'up' : 'down'">
                {{ item.trend > 0 ? '↑' : '↓' }} {{ Math.abs(item.trend) }}%
                <span class="trend-label">较昨日</span>
              </div>
            </div>
            <div class="stat-icon" :style="{ background: item.color }">
              <el-icon :size="24"><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>咨询分类分布</span>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="table-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待审核消息</span>
              <el-button type="primary" link>查看全部</el-button>
            </div>
          </template>
          <el-table :data="pendingReviews" style="width: 100%" max-height="300">
            <el-table-column prop="nickname" label="用户" width="100" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="createdTime" label="时间" width="160" />
            <el-table-column label="操作" width="100">
              <template #default>
                <el-button type="primary" link size="small">审核</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>紧急情况</span>
              <el-button type="danger" link>查看全部</el-button>
            </div>
          </template>
          <el-table :data="emergencyList" style="width: 100%" max-height="300">
            <el-table-column prop="nickname" label="用户" width="100" />
            <el-table-column prop="keyword" label="关键词" width="120" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.handled ? 'success' : 'danger'" size="small">
                  {{ row.handled ? '已处理' : '待处理' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '@/api/admin'
import type { DashboardStats } from '@/api/admin'
import {
  User,
  ChatLineRound,
  ShoppingCart,
  Star
} from '@element-plus/icons-vue'

const trendChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()
const trendPeriod = ref('week')

const stats = ref<DashboardStats>({
  totalUsers: 0,
  totalSessions: 0,
  totalMessages: 0,
  pendingReviews: 0,
  emergencyCount: 0,
  categoryStats: []
})

const pendingReviews = ref<any[]>([])
const emergencyList = ref<any[]>([])

const statCards = ref([
  { title: '累计用户', value: '0', icon: User, color: '#1890ff', trend: 12 },
  { title: '总咨询量', value: '0', icon: ChatLineRound, color: '#52c41a', trend: 15 },
  { title: '会话总数', value: '0', icon: ShoppingCart, color: '#722ed1', trend: 8 },
  { title: '待审核', value: '0', icon: Star, color: '#fa541c', trend: -5 }
])

const charts: echarts.ECharts[] = []

function formatNumber(num: number): string {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
}

function initTrendChart() {
  if (!trendChartRef.value) return
  const chart = echarts.init(trendChartRef.value)
  charts.push(chart)

  const dates = []
  const values = []
  const now = new Date()
  for (let i = 6; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    dates.push(`${d.getMonth() + 1}/${d.getDate()}`)
    values.push(Math.floor(Math.random() * 100 + 50))
  }

  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { top: 30, right: 20, bottom: 30, left: 50 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color: '#1890ff', width: 2 },
      itemStyle: { color: '#1890ff' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24,144,255,0.3)' },
          { offset: 1, color: 'rgba(24,144,255,0.02)' }
        ])
      }
    }]
  })
}

function initCategoryChart() {
  if (!categoryChartRef.value) return
  const chart = echarts.init(categoryChartRef.value)
  charts.push(chart)

  const data = stats.value.categoryStats || []
  const colors = ['#1890ff', '#52c41a', '#722ed1', '#fa541c', '#13c2c2', '#faad14']

  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}\n{d}%',
        fontSize: 11
      },
      data: data.map((item, index) => ({
        name: item.name,
        value: item.value,
        itemStyle: { color: colors[index % colors.length] }
      }))
    }]
  })
}

async function fetchData() {
  try {
    const data = await adminApi.getDashboardStats()
    stats.value = data

    statCards.value[0].value = formatNumber(data.totalUsers)
    statCards.value[1].value = formatNumber(data.totalMessages)
    statCards.value[2].value = formatNumber(data.totalSessions)
    statCards.value[3].value = formatNumber(data.pendingReviews)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

function handleResize() {
  charts.forEach(chart => chart.resize())
}

onMounted(async () => {
  await fetchData()

  nextTick(() => {
    initTrendChart()
    initCategoryChart()
  })

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(chart => chart.dispose())
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-trend.up {
  color: #52c41a;
}

.stat-trend.down {
  color: #ff4d4f;
}

.trend-label {
  color: #999;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.chart-row {
  margin-bottom: 16px;
}

.chart-container {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-row {
  margin-bottom: 16px;
}
</style>
