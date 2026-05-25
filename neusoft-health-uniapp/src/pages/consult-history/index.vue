<template>
  <view class="page">
    <NavHeader title="咨询历史" showBack fallbackUrl="/pages/profile/index" />
    <scroll-view scroll-y class="scroll-body" :scroll-top="scrollTop">
      <Card v-for="(item, i) in items" :key="i">
        <view class="list-row" @click="onItemClick(item)">
          <view class="list-left">
            <text class="list-title">{{ item.title }}</text>
            <text v-if="item.lastMessage" class="list-sub">{{ item.lastMessage }}</text>
          </view>
          <text v-if="item.messageCount" class="list-extra">{{ item.messageCount }}条消息</text>
          <text class="list-arrow">›</text>
        </view>
      </Card>
      <view v-if="items.length === 0" class="empty-hint">
        <SvgIcon name="message" :size="48" color="#BBBFC4" />
        <text class="empty-text">暂无内容</text>
      </view>
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
import { useUserStore } from '@/stores/user'
import type { ConsultSession } from '@/types'

const { scrollTop } = useScrollToTop()

const items = ref<ConsultSession[]>([])

const goBack = () => uni.navigateBack()

const onItemClick = (item: ConsultSession) => {
  uni.navigateTo({ url: `/pages/consult/index?sessionId=${item.id}` })
}

onMounted(async () => {
  if (!useUserStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  try {
    const res = await consultApi.listSessions()
    items.value = res.data || []
  } catch (err) {
    console.error('获取咨询历史失败', err)
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
.empty-hint { padding: 80px 0; text-align: center; }
.empty-icon { font-size: 48px; display: block; margin-bottom: 12px; }
.empty-text { font-size: 14px; color: #BBBFC4; }
</style>
