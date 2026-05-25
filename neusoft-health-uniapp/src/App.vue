<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('[东软智慧健康] App Launch')
  const token = uni.getStorageSync('token')
  if (token) {
    console.log('[东软智慧健康] 已登录')
  }
})

onShow(() => {
  console.log('[东软智慧健康] App Show')
})

onHide(() => {
  console.log('[东软智慧健康] App Hide')
})
</script>

<style>
/* 全局基础样式 */
* {
  box-sizing: border-box;
}

page {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  font-size: 14px;
  color: #1F2329;
  background: #F5F7FA;
  -webkit-font-smoothing: antialiased;
  height: 100%;
  min-height: 100%;
}

view, text, button, input {
  box-sizing: border-box;
}

/* 全局按钮重置 */
button::after {
  border: none;
}
button {
  padding: 0;
  margin: 0;
  line-height: inherit;
}

/* 全局输入框重置 */
input {
  outline: none;
  border: none;
}
</style>