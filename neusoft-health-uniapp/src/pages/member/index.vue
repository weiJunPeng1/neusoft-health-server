<template>
  <view class="page">
    <NavHeader title="会员中心" theme="blue" :showBack="false" />

    <scroll-view scroll-y class="page-scroll">
      <!-- 会员状态卡片 -->
      <view class="member-status-card">
        <view class="status-top">
          <view class="level-badge">
            <text class="level-icon">👑</text>
          </view>
          <view class="status-info">
            <text class="level-name">黄金会员</text>
            <text class="level-expire">有效期至 2026-08-19</text>
          </view>
          <view class="lv-tag">Lv.2</view>
        </view>
        <view class="status-quotas">
          <view class="quota-item">
            <text class="quota-val">50</text>
            <text class="quota-label">次/日</text>
          </view>
          <view class="quota-item">
            <text class="quota-val">30</text>
            <text class="quota-label">轮上下文</text>
          </view>
          <view class="quota-item">
            <text class="quota-val">✓</text>
            <text class="quota-label">自动建档</text>
          </view>
          <view class="quota-item">
            <text class="quota-val">—</text>
            <text class="quota-label">深度分析</text>
          </view>
        </view>
      </view>

      <!-- 订阅方案 -->
      <view class="section-title">订阅会员</view>
      <view class="plan-list">
        <view class="plan-card" v-for="p in plans" :key="p.code">
          <view class="plan-left">
            <text class="plan-name">{{ p.name }}</text>
            <view class="plan-price-row">
              <text class="plan-price">¥{{ p.price }}</text>
              <text v-if="p.originalPrice" class="plan-original">¥{{ p.originalPrice }}</text>
            </view>
            <text class="plan-quota">{{ p.quota }}次/日 · {{ p.rounds }}轮上下文</text>
          </view>
          <view class="plan-right">
            <button class="subscribe-btn" @click="doSubscribe(p.code)">立即开通</button>
          </view>
        </view>
      </view>

      <!-- 权益对比表 -->
      <view class="section-title">权益对比</view>
      <view class="benefits-table">
        <view class="benefit-row" v-for="(row,i) in benefits" :key="i">
          <text class="benefit-name">{{ row.name }}</text>
          <text v-for="(val,j) in row.values" :key="j" :class="['benefit-val', val === '✓' ? 'green' : val === '—' ? 'gray' : '']">{{ val }}</text>
        </view>
      </view>

      <!-- 订阅记录 -->
      <view class="section-title">订阅记录</view>
      <Card>
        <view class="history-item" v-for="h in history" :key="h.id">
          <view class="history-left">
            <text class="history-name">{{ h.plan }}</text>
            <text class="history-time">{{ h.time }}</text>
          </view>
          <view :class="['history-status', h.status === 'active' ? 's-active' : 's-expired']">{{ h.statusText }}</view>
        </view>
      </Card>

      <view style="height: 80px;" />
    </scroll-view>

    <TabBar current="member" />
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import TabBar from '@/components/TabBar/TabBar.vue'
import { reactive } from 'vue'

const plans = [
  { code: 'L1_M', name: '白银月卡', price: '19.9', originalPrice: '39.9', quota: '20', rounds: '15' },
  { code: 'L2_M', name: '黄金月卡', price: '39.9', originalPrice: '59.9', quota: '50', rounds: '30' },
  { code: 'L3_M', name: '铂金月卡', price: '69.9', originalPrice: '99.9', quota: '∞', rounds: '50' },
]

const benefits = reactive([
  { name: '每日咨询次数', values: ['3', '20', '50', '∞'] },
  { name: '上下文轮数', values: ['5', '15', '30', '50'] },
  { name: '自动建档', values: ['—', '✓', '✓', '✓'] },
  { name: '深度分析', values: ['—', '—', '—', '✓'] },
  { name: '导出记录', values: ['—', '✓', '✓', '✓'] },
])

const history = reactive([
  { id: 1, plan: '黄金月卡', time: '2026-05-20', status: 'active', statusText: '生效中' },
  { id: 2, plan: '白银月卡', time: '2026-04-20', status: 'expired', statusText: '已过期' },
])

const doSubscribe = (code: string) => {
  uni.navigateTo({ url: '/pages/payment/index' })
}
</script>

<style scoped>
.page { height: 100vh; display: flex; flex-direction: column; background: #F5F7FA; }
.page-scroll { flex: 1; }

.member-status-card {
  margin: 16px;
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 12px 40px rgba(102,126,234,0.4);
  color: #FFFFFF;
}
.status-top { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
.level-badge {
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.level-icon { font-size: 28px; }
.status-info { flex: 1; }
.level-name { font-size: 18px; font-weight: 600; display: block; }
.level-expire { font-size: 12px; opacity: 0.8; margin-top: 2px; display: block; }
.lv-tag {
  background: rgba(255,255,255,0.25);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}
.status-quotas { display: flex; justify-content: space-around; text-align: center; }
.quota-item { display: flex; flex-direction: column; }
.quota-val { font-size: 22px; font-weight: 700; }
.quota-label { font-size: 11px; opacity: 0.8; margin-top: 2px; }

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1F2329;
  padding: 16px 16px 12px;
}

.plan-list { padding: 0 16px; display: flex; flex-direction: column; gap: 10px; }
.plan-card {
  background: #FFFFFF;
  border-radius: 14px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  border: 1px solid #F0F1F5;
}
.plan-left { flex: 1; }
.plan-name { font-size: 14px; font-weight: 500; color: #1F2329; display: block; }
.plan-price-row { margin-top: 4px; }
.plan-price { font-size: 20px; font-weight: 700; color: #FF4757; }
.plan-original { font-size: 12px; color: #BBBFC4; text-decoration: line-through; margin-left: 8px; }
.plan-quota { font-size: 11px; color: #8F959E; margin-top: 4px; display: block; }
.subscribe-btn {
  background: linear-gradient(135deg, #4A90D9, #357ABD);
  color: #FFFFFF;
  font-size: 13px;
  padding: 8px 18px;
  border-radius: 20px;
  border: none;
  font-weight: 500;
}

.benefits-table { margin: 0 16px; background: #FFFFFF; border-radius: 14px; overflow: hidden; }
.benefit-row {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid #F5F5F5;
}
.benefit-row:last-child { border-bottom: none; }
.benefit-name { flex: 2; font-size: 13px; color: #646A73; }
.benefit-val { flex: 1; text-align: center; font-size: 13px; font-weight: 500; color: #1F2329; }
.benefit-val.green { color: #52C41A; }
.benefit-val.gray { color: #BBBFC4; }

.history-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; }
.history-item + .history-item { border-top: 1px solid #F5F5F5; }
.history-name { font-size: 14px; color: #1F2329; display: block; }
.history-time { font-size: 12px; color: #8F959E; margin-top: 2px; display: block; }
.history-status { font-size: 12px; padding: 3px 10px; border-radius: 10px; }
.s-active { background: #F0FFF4; color: #52C41A; }
.s-expired { background: #F5F7FA; color: #BBBFC4; }
</style>
