import { request } from './request'

export const authApi = {
  sendSmsCode: (phone: string) => request<void>({
    url: '/api/v1/auth/send-code',
    method: 'POST',
    data: { phone },
  }),
  loginBySms: (phone: string, code: string) => request<{ token: string }>({
    url: '/api/v1/auth/login',
    method: 'POST',
    data: { phone, code },
  }),
  loginByPassword: (phone: string, password: string) => request<{ token: string }>({
    url: '/api/v1/auth/login/password',
    method: 'POST',
    data: { phone, password },
  }),
  logout: () => request<void>({
    url: '/api/v1/auth/logout',
    method: 'POST',
  }),
  updatePassword: (oldPassword: string, newPassword: string) => request<void>({
    url: '/api/v1/auth/password',
    method: 'POST',
    data: { oldPassword, newPassword },
  }),
  refreshToken: (refreshToken: string) => request<{ accessToken: string; refreshToken: string }>({
    url: '/api/v1/auth/refresh',
    method: 'POST',
    headers: { Authorization: `Bearer ${refreshToken}` },
  }),
}
