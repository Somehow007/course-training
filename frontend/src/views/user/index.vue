<template>
  <div class="user-page">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="所属学院">
          <CollegeSelect v-model="searchForm.collegeId" placeholder="请选择学院" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button v-permission="'100'" type="primary" @click="handleAdd">新增用户</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe class="table-flex">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="collegeName" label="所属学院" />
        <el-table-column prop="dictName" label="用户身份" />
        <el-table-column label="用户状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.delFlag === 1 ? 'danger' : 'success'">
              {{ row.delFlag === 1 ? '禁用' : '启用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button 
              v-permission="'100'" 
              :type="row.delFlag === 1 ? 'success' : 'danger'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.delFlag === 1 ? '启用' : '禁用' }}
            </el-button>
            <el-button
              v-permission="'100'"
              type="primary"
              link
              @click="handleResetPassword(row)"
            >
              重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页容器 -->
      <div class="pagination-container">
        <Pagination
          v-model:current="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :loading="loading"
          @change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增用户对话框 -->
    <el-dialog v-model="dialogVisible" title="新增用户" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="所属学院">
          <CollegeSelect v-model="form.collegeId" placeholder="请选择学院" clearable />
        </el-form-item>
        <el-form-item label="用户身份" prop="dictId">
          <DictSelect v-model="form.dictId" dict-type="user_state" placeholder="请选择身份" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <ResetPasswordDialog
      v-model="resetPasswordVisible"
      :user-id="selectedUserId"
      :username="selectedUsername"
      @success="loadData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { userApi } from '@/api/user'
import type { UserPageItem } from '@/types/api'
import { usePagination } from '@/composables/usePagination'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import DictSelect from '@/components/business/DictSelect.vue'
import Pagination from '@/components/common/Pagination.vue'
import ResetPasswordDialog from '@/components/business/ResetPasswordDialog.vue'

// 搜索表单
const searchForm = reactive({
  username: '',
  collegeId: undefined as string | undefined
})

// 分页列表
const { loading, tableData, pagination, loadData, handleCurrentChange, handleSizeChange, search } =
  usePagination<UserPageItem>((params) => userApi.pageQuery(params))

// 分页变化处理
function handlePageChange({ current, pageSize }: { current: number; pageSize: number }) {
  handleCurrentChange(current)
  if (pageSize !== pagination.size) {
    handleSizeChange(pageSize)
  }
}

// 对话框
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const resetPasswordVisible = ref(false)
const selectedUserId = ref('')
const selectedUsername = ref('')

const form = reactive({
  username: '',
  password: '',
  collegeId: '',
  dictId: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  dictId: [{ required: true, message: '请选择用户身份', trigger: 'change' }]
}

// 搜索
function handleSearch() {
  search({
    username: searchForm.username,
    collegeId: searchForm.collegeId
  })
}

function handleReset() {
  searchForm.username = ''
  searchForm.collegeId = undefined
  search()
}

// 新增
function handleAdd() {
  form.username = ''
  form.password = ''
  form.collegeId = ''
  form.dictId = ''
  dialogVisible.value = true
}

// 切换用户状态
async function handleToggleStatus(row: UserPageItem) {
  const action = row.delFlag === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', { type: 'warning' })
  
  if (row.delFlag === 1) {
    await userApi.enable(row.id)
    ElMessage.success('启用成功')
  } else {
    await userApi.disable(row.id)
    ElMessage.success('禁用成功')
  }
  
  loadData()
}

// 提交
async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    await userApi.registry({
      username: form.username,
      password: form.password,
      collegeId: form.collegeId,
      dictId: form.dictId
    })
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

function handleResetPassword(row: UserPageItem) {
  selectedUserId.value = row.id
  selectedUsername.value = row.username
  resetPasswordVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.user-page {
  height: 100%;
  
  .el-card {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.table-flex {
  flex: 1;
  min-height: 0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}
</style>
