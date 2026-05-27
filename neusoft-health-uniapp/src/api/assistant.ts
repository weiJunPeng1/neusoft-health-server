import { request } from './request'

export interface AssistantResponse {
  feature: string
  content: string
  disclaimer: string
}

export const assistantApi = {
  query: (feature: string, healthProfile?: string) => request<AssistantResponse>({
    url: '/api/v1/assistant/query',
    method: 'POST',
    data: { feature, healthProfile },
  }),
}
