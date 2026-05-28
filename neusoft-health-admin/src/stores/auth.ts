import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { UserInfo } from '@/api/auth'

const TOKEN_KEY = 'admin_token'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<UserInfo | null>(null)

  const roles = computed(() => user.value?.roles || [])

  function hasRole(role: string): boolean {
    return roles.value.includes(role)
  }

  function hasAnyRole(roleList: string[]): boolean {
    return roleList.some(r => roles.value.includes(r))
  }

  function isSuperAdmin(): boolean {
    return hasRole('R002')
  }

  function isSystemAdmin(): boolean {
    return hasRole('R003')
  }

  function isContentReviewer(): boolean {
    return hasRole('R004')
  }

  async function login(phone: string, password: string) {
    const result = await authApi.login({ phone, password })
    token.value = result.accessToken
    localStorage.setItem(TOKEN_KEY, result.accessToken)
    return result
  }

  async function getUserInfo() {
    const info = await authApi.getUserInfo()
    user.value = info
    return info
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  return {
    token,
    user,
    roles,
    hasRole,
    hasAnyRole,
    isSuperAdmin,
    isSystemAdmin,
    isContentReviewer,
    login,
    getUserInfo,
    logout
  }
})
