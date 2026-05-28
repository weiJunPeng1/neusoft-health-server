import request from './request'

export interface DashboardStats {
  totalUsers: number
  todayUsers: number
  totalSessions: number
  totalMessages: number
  todayMessages: number
  todaySessions: number
  pendingReviews: number
  emergencyCount: number
  categoryStats: CategoryStat[]
  memberStats: MemberStats
  hotConsults: HotConsult[]
  recentConsults: RecentConsult[]
}

export interface CategoryStat {
  name: string
  value: number
}

export interface MemberStats {
  totalMembers: number
  L0: number
  L1: number
  L2: number
  L3: number
}

export interface HotConsult {
  keyword: string
  hitCount: number
  source: number
}

export interface RecentConsult {
  id: number
  userId: number
  content: string
  createdAt: string
}

export interface TrendData {
  date: string
  count?: number
  sessions?: number
  messages?: number
}

export interface AiPerformance {
  avgResponseTime: number
  maxResponseTime: number
  minResponseTime: number
  totalAiCalls: number
}

export interface UserListParams {
  page?: number
  pageSize?: number
  phone?: string
  status?: number
}

export interface UserInfo {
  id: number
  phone: string
  nickname: string
  avatar: string
  gender: number
  status: number
  lastLoginTime: string
  createdTime: string
}

export const adminApi = {
  getDashboardStats() {
    return request.get<DashboardStats>('/admin/stats/dashboard')
  },

  getCategoryStats() {
    return request.get<CategoryStat[]>('/admin/stats/categories')
  },

  getUserTrend(days: number = 30) {
    return request.get<TrendData[]>('/admin/stats/user-trend', { params: { days } })
  },

  getConsultTrend(days: number = 30) {
    return request.get<TrendData[]>('/admin/stats/consult-trend', { params: { days } })
  },

  getMemberStats() {
    return request.get<MemberStats>('/admin/stats/member-stats')
  },

  getHotConsults(limit: number = 8) {
    return request.get<HotConsult[]>('/admin/stats/hot-consults', { params: { limit } })
  },

  getRecentConsults(limit: number = 10) {
    return request.get<RecentConsult[]>('/admin/stats/recent-consults', { params: { limit } })
  },

  getAiPerformance() {
    return request.get<AiPerformance>('/admin/stats/ai-performance')
  },

  getUserList(params: UserListParams) {
    return request.get('/admin/users/list', { params })
  },

  getUserDetail(id: number) {
    return request.get(`/admin/users/${id}`)
  },

  updateUserStatus(id: number, status: number) {
    return request.put(`/admin/users/${id}/status`, null, { params: { status } })
  },

  getReviewList(params: { page?: number; pageSize?: number }) {
    return request.get('/admin/review/pending', { params })
  },

  reviewMessage(data: { messageId: number; status: number; reason?: string }) {
    return request.post('/admin/review/message', data)
  },

  getFaqList(params: { page?: number; pageSize?: number; categoryId?: number }) {
    return request.get('/admin/faq/list', { params })
  },

  createFaq(data: { question: string; presetAnswer: string; categoryId: number }) {
    return request.post('/admin/faq/create', data)
  },

  updateFaq(data: { id: number; question: string; presetAnswer: string; categoryId: number }) {
    return request.put('/admin/faq', data)
  },

  deleteFaq(id: number) {
    return request.delete(`/admin/faq/${id}`)
  },

  getFaqCategories() {
    return request.get('/admin/faq/categories')
  },

  getSensitiveWordList(params: { page?: number; pageSize?: number }) {
    return request.get('/admin/sensitive-word', { params })
  },

  createSensitiveWord(data: { word: string; category: string; severity: number }) {
    return request.post('/admin/sensitive-word/create', data)
  },

  deleteSensitiveWord(id: number) {
    return request.delete(`/admin/sensitive-word/${id}`)
  },

  getOperationLogs(params: { page?: number; pageSize?: number; action?: string }) {
    return request.get('/admin/logs/list', { params })
  },

  getSystemConfig(configKey: string) {
    return request.get(`/admin/config/${configKey}`)
  },

  updateSystemConfig(data: { configKey: string; configValue: string }) {
    return request.put('/admin/config', data)
  },

  getMemberList(params: { page?: number; pageSize?: number }) {
    return request.get('/admin/member/users', { params })
  },

  getOrderList(params: { page?: number; pageSize?: number; status?: number }) {
    return request.get('/admin/payment/orders', { params })
  },

  getRefundList(params: { page?: number; pageSize?: number }) {
    return request.get('/admin/payment/refunds', { params })
  },

  approveRefund(id: number, remark?: string) {
    return request.post(`/admin/payment/refund/${id}/approve`, null, { params: { remark } })
  },

  rejectRefund(id: number, remark: string) {
    return request.post(`/admin/payment/refund/${id}/reject`, null, { params: { remark } })
  }
}
