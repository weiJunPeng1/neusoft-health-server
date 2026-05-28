<template>
  <div class="log-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <div class="header-actions">
            <el-select v-model="actionFilter" placeholder="操作类型" style="width: 150px; margin-right: 12px" clearable>
              <el-option label="登录" value="LOGIN" />
              <el-option label="登出" value="LOGOUT" />
              <el-option label="创建" value="CREATE" />
              <el-option label="更新" value="UPDATE" />
              <el-option label="删除" value="DELETE" />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="logList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operation" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getActionType(row.operation)" size="small">
              {{ row.operation }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="目标类型" width="100" />
        <el-table-column prop="targetId" label="目标ID" width="80" />
        <el-table-column prop="requestParams" label="详情" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="createdTime" label="操作时间" width="180" />
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
import { adminApi } from '@/api/admin'

const loading = ref(false)
const logList = ref<any[]>([])
const actionFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

function getActionType(action: string) {
  const map: Record<string, string> = {
    LOGIN: 'success',
    LOGOUT: 'info',
    CREATE: 'primary',
    UPDATE: 'warning',
    DELETE: 'danger'
  }
  return map[action] || 'info'
}

async function fetchLogs() {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (actionFilter.value) params.action = actionFilter.value

    const data = await adminApi.getOperationLogs(params)
    if (Array.isArray(data)) {
      logList.value = data
      total.value = data.length
    } else {
      logList.value = (data as any)?.list || []
      total.value = (data as any)?.total || 0
    }
  } catch (error) {
    console.error('获取日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchLogs()
}

function handleSizeChange() {
  currentPage.value = 1
  fetchLogs()
}

function handleCurrentChange() {
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.log-management {
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
