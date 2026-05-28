import { request } from './request'
import type { ConsultSession, ConsultMessage, FaqCategory, FaqItem, HealthSearchResult } from '@/types'

export const consultApi = {
  // 会话管理
  createSession: (firstMessage: string) => request<ConsultSession>({
    url: '/api/v1/consultation/session',
    method: 'POST',
    data: { firstMessage },
  }),
  listSessions: () => request<ConsultSession[]>({ url: '/api/v1/consultation/sessions' }),
  deleteSession: (id: number) => request<void>({
    url: `/api/v1/consultation/session/${id}`,
    method: 'DELETE',
  }),

  // 消息
  sendMessage: (sessionId: number, content: string, healthProfile?: any) => request<ConsultMessage>({
    url: '/api/v1/consultation/message',
    method: 'POST',
    data: { sessionId, content, healthProfile },
    timeout: 60000,
  }),
  listMessages: (sessionId: number) => request<ConsultMessage[]>({
    url: `/api/v1/consultation/session/${sessionId}/messages`,
  }),

  // 紧急日志
  getEmergencyLogs: () => request<any[]>({ url: '/api/v1/consultation/emergency/logs' }),
  handleEmergency: (id: number) => request<void>({
    url: `/api/v1/consultation/emergency/${id}/handle`,
    method: 'POST',
  }),

  // FAQ
  getFaqCategories: () => request<FaqCategory[]>({ url: '/api/v1/faq/categories' }),
  getFaqCategoryTree: () => request<FaqCategory[]>({ url: '/api/v1/faq/categories/tree' }),
  getFaqsByCategory: (categoryId: number) => request<FaqItem[]>({ url: `/api/v1/faq/category/${categoryId}` }),

  // 健康搜索
  healthSearch: (keyword: string) => request<HealthSearchResult>({
    url: '/api/v1/health-search',
    method: 'POST',
    data: { keyword },
  }),

  // TTS 语音合成
  synthesize: (text: string) => request<{ audio: string; format: string }>({
    url: '/api/v1/tts/synthesize',
    method: 'POST',
    data: { text },
    timeout: 30000,
  }),

  synthesizeSse: async (text: string): Promise<ReadableStream<Uint8Array> | null> => {
    // #ifdef H5
    const token = uni.getStorageSync('token')
    const { protocol, hostname } = window.location
    const resp = await fetch(`${protocol}//${hostname}:8080/api/v1/tts/synthesize/sse`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify({ text }),
    })
    return resp.body as ReadableStream<Uint8Array>
    // #endif
    // #ifndef H5
    return null
    // #endif
  },
}
