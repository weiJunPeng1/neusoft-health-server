import { request } from './request'
import type { SubscriptionPlan, PaymentOrder } from '@/types'

export const paymentApi = {
  getPlans: () => request<SubscriptionPlan[]>({ url: '/api/v1/payment/plans' }),
  createOrder: (planCode: string) => request<PaymentOrder>({
    url: '/api/v1/payment/order',
    method: 'POST',
    data: { planCode },
  }),
  queryOrder: (orderNo: string) => request<PaymentOrder>({ url: `/api/v1/payment/order/${orderNo}` }),
  cancelOrder: (orderNo: string) => request<void>({
    url: `/api/v1/payment/order/${orderNo}/cancel`,
    method: 'POST',
  }),
  applyRefund: (orderNo: string, reason: string) => request<void>({
    url: '/api/v1/payment/refund/apply',
    method: 'POST',
    data: { orderNo, reason },
  }),
  getMyRefunds: () => request<any[]>({ url: '/api/v1/payment/refund/my' }),
}
