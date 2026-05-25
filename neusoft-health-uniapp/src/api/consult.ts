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
}
