// 用户信息
export interface UserProfile {
  id: number
  phone: string
  nickname: string
  avatar: string
  gender: number
  birthday: string
  memberLevel: string
  memberExpireTime: string
  status: number
}

// 会员状态
export interface MemberStatus {
  levelCode: string
  levelName: string
  dailyQuota: number
  contextRounds: number
  autoSync: boolean
  deepAnalysis: boolean
  exportEnabled: boolean
  expireTime: string
  remainingDays: number
  inGracePeriod: boolean
}

// 会员等级
export interface MemberLevelVO {
  levelCode: string
  levelName: string
  dailyQuota: number
  contextRounds: number
  autoSync: boolean
  deepAnalysis: boolean
  exportEnabled: boolean
  description: string
}

// 订阅方案
export interface SubscriptionPlan {
  planCode: string
  planName: string
  levelCode: string
  durationDays: number
  price: number
  originalPrice: number
  isFirstMonthPrice: boolean
}

// 支付订单
export interface PaymentOrder {
  orderNo: string
  planName: string
  amount: number
  status: 'PENDING' | 'PAID' | 'CANCELLED' | 'REFUNDING' | 'REFUNDED'
  createdTime: string
  paidTime: string
}

// 健康档案
export interface HealthProfile {
  height?: number
  weight?: number
  bloodType?: string
  allergies?: string
  medicalHistory?: string
  medicationHistory?: string
  familyHistory?: string
}

// 用户设置
export interface UserSettings {
  notificationEnabled: boolean
  voiceEnabled: boolean
  voiceSpeed: number
  privacyMode: boolean
}

// 收藏
export interface UserFavorite {
  id: number
  messageId: number
  sessionId: number
  content: string
  createdTime: string
}

// 登录日志
export interface LoginLog {
  id: number
  ipAddress: string
  device: string
  loginTime: string
}

// 咨询会话
export interface ConsultSession {
  id: number
  title: string
  lastMessage: string
  messageCount: number
  createdTime: string
  updatedTime: string
}

// 咨询消息
export interface ConsultMessage {
  id: number
  sessionId: number
  role: 'user' | 'assistant'
  content: string
  createdTime: string
}

// FAQ分类
export interface FaqCategory {
  id: number
  name: string
  icon: string
  faqCount: number
  children?: FaqCategory[]
}

// FAQ条目
export interface FaqItem {
  id: number
  categoryId: number
  question: string
  answer: string
  sortOrder: number
}

// 邀请信息
export interface InviteInfo {
  inviteCode: string
  inviteCount: number
  rewardAmount: number
}

// 退款记录
export interface RefundRequest {
  id: number
  orderNo: string
  amount: number
  reason: string
  status: string
  createdTime: string
}

// API 通用响应
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}
