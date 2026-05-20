<template>
  <view class="page">
    <NavHeader title="健康咨询" showBack @back="goBack" />

    <!-- 紧急横幅 -->
    <view class="emergency-banner" @click="showEmergency">
      <view class="emer-icon">⚠️</view>
      <view class="emer-text">
        <text class="emer-title">紧急情况？请立即拨打120</text>
      </view>
      <text class="emer-arrow">›</text>
    </view>

    <!-- 消息列表 -->
    <scroll-view scroll-y class="msg-area" :scroll-top="scrollTop">
      <view v-for="(msg, i) in messages" :key="i" :class="['msg-row', msg.role === 'user' ? 'msg-user' : 'msg-ai']">
        <view v-if="msg.role === 'ai'" class="msg-avatar">🤖</view>
        <view :class="['msg-bubble', msg.role === 'user' ? 'bubble-user' : 'bubble-ai']">
          <text class="msg-text">{{ msg.content }}</text>
        </view>
        <view v-if="msg.role === 'user'" class="msg-avatar">👤</view>
      </view>

      <!-- 快捷问题（仅初始状态） -->
      <view v-if="messages.length === 1" class="quick-qs">
        <text class="quick-label">快捷问题</text>
        <view class="quick-tags">
          <text v-for="q in quickQuestions" :key="q" class="quick-tag" @click="sendMessage(q)">{{ q }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 输入栏 -->
    <view class="input-bar">
      <input
        v-model="inputText"
        class="msg-input"
        placeholder="输入您的健康问题..."
        :adjust-position="false"
        confirm-type="send"
        @confirm="sendMessage(inputText)"
      />
      <view class="send-btn" @click="sendMessage(inputText)">
        <text class="send-icon">↑</text>
      </view>
    </view>

    <!-- 紧急弹窗 -->
    <Modal :visible="showEmerModal" @close="showEmerModal = false">
      <view class="modal-inner">
        <view class="emer-modal-icon">🚨</view>
        <text class="emer-modal-title">紧急医疗情况</text>
        <text class="emer-modal-body">如果您正在经历胸痛、呼吸困难、严重创伤等紧急情况，请立即拨打120急救电话。AI健康助手不能替代紧急医疗救助。</text>
        <button class="btn-primary" @click="showEmerModal = false">我知道了</button>
      </view>
    </Modal>
  </view>
</template>

<script setup lang="ts">
import NavHeader from '@/components/NavHeader/NavHeader.vue'
import Modal from '@/components/Modal/Modal.vue'
import { ref, reactive, nextTick } from 'vue'

const inputText = ref('')
const scrollTop = ref(0)
const showEmerModal = ref(false)

const messages = reactive<Array<{ role: string; content: string }>>([
  { role: 'ai', content: '您好！我是您的AI健康助手，很高兴为您服务。请问有什么健康问题需要咨询吗？' },
])

const quickQuestions = [
  '感冒发烧怎么办',
  '头痛是什么原因',
  '如何改善睡眠',
  '血压高怎么办',
  '皮肤过敏处理',
]

const sendMessage = (text: string) => {
  const msg = text.trim()
  if (!msg) return
  messages.push({ role: 'user', content: msg })
  inputText.value = ''

  // 模拟AI回复
  setTimeout(() => {
    const replies = [
      '感谢您的咨询。根据您描述的情况，建议您注意休息，多喝水，观察症状变化。如果持续不适，请及时就医。',
      '这是一个常见健康问题。建议您保持良好的生活习惯：规律作息、均衡饮食、适量运动。',
      '我理解您的担忧。请放心，我会为您提供基于循证医学的建议。注意：AI建议仅供参考，不能替代专业医生诊断。',
    ]
    messages.push({ role: 'ai', content: replies[Math.floor(Math.random() * replies.length)] })
    scrollToBottom()
  }, 800)

  nextTick(() => scrollToBottom())
}

const scrollToBottom = () => {
  scrollTop.value = 99999
}

const showEmergency = () => {
  showEmerModal.value = true
}

const goBack = () => {
  uni.navigateBack()
}
</script>

<style scoped>
.page { height: 100vh; display: flex; flex-direction: column; background: #F5F7FA; }

.emergency-banner {
  background: linear-gradient(135deg, #FF6B6B, #FF4757);
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.emer-icon { font-size: 20px; }
.emer-text { flex: 1; }
.emer-title { font-size: 13px; color: #FFFFFF; font-weight: 500; }
.emer-arrow { font-size: 18px; color: rgba(255,255,255,0.7); }

.msg-area {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.msg-row {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
  align-items: flex-start;
}
.msg-user { flex-direction: row-reverse; }
.msg-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.msg-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 16px;
  line-height: 1.5;
}
.bubble-ai { background: #FFFFFF; border-radius: 0 16px 16px 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.bubble-user { background: #4A90D9; border-radius: 16px 0 16px 16px; }
.msg-text { font-size: 14px; color: #1F2329; }
.bubble-user .msg-text { color: #FFFFFF; }

.quick-qs { margin-top: 16px; }
.quick-label { font-size: 13px; color: #8F959E; margin-bottom: 10px; display: block; }
.quick-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.quick-tag {
  padding: 10px 16px;
  background: #FFFFFF;
  border-radius: 20px;
  font-size: 13px;
  color: #646A73;
  border: 1px solid #E5E6EB;
}

.input-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: #FFFFFF;
  border-top: 1px solid #F0F1F5;
  padding-bottom: calc(10px + env(safe-area-inset-bottom));
}
.msg-input {
  flex: 1;
  height: 40px;
  background: #F5F7FA;
  border-radius: 20px;
  padding: 0 16px;
  font-size: 14px;
}
.send-btn {
  width: 40px;
  height: 40px;
  background: #4A90D9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.send-icon { color: #FFFFFF; font-size: 18px; font-weight: 700; }

.modal-inner { text-align: center; }
.emer-modal-icon { font-size: 48px; margin-bottom: 12px; }
.emer-modal-title { font-size: 18px; font-weight: 600; color: #1F2329; display: block; margin-bottom: 8px; }
.emer-modal-body { font-size: 14px; color: #646A73; line-height: 1.7; display: block; margin-bottom: 20px; }
.btn-primary {
  width: 100%;
  background: linear-gradient(135deg, #FF6B6B, #FF4757);
  color: #FFFFFF;
  border: none;
  padding: 12px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
}
</style>
