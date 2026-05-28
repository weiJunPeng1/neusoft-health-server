import request from './request'

export interface LoginParams {
  phone: string
  password: string
}

export interface LoginResult {
  userId: number
  nickname: string
  avatarUrl: string
  accessToken: string
  refreshToken: string
  expiresIn: number
}

export interface UserInfo {
  id: number
  phone: string
  nickname: string
  avatarUrl: string
  gender: number
  age: number
  loginType: string
  disclaimerAccepted: number
  lastLoginTime: string
  status: number
  createdTime: string
  hasPassword: boolean
  roles: string[]
}

export const authApi = {
  login(data: LoginParams) {
    return request.post<LoginResult>('/auth/login/password', data)
  },

  getUserInfo() {
    return request.get<UserInfo>('/user/profile')
  },

  logout() {
    return request.post('/auth/logout')
  }
}
