<template>
  <div class="sensitive-words-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>敏感词管理</span>
          <el-button type="primary" @click="handleAdd">新增敏感词</el-button>
        </div>
      </template>

      <el-table :data="wordList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="word" label="敏感词" width="200" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.severity === 2 ? 'danger' : 'warning'" size="small">
              {{ row.severity === 2 ? '高风险' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="100">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增敏感词" width="400">
      <el-form :model="form" label-width="80px">
        <el-form-item label="敏感词" required>
          <el-input v-model="form.word" placeholder="请输入敏感词" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="政治" value="政治" />
            <el-option label="暴力" value="暴力" />
            <el-option label="色情" value="色情" />
            <el-option label="医疗风险" value="医疗风险" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-radio-group v-model="form.severity">
            <el-radio :value="1">普通</el-radio>
            <el-radio :value="2">高风险</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const wordList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const dialogVisible = ref(false)
const form = ref({
  word: '',
  category: '',
  severity: 1
})

async function fetchWords() {
  loading.value = true
  try {
    const data = await adminApi.getSensitiveWordList({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (Array.isArray(data)) {
      wordList.value = data
      total.value = data.length
    } else {
      wordList.value = (data as any)?.list || []
      total.value = (data as any)?.total || 0
    }
  } catch (error) {
    console.error('获取敏感词列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSizeChange() {
  currentPage.value = 1
  fetchWords()
}

function handleCurrentChange() {
  fetchWords()
}

function handleAdd() {
  form.value = {
    word: '',
    category: '',
    severity: 1
  }
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除这个敏感词吗？', '确认删除', {
      type: 'warning'
    })
    await adminApi.deleteSensitiveWord(row.id)
    ElMessage.success('删除成功')
    fetchWords()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

async function handleSubmit() {
  if (!form.value.word || !form.value.category) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    await adminApi.createSensitiveWord(form.value)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    fetchWords()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

onMounted(() => {
  fetchWords()
})
</script>

<style scoped>
.sensitive-words-management {
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
