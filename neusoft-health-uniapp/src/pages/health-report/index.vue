<template>
  <view class="page">
    <NavHeader title="AI报告解读" showBack fallbackUrl="/pages/health-assistant/index" />

    <scroll-view v-if="analysisState === 'idle' || analysisState === 'uploading'" scroll-y class="scroll-body">
      <view class="upload-section">
        <view class="upload-zone" @click="chooseFile">
          <view class="upload-icon-wrap">
            <SvgIcon name="chart" :size="32" color="#4A90D9" />
          </view>
          <text class="upload-title">上传体检报告或化验单</text>
          <text class="upload-desc">支持 PDF、图片格式（JPG/PNG），最大20MB</text>
        </view>
        <button class="demo-btn" @click="simulateAnalysis">查看示例报告（演示）</button>
      </view>

      <view class="features-section">
        <text class="section-label">AI报告解读功能</text>
        <view class="feature-grid">
          <view class="feature-item">
            <view class="feature-icon" style="background: linear-gradient(135deg, #4A90D9, #357ABD);">
              <SvgIcon name="clipboard" :size="20" color="#FFFFFF" />
            </view>
            <text class="feature-name">指标识别</text>
            <text class="feature-desc">自动识别报告中的关键指标</text>
          </view>
          <view class="feature-item">
            <view class="feature-icon" style="background: linear-gradient(135deg, #52C41A, #3CB371);">
              <SvgIcon name="hospital" :size="20" color="#FFFFFF" />
            </view>
            <text class="feature-name">异常预警</text>
            <text class="feature-desc">标记异常指标并给出建议</text>
          </view>
          <view class="feature-item">
            <view class="feature-icon" style="background: linear-gradient(135deg, #FAAD14, #FFD700);">
              <SvgIcon name="lightbulb" :size="20" color="#FFFFFF" />
            </view>
            <text class="feature-name">健康建议</text>
            <text class="feature-desc">个性化饮食运动改善方案</text>
          </view>
        </view>
      </view>

      <view class="disclaimer-bar">
        <text class="disclaimer-text">AI报告解读仅供参考，不能替代专业医疗诊断。如有异常指标，请及时咨询医生。</text>
      </view>

      <view style="height: 40px;" />
    </scroll-view>

    <scroll-view v-if="analysisState === 'analyzing'" scroll-y class="scroll-body">
      <view class="analyzing-state">
        <view class="analyzing-circle">
          <SvgIcon name="robot" :size="36" color="#FFFFFF" />
        </view>
        <text class="analyzing-title">AI正在分析您的报告...</text>
        <text class="analyzing-sub">正在识别指标数据，请稍候</text>
        <view class="analyzing-dots">
          <view class="adot adot1" />
          <view class="adot adot2" />
          <view class="adot adot3" />
        </view>
      </view>
    </scroll-view>

    <scroll-view v-if="analysisState === 'done'" scroll-y class="scroll-body">
      <view class="report-overview">
        <view class="overview-header">
          <text class="overview-title">报告概览</text>
          <view class="ai-tag"><text class="ai-tag-text">AI已解读</text></view>
        </view>
        <view class="overview-row">
          <text class="overview-label">姓名</text>
          <text class="overview-value">{{ reportData.name }}</text>
        </view>
        <view class="overview-row">
          <text class="overview-label">检查日期</text>
          <text class="overview-value">{{ reportData.date }}</text>
        </view>
        <view class="overview-row">
          <text class="overview-label">报告类型</text>
          <text class="overview-value">{{ reportData.type }}</text>
        </view>
        <view class="overview-row">
          <text class="overview-label">综合评估</text>
          <text class="overview-value overview-good">{{ reportData.summary }}</text>
        </view>
      </view>

      <view class="indicators-section">
        <view class="indicators-header">
          <text class="indicators-title">关键指标分析</text>
          <text class="indicators-count">共检测 {{ reportData.indicators.length }} 项指标</text>
        </view>
        <view v-for="(ind, i) in displayedIndicators" :key="i" class="indicator-row">
          <view class="indicator-left">
            <text class="indicator-name">{{ ind.name }}</text>
            <text class="indicator-range">正常范围：{{ ind.range }}</text>
            <view class="progress-bg">
              <view class="progress-fill" :style="{ width: ind.percent + '%', background: ind.color }" />
            </view>
          </view>
          <view class="indicator-right">
            <text class="indicator-value" :class="'status-' + ind.status">{{ ind.value }}</text>
            <view class="status-tag" :class="'tag-' + ind.status">
              <text class="status-text">{{ ind.statusText }}</text>
            </view>
          </view>
        </view>
        <view v-if="!showAllIndicators && reportData.indicators.length > 5" class="show-more" @click="showAllIndicators = true">
          <text class="show-more-text">查看全部{{ reportData.indicators.length }}项指标</text>
        </view>
      </view>

      <view class="advice-section">
        <text class="advice-title">AI健康建议</text>

        <view v-if="reportData.warnings.length > 0" class="advice-card warning-card">
          <view class="advice-card-header">
            <text class="advice-icon">⚠</text>
            <text class="advice-card-title">需关注的异常指标</text>
          </view>
          <view v-for="(w, i) in reportData.warnings" :key="i" class="advice-item">
            <text class="advice-text">{{ i + 1 }}. {{ w }}</text>
          </view>
        </view>

        <view class="advice-card success-card">
          <view class="advice-card-header">
            <text class="advice-icon">✓</text>
            <text class="advice-card-title">健康改善建议</text>
          </view>
          <view v-for="(s, i) in reportData.suggestions" :key="i" class="advice-item">
            <text class="advice-text">• {{ s }}</text>
          </view>
        </view>
      </view>

      <view class="action-bar">
        <button class="action-btn" @click="saveToRecord">保存到健康档案</button>
      </view>

      <view class="disclaimer-bar">
        <text class="disclaimer-text">以上内容由AI生成，仅供参考，不构成医疗诊断建议。如有身体不适，请及时就医。</text>
      </view>

      <view style="height: 40px;" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'

interface Indicator {
  name: string
  range: string
  value: string
  status: 'normal' | 'high' | 'low'
  statusText: string
  percent: number
  color: string
}

interface ReportData {
  name: string
  date: string
  type: string
  summary: string
  indicators: Indicator[]
  warnings: string[]
  suggestions: string[]
}

type AnalysisState = 'idle' | 'uploading' | 'analyzing' | 'done'

const analysisState = ref<AnalysisState>('idle')
const showAllIndicators = ref(false)

const reportData = ref<ReportData>({
  name: '张**',
  date: '2026-05-15',
  type: '常规体检报告',
  summary: '整体健康状况良好',
  indicators: [
    { name: '血压', range: '90-140/60-90', value: '120/80', status: 'normal', statusText: '正常', percent: 60, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '血糖（空腹）', range: '3.9-6.1 mmol/L', value: '6.8', status: 'high', statusText: '偏高', percent: 72, color: 'linear-gradient(90deg,#FAAD14,#FFC53D)' },
    { name: '总胆固醇', range: '<5.2 mmol/L', value: '4.6', status: 'normal', statusText: '正常', percent: 56, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '尿酸', range: '150-420 umol/L', value: '445', status: 'high', statusText: '偏高', percent: 78, color: 'linear-gradient(90deg,#FAAD14,#FFC53D)' },
    { name: '心率', range: '60-100 次/分', value: '72', status: 'normal', statusText: '正常', percent: 60, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '甘油三酯', range: '<1.7 mmol/L', value: '1.5', status: 'normal', statusText: '正常', percent: 50, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '高密度脂蛋白', range: '>1.0 mmol/L', value: '1.3', status: 'normal', statusText: '正常', percent: 65, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '低密度脂蛋白', range: '<3.4 mmol/L', value: '2.8', status: 'normal', statusText: '正常', percent: 55, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '谷丙转氨酶', range: '0-40 U/L', value: '28', status: 'normal', statusText: '正常', percent: 45, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '肌酐', range: '44-133 umol/L', value: '85', status: 'normal', statusText: '正常', percent: 55, color: 'linear-gradient(90deg,#52C41A,#73D13D)' },
    { name: '血红蛋白', range: '120-160 g/L', value: '108', status: 'low', statusText: '偏低', percent: 42, color: 'linear-gradient(90deg,#FAAD14,#FFC53D)' },
    { name: '白细胞计数', range: '4-10 ×10⁹/L', value: '6.5', status: 'normal', statusText: '正常', percent: 55, color: 'linear-gradient(90deg,#52C41A,#73D13D)' }
  ],
  warnings: [
    '**血糖偏高（6.8 mmol/L）**：已超出正常空腹血糖范围，建议进行糖耐量测试，排查糖尿病风险。',
    '**尿酸偏高（445 umol/L）**：接近高尿酸血症诊断标准，需注意饮食控制。',
    '**血红蛋白偏低（108 g/L）**：提示可能存在轻度贫血，建议补充铁质和维生素B12。'
  ],
  suggestions: [
    '**饮食方面**：控制含糖饮料和精制碳水摄入，减少高嘌呤食物（动物内脏、海鲜、啤酒）',
    '**运动建议**：每周至少150分钟中等强度有氧运动，配合力量训练',
    '**复查建议**：建议1个月后复查空腹血糖和尿酸',
    '**就医提示**：如出现多饮、多食、多尿、关节红肿热痛等症状，请及时就医'
  ]
})

const displayedIndicators = computed(() => {
  if (showAllIndicators.value) return reportData.value.indicators
  return reportData.value.indicators.slice(0, 5)
})

const chooseFile = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: () => {
      startAnalysis()
    }
  })
}

const simulateAnalysis = () => {
  startAnalysis()
}

const startAnalysis = () => {
  analysisState.value = 'analyzing'
  showAllIndicators.value = false
  setTimeout(() => {
    analysisState.value = 'done'
  }, 2500)
}

const saveToRecord = () => {
  uni.showToast({ title: '已保存到健康档案', icon: 'success' })
}

if (!useUserStore.isLoggedIn) {
  uni.navigateTo({ url: '/pages/login/index' })
}
</script>

<style scoped>
.page { min-height: 100vh; background: #F5F7FA; }
.scroll-body { padding-top: 12px; }

.upload-section { padding: 0 16px; margin-bottom: 16px; }
.upload-zone {
  background: #FAFBFC;
  border: 2px dashed #D0D5DD;
  border-radius: 16px;
  padding: 40px 20px;
  text-align: center;
}
.upload-icon-wrap {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #E8F0FE, #D0E3FC);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}
.upload-title { font-size: 15px; font-weight: 500; color: #1F2329; display: block; margin-bottom: 6px; }
.upload-desc { font-size: 13px; color: #8F959E; display: block; }
.demo-btn {
  margin-top: 12px;
  width: 100%;
  background: #4A90D9;
  color: #FFFFFF;
  border: none;
  padding: 12px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
}

.features-section { padding: 0 16px; margin-bottom: 16px; }
.section-label { font-size: 13px; color: #8F959E; padding-bottom: 12px; display: block; }
.feature-grid { display: flex; gap: 12px; }
.feature-item {
  flex: 1;
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px 12px;
  text-align: center;
}
.feature-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 8px;
}
.feature-name { font-size: 13px; font-weight: 600; color: #1F2329; display: block; margin-bottom: 4px; }
.feature-desc { font-size: 11px; color: #8F959E; display: block; line-height: 1.4; }

.disclaimer-bar { margin: 16px; padding: 12px; background: rgba(74,144,217,0.06); border-radius: 8px; }
.disclaimer-text { font-size: 11px; color: #8F959E; line-height: 1.6; }

.analyzing-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;
}
.analyzing-circle {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #667EEA, #764BA2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}
.analyzing-title { font-size: 16px; font-weight: 500; color: #1F2329; margin-bottom: 6px; }
.analyzing-sub { font-size: 13px; color: #8F959E; }
.analyzing-dots { display: flex; gap: 8px; margin-top: 24px; }
.adot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4A90D9;
  animation: bounce 1.4s ease-in-out infinite;
}
.adot1 { animation-delay: 0s; }
.adot2 { animation-delay: 0.2s; }
.adot3 { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 80%, 100% { opacity: 0.3; transform: scale(0.8); }
  40% { opacity: 1; transform: scale(1); }
}

.report-overview {
  margin: 0 16px 16px;
  background: #FFFFFF;
  border-radius: 16px;
  padding: 16px;
}
.overview-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.overview-title { font-size: 16px; font-weight: 600; color: #1F2329; }
.ai-tag { background: #F0FFF4; padding: 4px 10px; border-radius: 12px; }
.ai-tag-text { font-size: 11px; color: #52C41A; font-weight: 500; }
.overview-row { display: flex; justify-content: space-between; padding: 8px 0; }
.overview-label { font-size: 14px; color: #8F959E; }
.overview-value { font-size: 14px; color: #1F2329; }
.overview-good { color: #52C41A; font-weight: 500; }

.indicators-section {
  margin: 0 16px 16px;
  background: #FFFFFF;
  border-radius: 16px;
  padding: 16px;
}
.indicators-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.indicators-title { font-size: 16px; font-weight: 600; color: #1F2329; }
.indicators-count { font-size: 12px; color: #8F959E; }

.indicator-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #F0F0F0;
}
.indicator-row:last-child { border-bottom: none; }
.indicator-left { flex: 1; }
.indicator-name { font-size: 14px; font-weight: 500; color: #1F2329; display: block; }
.indicator-range { font-size: 12px; color: #8F959E; display: block; margin-top: 2px; }
.progress-bg {
  height: 8px;
  background: #F0F0F0;
  border-radius: 4px;
  margin-top: 6px;
  overflow: hidden;
}
.progress-fill { height: 100%; border-radius: 4px; transition: width 1.5s ease; }
.indicator-right { text-align: right; margin-left: 16px; }
.indicator-value { font-size: 18px; font-weight: 600; display: block; }
.status-normal { color: #52C41A; }
.status-high { color: #FF4757; }
.status-low { color: #FAAD14; }
.status-tag { display: inline-block; padding: 2px 10px; border-radius: 12px; margin-top: 4px; }
.tag-normal { background: #F0FFF4; }
.tag-high { background: #FFF1F0; }
.tag-low { background: #FFF7E6; }
.status-text { font-size: 12px; }
.tag-normal .status-text { color: #52C41A; }
.tag-high .status-text { color: #FF4757; }
.tag-low .status-text { color: #FAAD14; }

.show-more { text-align: center; padding: 12px 0 4px; }
.show-more-text { font-size: 13px; color: #4A90D9; }

.advice-section { margin: 0 16px 16px; }
.advice-title { font-size: 16px; font-weight: 600; color: #1F2329; display: block; margin-bottom: 12px; }
.advice-card {
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}
.warning-card { background: linear-gradient(135deg, #FFF7E6, #FFFBE6); }
.success-card { background: linear-gradient(135deg, #F0FFF4, #E8F8F0); }
.advice-card-header { display: flex; align-items: center; gap: 8px; margin-bottom: 10px; }
.advice-icon { font-size: 16px; }
.warning-card .advice-icon { color: #FAAD14; }
.success-card .advice-icon { color: #52C41A; }
.advice-card-title { font-size: 14px; font-weight: 600; }
.warning-card .advice-card-title { color: #D48806; }
.success-card .advice-card-title { color: #237837; }
.advice-item { margin-bottom: 6px; }
.advice-text { font-size: 13px; color: #646A73; line-height: 1.7; }

.action-bar { padding: 0 16px 16px; }
.action-btn {
  width: 100%;
  background: #FFFFFF;
  color: #4A90D9;
  border: 1px solid #4A90D9;
  padding: 12px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
}
</style>
