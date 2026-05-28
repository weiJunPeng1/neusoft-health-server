<template>
  <div class="user-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchPhone"
              placeholder="搜索手机号"
              style="width: 200px; margin-right: 12px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 12px" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="140">
          <template #default="{ row }">
            {{ maskPhone(row.phone) }}
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
        <el-table-column prop="createdTime" label="注册时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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

    <el-dialog v-model="dialogVisible" title="用户详情" width="600">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentUser?.id }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser?.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ maskPhone(currentUser?.phone || '') }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ currentUser?.gender === 1 ? '男' : currentUser?.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser?.status === 1 ? 'success' : 'danger'">
            {{ currentUser?.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser?.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="最后登录" :span="2">{{ currentUser?.lastLoginTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'
import type { UserInfo } from '@/api/admin'

const loading = ref(false)
const userList = ref<UserInfo[]>([])
const searchPhone = ref('')
const statusFilter = ref<number | ''>('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const dialogVisible = ref(false)
const currentUser = ref<UserInfo | null>(null)

function maskPhone(phone: string): string {
  if (!phone || phone.length < 7) return phone
  return phone.substring(0, 3) + '******' + phone.substring(phone.length - 2)
}

async function fetchUsers() {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (searchPhone.value) params.phone = searchPhone.value
    if (statusFilter.value !== '') params.status = statusFilter.value

    const data = await adminApi.getUserList(params)
    if (Array.isArray(data)) {
      userList.value = data
      total.value = data.length
    } else {
      userList.value = (data as any)?.list || []
      total.value = (data as any)?.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchUsers()
}

function handleSizeChange() {
  currentPage.value = 1
  fetchUsers()
}

function handleCurrentChange() {
  fetchUsers()
}

function handleView(row: UserInfo) {
  currentUser.value = row
  dialogVisible.value = true
}

async function handleToggleStatus(row: UserInfo) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${row.nickname} 吗？`, '确认操作', {
      type: 'warning'
    })
    await adminApi.updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
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
</style>
