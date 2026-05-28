<template>
  <div class="faq-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>常见问题管理</span>
          <el-button type="primary" @click="handleAdd">新增FAQ</el-button>
        </div>
      </template>

      <el-table :data="faqList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="question" label="问题" show-overflow-tooltip />
        <el-table-column prop="presetAnswer" label="答案" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑FAQ' : '新增FAQ'" width="600">
      <el-form :model="form" label-width="80px">
        <el-form-item label="问题" required>
          <el-input v-model="form.question" placeholder="请输入问题" />
        </el-form-item>
        <el-form-item label="答案" required>
          <el-input v-model="form.presetAnswer" type="textarea" :rows="4" placeholder="请输入答案" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
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
const faqList = ref<any[]>([])
const categories = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)
const form = ref({
  question: '',
  presetAnswer: '',
  categoryId: null as number | null,
  sortOrder: 0
})

async function fetchFaqs() {
  loading.value = true
  try {
    const data = await adminApi.getFaqList({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (Array.isArray(data)) {
      faqList.value = data
      total.value = data.length
    } else {
      faqList.value = (data as any)?.list || []
      total.value = (data as any)?.total || 0
    }
  } catch (error) {
    console.error('获取FAQ列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const data = await adminApi.getFaqCategories()
    categories.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

function handleSizeChange() {
  currentPage.value = 1
  fetchFaqs()
}

function handleCurrentChange() {
  fetchFaqs()
}

function handleAdd() {
  isEdit.value = false
  currentId.value = null
  form.value = {
    question: '',
    presetAnswer: '',
    categoryId: null,
    sortOrder: 0
  }
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  currentId.value = row.id
  form.value = {
    question: row.question,
    presetAnswer: row.presetAnswer || '',
    categoryId: row.categoryId,
    sortOrder: row.sortOrder
  }
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除这条FAQ吗？', '确认删除', {
      type: 'warning'
    })
    await adminApi.deleteFaq(row.id)
    ElMessage.success('删除成功')
    fetchFaqs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

async function handleSubmit() {
  if (!form.value.question || !form.value.presetAnswer || !form.value.categoryId) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    if (isEdit.value && currentId.value) {
      await adminApi.updateFaq({ id: currentId.value, ...form.value } as any)
      ElMessage.success('更新成功')
    } else {
      await adminApi.createFaq(form.value as any)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchFaqs()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchFaqs()
  fetchCategories()
})
</script>

<style scoped>
.faq-management {
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
