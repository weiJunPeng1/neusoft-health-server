import type { ApiResponse } from '@/types'
import { useUserStore } from '@/stores/user'

const getBaseUrl = () => {
  // #ifdef H5
  const { protocol, hostname } = window.location
  return `${protocol}//${hostname}:8080`
  // #endif
  // #ifndef H5
  return 'http://localhost:8080'
  // #endif
}

const BASE_URL = getBaseUrl()

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
  timeout?: number
}

export const request = <T = any>(options: RequestOptions): Promise<ApiResponse<T>> => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      timeout: options.timeout || 10000,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.header,
      },
      success: (res: any) => {
        const { statusCode, data } = res
        if (statusCode === 200 && data.code === 0) {
          resolve(data)
        } else if (statusCode === 401 || data.code === 401) {
          uni.removeStorageSync('token')
          useUserStore.logout()
          reject(data)
        } else {
          uni.showToast({ title: data.message || '请求失败', icon: 'none' })
          reject(data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      },
    })
  })
}