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
  hasPassword: boolean
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
    id?: number
    planCode: string
    planName: string
    levelCode: string
    levelName?: string
    durationDays: number
    price: number
    originalPrice: number
    isFirstPurchasePrice?: boolean
}

// 支付订单
export interface PaymentOrder {
    orderNo: string
    planId?: number
    planName: string
    amount: number
    payMethod?: string
    payStatus: 0 | 1 | 2 | 3 | 4 | 5
    payStatusDesc?: string
    createdTime: string
    paidTime?: string
    expireTime?: string
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
  notificationEnabled: number
  voiceEnabled: number
  voiceSpeed: number
  voiceVolume: number
  voiceTone: string
  anonymousMode: number
  privacyMode: number
  recommendEnabled: number
  autoSyncHealthProfile: number
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

// 首页数据（后端实际返回）
export interface HomeData {
  faqCategories: FaqCategory[]
  articles: HealthArticle[]
  disclaimer: string
}

// FAQ分类（含FAQ列表）
export interface FaqCategory {
  id: number
  name: string
  icon: string
  sortOrder: number
  faqList: FaqItem[]
}

// FAQ条目
export interface FaqItem {
  id: number
  categoryId: number
  question: string
  presetAnswer: string
  sortOrder: number
}

// 健康资讯
export interface HealthArticle {
  id: number
  title: string
  summary: string
  coverUrl: string
  contentUrl: string
  sortOrder: number
  createdTime: string
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

// 健康搜索结果
export interface HealthSearchResult {
  id: number
  keyword: string
  content: string
  source: number
  disclaimer: string
}

// 免责声明
export interface Disclaimer {
  id: number
  content: string
  version: string
  updatedTime: string
}

// API 通用响应
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}