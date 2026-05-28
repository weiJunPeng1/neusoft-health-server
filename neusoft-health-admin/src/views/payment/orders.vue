<template>
  <div class="order-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="activeTab" size="small" @change="handleTabChange">
            <el-radio-button label="orders">订单管理</el-radio-button>
            <el-radio-button label="refunds">退款审核</el-radio-button>
          </el-radio-group>
          <div class="header-actions" v-if="activeTab === 'orders'">
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 12px" clearable>
              <el-option label="待支付" :value="0" />
              <el-option label="已支付" :value="1" />
              <el-option label="已取消" :value="2" />
              <el-option label="退款中" :value="3" />
              <el-option label="已退款" :value="4" />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table v-if="activeTab === 'orders'" :data="pagedOrders" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="nickname" label="用户" width="120" />
        <el-table-column prop="planName" label="方案" width="150" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100">
          <template #default="{ row }">
            {{ row.payMethod === 'alipay' ? '支付宝' : '微信' }}
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.payStatus)" size="small">
              {{ getOrderStatusText(row.payStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column prop="paidTime" label="支付时间" width="180" />
      </el-table>

      <el-table v-if="activeTab === 'refunds'" :data="refundList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="userPhone" label="手机号" width="140" />
        <el-table-column prop="refundAmount" label="退款金额" width="100">
          <template #default="{ row }">
            ¥{{ row.refundAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="退款原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getRefundStatusType(row.status)" size="small">
              {{ getRefundStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handleRemark" label="处理备注" width="140" show-overflow-tooltip />
        <el-table-column prop="createdTime" label="申请时间" width="180" />
        <el-table-column label="操作" fixed="right" width="160" v-if="activeTab === 'refunds'">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" link size="small" @click="handleApprove(row)">通过</el-button>
              <el-button type="danger" link size="small" @click="handleReject(row)">拒绝</el-button>
            </template>
            <span v-else class="text-muted">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="activeTab === 'orders'">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredOrders.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="rejectDialogVisible" title="拒绝退款" width="400">
      <el-form>
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const activeTab = ref('orders')
const orderList = ref<any[]>([])
const refundList = ref<any[]>([])
const statusFilter = ref<number | ''>('')
const currentPage = ref(1)
const pageSize = ref(20)

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentRefundId = ref<number | null>(null)

const filteredOrders = computed(() => {
  if (statusFilter.value === '') return orderList.value
  return orderList.value.filter((o: any) => o.payStatus === statusFilter.value)
})

const pagedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredOrders.value.slice(start, start + pageSize.value)
})

function getOrderStatusType(status: number) {
  const map: Record<number, string> = {
    0: 'warning', 1: 'success', 2: 'info', 3: 'danger', 4: 'success', 5: 'danger'
  }
  return map[status] || 'info'
}

function getOrderStatusText(status: number) {
  const map: Record<number, string> = {
    0: '待支付', 1: '已支付', 2: '已取消', 3: '退款中', 4: '已退款', 5: '退款失败'
  }
  return map[status] || '未知'
}

function getRefundStatusType(status: number) {
  const map: Record<number, string> = {
    0: 'warning', 1: 'success', 2: 'danger', 3: 'success', 4: 'danger'
  }
  return map[status] || 'info'
}

function getRefundStatusText(status: number) {
  const map: Record<number, string> = {
    0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已到账', 4: '到账失败'
  }
  return map[status] || '未知'
}

async function fetchOrders() {
  loading.value = true
  try {
    const data = await adminApi.getOrderList({})
    orderList.value = Array.isArray(data) ? data : ((data as any)?.list || [])
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function fetchRefunds() {
  loading.value = true
  try {
    const data = await adminApi.getRefundList({})
    refundList.value = Array.isArray(data) ? data : ((data as any)?.list || [])
  } catch (error) {
    console.error('获取退款列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  currentPage.value = 1
  if (activeTab.value === 'orders') fetchOrders()
  else fetchRefunds()
}

function handleSearch() {
  currentPage.value = 1
}

function handleSizeChange() {
  currentPage.value = 1
}

function handleCurrentChange() {}

async function handleApprove(row: any) {
  try {
    await ElMessageBox.confirm(`确定通过退款 ¥${row.refundAmount?.toFixed(2)} 吗？`, '审核退款', { type: 'warning' })
    await adminApi.approveRefund(row.id)
    ElMessage.success('已通过')
    fetchRefunds()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

function handleReject(row: any) {
  currentRefundId.value = row.id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

async function confirmReject() {
  if (!rejectReason.value) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  try {
    await adminApi.rejectRefund(currentRefundId.value!, rejectReason.value)
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    fetchRefunds()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.text-muted {
  color: #999;
  font-size: 12px;
}
</style>
