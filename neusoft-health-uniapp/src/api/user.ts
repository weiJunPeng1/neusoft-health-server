import { request } from './request'
import type { UserProfile, HealthProfile, UserSettings, UserFavorite, LoginLog, HomeData, Disclaimer } from '@/types'

export const systemApi = {
  getHomeData: () => request<HomeData>({ url: '/api/v1/home' }),
  acceptDisclaimer: () => request<void>({ url: '/api/v1/disclaimer/accept', method: 'POST' }),
}

export const userApi = {
  // 用户基本信息 (UserController)
  getProfile: () => request<UserProfile>({ url: '/api/v1/user/profile' }),
  updateProfile: (data: Partial<UserProfile>) => request<void>({
    url: '/api/v1/user/profile',
    method: 'PUT',
    data,
  }),
  getLoginLogs: () => request<LoginLog[]>({ url: '/api/v1/user/login-logs' }),

  // 健康档案 (UserProfileController)
  getHealthProfile: () => request<HealthProfile>({ url: '/api/v1/user/health-profile' }),
  updateHealthProfile: (data: Partial<HealthProfile>) => request<void>({
    url: '/api/v1/user/health-profile',
    method: 'PUT',
    data,
  }),

  // 用户设置 (UserProfileController)
  getSettings: () => request<UserSettings>({ url: '/api/v1/user/settings' }),
  updateSettings: (data: Partial<UserSettings>) => request<void>({
    url: '/api/v1/user/settings',
    method: 'PUT',
    data,
  }),

  // 收藏 (UserProfileController)
  toggleFavorite: (messageId: number) => request<boolean>({
    url: '/api/v1/user/favorite/toggle',
    method: 'POST',
    data: { messageId },
  }),
  getFavorites: () => request<UserFavorite[]>({ url: '/api/v1/user/favorites' }),
  checkFavorite: (messageId: number) => request<boolean>({
    url: `/api/v1/user/favorite/check?messageId=${messageId}`,
  }),
}
