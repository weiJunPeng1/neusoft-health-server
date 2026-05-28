<template>
  <div class="member-management">
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">会员总数</div>
            <div class="stat-value">{{ memberStats.totalMembers || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">L1 基础会员</div>
            <div class="stat-value">{{ memberStats.l1Count || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">L2 高级会员</div>
            <div class="stat-value">{{ memberStats.l2Count || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">L3 专业会员</div>
            <div class="stat-value">{{ memberStats.l3Count || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>会员列表</span>
          <el-button type="primary" @click="fetchMembers">刷新</el-button>
        </div>
      </template>

      <el-table :data="memberList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="用户" width="120" />
        <el-table-column prop="memberLevel" label="会员等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.memberLevel)" size="small">
              {{ getLevelText(row.memberLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="expireTime" label="到期时间" width="180" />
        <el-table-column label="会员状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '有效' : '已过期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleGrant(row)">续期</el-button>
            <el-button type="danger" link size="small" @click="handleRevoke(row)">撤销</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const memberList = ref<any[]>([])
const memberStats = ref<any>({})
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

function getLevelType(level: string) {
  const map: Record<string, string> = {
    L0: 'info',
    L1: '',
    L2: 'warning',
    L3: 'danger'
  }
  return map[level] || 'info'
}

function getLevelText(level: string) {
  const map: Record<string, string> = {
    L0: '普通用户',
    L1: '基础会员',
    L2: '高级会员',
    L3: '专业会员'
  }
  return map[level] || level
}

async function fetchMemberStats() {
  try {
    const data = await adminApi.getMemberStats()
    memberStats.value = data || {}
  } catch (error) {
    console.error('获取会员统计失败:', error)
  }
}

async function fetchMembers() {
  loading.value = true
  try {
    const data = await adminApi.getMemberList({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (Array.isArray(data)) {
      memberList.value = data
      total.value = data.length
    } else {
      memberList.value = (data as any)?.list || []
      total.value = (data as any)?.total || 0
    }
  } catch (error) {
    console.error('获取会员列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSizeChange() {
  currentPage.value = 1
  fetchMembers()
}

function handleCurrentChange() {
  fetchMembers()
}

async function handleGrant(row: any) {
  ElMessage.info('续期功能开发中')
}

async function handleRevoke(row: any) {
  try {
    await ElMessageBox.confirm(`确定要撤销用户 ${row.nickname} 的会员吗？`, '确认操作', {
      type: 'warning'
    })
    ElMessage.success('撤销成功')
    fetchMembers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤销失败')
    }
  }
}

onMounted(() => {
  fetchMemberStats()
  fetchMembers()
})
</script>

<style scoped>
.member-management {
  padding: 0;
}

.stat-row {
  margin-bottom: 16px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
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
