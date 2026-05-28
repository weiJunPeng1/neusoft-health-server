<template>
  <div class="review-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>内容审核 <el-badge v-if="total > 0" :value="total" type="warning" style="margin-left: 8px" /></span>
          <div class="header-actions">
            <el-tag v-if="autoRefreshing" type="success" size="small" style="margin-right: 8px">自动刷新中</el-tag>
            <el-button type="primary" @click="fetchReviews">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="reviewList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="用户" width="100" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="role" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.role === 'user' ? 'primary' : 'success'" size="small">
              {{ row.role === 'user' ? '用户' : 'AI' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewStatus" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getReviewStatusType(row.reviewStatus)" size="small">
              {{ getReviewStatusText(row.reviewStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <template v-if="row.reviewStatus === 0">
              <el-button type="success" link size="small" @click="handleReview(row, 1)">通过</el-button>
              <el-button type="danger" link size="small" @click="handleReview(row, 2)">违规</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="消息详情" width="600">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户">{{ currentMessage?.nickname }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ currentMessage?.role === 'user' ? '用户' : 'AI' }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentMessage?.content }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ currentMessage?.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getReviewStatusType(currentMessage?.reviewStatus)">
            {{ getReviewStatusText(currentMessage?.reviewStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="rejectDialogVisible" title="标记违规" width="400">
      <el-form>
        <el-form-item label="违规原因">
          <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入违规原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认违规</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const reviewList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const autoRefreshing = ref(true)
let refreshTimer: ReturnType<typeof setInterval> | null = null

const dialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const currentMessage = ref<any>(null)
const rejectReason = ref('')

function getReviewStatusType(status: number) {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return map[status] || 'info'
}

function getReviewStatusText(status: number) {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '违规'
  }
  return map[status] || '未知'
}

async function fetchReviews() {
  loading.value = true
  try {
    const data = await adminApi.getReviewList({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (Array.isArray(data)) {
      reviewList.value = data
      total.value = data.length
    } else {
      reviewList.value = (data as any)?.list || []
      total.value = (data as any)?.total || 0
    }
  } catch (error) {
    console.error('获取审核列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSizeChange() {
  currentPage.value = 1
  fetchReviews()
}

function handleCurrentChange() {
  fetchReviews()
}

function handleView(row: any) {
  currentMessage.value = row
  dialogVisible.value = true
}

async function handleReview(row: any, status: number) {
  if (status === 2) {
    currentMessage.value = row
    rejectReason.value = ''
    rejectDialogVisible.value = true
    return
  }

  try {
    await adminApi.reviewMessage({
      messageId: row.id,
      status: 1
    })
    ElMessage.success('审核通过，AI已生成回复')
    fetchReviews()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

async function confirmReject() {
  if (!rejectReason.value) {
    ElMessage.warning('请输入违规原因')
    return
  }

  try {
    await adminApi.reviewMessage({
      messageId: currentMessage.value.id,
      status: 2,
      reason: rejectReason.value
    })
    ElMessage.success('已标记违规，用户将收到违规提示')
    rejectDialogVisible.value = false
    fetchReviews()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchReviews()
  refreshTimer = setInterval(fetchReviews, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
})
</script>

<style scoped>
.review-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
