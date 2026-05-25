import { reactive } from 'vue'
import type { User, MemberStatus } from '@/types'

export const useUserStore = reactive({
  user: null as User | null,
  token: uni.getStorageSync('token') || '',
  memberStatus: null as MemberStatus | null,

  get isLoggedIn() {
    return !!uni.getStorageSync('token')
  },

  get memberLevel() {
    return this.memberStatus?.levelCode || 'L0'
  },

  login(token: string) {
    this.token = token
    uni.setStorageSync('token', token)
  },

  logout() {
    this.token = ''
    this.user = null
    this.memberStatus = null
    uni.removeStorageSync('token')
    uni.reLaunch({ url: '/pages/login/index' })
  },

  setUser(user: User) {
    this.user = user
  },

  setMemberStatus(status: MemberStatus) {
    this.memberStatus = status
  },
})
