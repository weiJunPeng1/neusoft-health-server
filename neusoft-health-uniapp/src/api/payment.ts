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
  getOrders: () => request<PaymentOrder[]>({ url: '/api/v1/payment/orders' }),
  getQrCode: (orderNo: string) => request<{ qrCode: string; orderNo: string }>({
    url: `/api/v1/payment/qrcode/${orderNo}`,
  }),
  createPagePayUrl: (orderNo: string) => request<{ pagePayUrl: string; orderNo: string }>({
    url: `/api/v1/payment/page-pay/${orderNo}`,
  }),
  queryAlipayOrder: (orderNo: string) => request<{ orderNo: string; alipayResult: string }>({
    url: `/api/v1/payment/alipay/query/${orderNo}`,
  }),
  closeAlipayOrder: (orderNo: string) => request<{ orderNo: string; tradeNo: string }>({
    url: `/api/v1/payment/alipay/close/${orderNo}`,
    method: 'POST',
  }),
  alipayRefund: (orderNo: string, refundRequestNo: string, refundAmount: string, reason?: string) => request<{ orderNo: string; tradeNo: string; refundRequestNo: string }>({
    url: '/api/v1/payment/alipay/refund',
    method: 'POST',
    data: { orderNo, refundRequestNo, refundAmount, reason },
  }),
  queryAlipayRefund: (orderNo: string, refundRequestNo: string) => request<{ orderNo: string; refundRequestNo: string; alipayResult: string }>({
    url: '/api/v1/payment/alipay/refund/query',
    params: { orderNo, refundRequestNo },
  }),
  getBillDownloadUrl: (billType: string, billDate: string) => request<{ billType: string; billDate: string; downloadUrl: string }>({
    url: '/api/v1/payment/alipay/bill/download-url',
    params: { billType, billDate },
  }),
}
