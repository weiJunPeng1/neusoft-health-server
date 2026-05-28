<template>
  <div class="roles-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-table :data="roleList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleCode" label="角色编码" width="120" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handlePermission(row)">权限</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500">
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色编码" required>
          <el-input v-model="form.roleCode" placeholder="如 R005" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="角色名称" required>
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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

const loading = ref(false)
const roleList = ref<any[]>([
  { id: 1, roleCode: 'R001', roleName: '普通用户', description: 'C端普通用户', status: 1 },
  { id: 2, roleCode: 'R002', roleName: '超级管理员', description: '系统全面管理权限', status: 1 },
  { id: 3, roleCode: 'R003', roleName: '系统管理员', description: '用户管理/配置/统计', status: 1 },
  { id: 4, roleCode: 'R004', roleName: '内容审核员', description: '咨询审核/FAQ管理', status: 1 }
])

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  roleCode: '',
  roleName: '',
  description: ''
})

function handleAdd() {
  isEdit.value = false
  form.value = {
    roleCode: '',
    roleName: '',
    description: ''
  }
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  form.value = {
    roleCode: row.roleCode,
    roleName: row.roleName,
    description: row.description
  }
  dialogVisible.value = true
}

function handlePermission(row: any) {
  ElMessage.info('权限配置功能开发中')
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除这个角色吗？', '确认删除', {
      type: 'warning'
    })
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

function handleSubmit() {
  if (!form.value.roleCode || !form.value.roleName) {
    ElMessage.warning('请填写完整信息')
    return
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
}
</script>

<style scoped>
.roles-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
