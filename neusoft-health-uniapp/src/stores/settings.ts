import { reactive } from 'vue'
import { userApi } from '@/api/user'
import type { UserSettings } from '@/types'

const defaultSettings: UserSettings = {
  notificationEnabled: 1,
  voiceEnabled: 1,
  voiceSpeed: 1.0,
  voiceVolume: 80,
  voiceTone: 'default',
  anonymousMode: 0,
  privacyMode: 0,
  recommendEnabled: 1,
  autoSyncHealthProfile: 1
}

export const useSettingsStore = reactive({
  settings: { ...defaultSettings } as UserSettings,
  loaded: false,

  get voiceEnabled() {
    return !!this.settings.voiceEnabled
  },

  get voiceSpeed() {
    return this.settings.voiceSpeed || 1.0
  },

  async load() {
    try {
      const res = await userApi.getSettings()
      if (res.data) {
        Object.assign(this.settings, res.data)
      }
      this.loaded = true
    } catch (err) {
      console.error('加载设置失败', err)
    }
  },

  async update(key: keyof UserSettings, value: any) {
    (this.settings as any)[key] = value
    try {
      await userApi.updateSettings({ [key]: value })
    } catch (err) {
      console.error('更新设置失败', err)
    }
  },

  toggle(key: keyof UserSettings) {
    const current = (this.settings as any)[key]
    this.update(key, current ? 0 : 1)
  }
})
