<template>
  <div class="permissions-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>权限配置</span>
          <el-button type="primary" @click="handleAdd">新增权限</el-button>
        </div>
      </template>

      <el-table :data="permissionList" style="width: 100%" row-key="id" default-expand-all>
        <el-table-column prop="permCode" label="权限编码" width="200" />
        <el-table-column prop="permName" label="权限名称" width="150" />
        <el-table-column prop="permType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.permType)" size="small">
              {{ getTypeText(row.permType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="500">
      <el-form :model="form" label-width="80px">
        <el-form-item label="权限编码" required>
          <el-input v-model="form.permCode" placeholder="如 user:list" />
        </el-form-item>
        <el-form-item label="权限名称" required>
          <el-input v-model="form.permName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="form.permType" placeholder="请选择类型">
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="数据" value="data" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级权限">
          <el-tree-select
            v-model="form.parentId"
            :data="permissionTree"
            :props="{ label: 'permName', value: 'id' }"
            placeholder="请选择上级权限"
            clearable
          />
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
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const permissionList = ref([
  {
    id: 1,
    permCode: 'user',
    permName: '用户管理',
    permType: 'menu',
    parentId: 0,
    sortOrder: 1,
    children: [
      { id: 11, permCode: 'user:list', permName: '用户列表', permType: 'button', parentId: 1, sortOrder: 1 },
      { id: 12, permCode: 'user:create', permName: '创建用户', permType: 'button', parentId: 1, sortOrder: 2 },
      { id: 13, permCode: 'user:edit', permName: '编辑用户', permType: 'button', parentId: 1, sortOrder: 3 },
      { id: 14, permCode: 'user:delete', permName: '删除用户', permType: 'button', parentId: 1, sortOrder: 4 }
    ]
  },
  {
    id: 2,
    permCode: 'content',
    permName: '内容管理',
    permType: 'menu',
    parentId: 0,
    sortOrder: 2,
    children: [
      { id: 21, permCode: 'content:review', permName: '内容审核', permType: 'button', parentId: 2, sortOrder: 1 },
      { id: 22, permCode: 'content:faq', permName: 'FAQ管理', permType: 'button', parentId: 2, sortOrder: 2 },
      { id: 23, permCode: 'content:sensitive', permName: '敏感词管理', permType: 'button', parentId: 2, sortOrder: 3 }
    ]
  },
  {
    id: 3,
    permCode: 'config',
    permName: '系统配置',
    permType: 'menu',
    parentId: 0,
    sortOrder: 3,
    children: [
      { id: 31, permCode: 'config:edit', permName: '编辑配置', permType: 'button', parentId: 3, sortOrder: 1 }
    ]
  }
])

const permissionTree = computed(() => permissionList.value)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  permCode: '',
  permName: '',
  permType: 'menu',
  parentId: 0,
  sortOrder: 0
})

function getTypeTag(type: string) {
  const map: Record<string, string> = {
    menu: '',
    button: 'success',
    data: 'warning'
  }
  return map[type] || 'info'
}

function getTypeText(type: string) {
  const map: Record<string, string> = {
    menu: '菜单',
    button: '按钮',
    data: '数据'
  }
  return map[type] || '未知'
}

function handleAdd() {
  isEdit.value = false
  form.value = {
    permCode: '',
    permName: '',
    permType: 'menu',
    parentId: 0,
    sortOrder: 0
  }
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  form.value = {
    permCode: row.permCode,
    permName: row.permName,
    permType: row.permType,
    parentId: row.parentId,
    sortOrder: row.sortOrder
  }
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除这个权限吗？', '确认删除', {
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
  if (!form.value.permCode || !form.value.permName) {
    ElMessage.warning('请填写完整信息')
    return
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
}
</script>

<style scoped>
.permissions-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
