import { request } from './request'
import type { MemberStatus, MemberLevelVO } from '@/types'

export const memberApi = {
  getStatus: () => request<MemberStatus>({ url: '/api/v1/member/status' }),
  getLevels: () => request<MemberLevelVO[]>({ url: '/api/v1/member/levels' }),
  getHistory: () => request<any>({ url: '/api/v1/member/history' }),
}
