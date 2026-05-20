<template>
  <view class="page">
    <NavHeader title="AI健康助手" showBack @back="goBack" />
    <scroll-view scroll-y class="scroll-body">
      <Card v-for="(item, i) in items" :key="i">
        <view class="list-row" @click="onItemClick(item, i)">
          <view class="list-left">
            <text class="list-title">{{ item.text }}</text>
            <text v-if="item.time" class="list-sub">{{ item.time }}</text>
          </view>
          <text v-if="item.count" class="list-extra">{{ item.count }}</text>
          <text class="list-arrow">›</text>
        </view>
      </Card>
      <view v-if="items.length === 0" class="empty-hint">
        <text class="empty-icon">🤖</text>
        <text class="empty-text">暂无内容</text>
      </view>
      <view style="height: 40px;" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Card from '@/components/Card/Card.vue'
import { ref } from 'vue'

const items = ref([{'text': '健康报告解读', 'time': '上传报告自动分析', 'count': ''}, {'text': '每日健康贴士', 'time': '个性健康建议', 'count': ''}, {'text': '用药提醒', 'time': '定时提醒服药', 'count': ''}])

const goBack = () => uni.navigateBack()

const onItemClick = (item: any, index: number) => {
  uni.showToast({ title: `查看: ${item.text}`, icon: 'none' })
}
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
.empty-hint { padding: 80px 0; text-align: center; }
.empty-icon { font-size: 48px; display: block; margin-bottom: 12px; }
.empty-text { font-size: 14px; color: #BBBFC4; }
</style>
