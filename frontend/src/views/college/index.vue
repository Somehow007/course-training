<template>
  <div class="college-page">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学院名称">
          <el-input v-model="searchForm.collegeName" placeholder="请输入学院名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button v-permission="'100'" type="primary" @click="handleAdd">新增学院</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe class="table-flex">
        <el-table-column prop="collegeName" label="学院名称" />
        <el-table-column prop="majorNum" label="专业数量" width="100" />
        <el-table-column prop="courseNum" label="课程数量" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-permission="'100'" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'100'" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学院' : '新增学院'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学院编号" prop="collegeCode">
          <el-input v-model="form.collegeCode" placeholder="请输入学院编号" />
        </el-form-item>
        <el-form-item label="学院名称" prop="collegeName">
          <el-input v-model="form.collegeName" placeholder="请输入学院名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { collegeApi } from '@/api/college'
import type { CollegePageItem } from '@/types/api'
import { usePagination } from '@/composables/usePagination'
import Pagination from '@/components/common/Pagination.vue'

// 搜索表单
const searchForm = reactive({
  collegeName: ''
})

// 分页列表
const { loading, tableData, pagination, loadData, handleCurrentChange, handleSizeChange, search } =
  usePagination<CollegePageItem>((params) => collegeApi.page(params))

// 分页变化处理
function handlePageChange({ current, pageSize }: { current: number; pageSize: number }) {
  handleCurrentChange(current)
  if (pageSize !== pagination.size) {
    handleSizeChange(pageSize)
  }
}

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  collegeId: '',
  collegeCode: '',
  collegeName: ''
})

const rules: FormRules = {
  collegeCode: [{ required: true, message: '请输入学院编号', trigger: 'blur' }],
  collegeName: [{ required: true, message: '请输入学院名称', trigger: 'blur' }]
}

// 搜索
function handleSearch() {
  search({ collegeNames: searchForm.collegeName ? [searchForm.collegeName] : undefined })
}

function handleReset() {
  searchForm.collegeName = ''
  search()
}

// 新增
function handleAdd() {
  isEdit.value = false
  form.collegeId = ''
  form.collegeCode = ''
  form.collegeName = ''
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: CollegePageItem) {
  isEdit.value = true
  form.collegeId = row.id
  form.collegeName = row.collegeName
  dialogVisible.value = true
}

// 删除
async function handleDelete(row: CollegePageItem) {
  await ElMessageBox.confirm('确定要删除该学院吗？', '提示', { type: 'warning' })
  await collegeApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

// 提交
async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await collegeApi.update({ collegeId: form.collegeId, collegeName: form.collegeName })
      ElMessage.success('修改成功')
    } else {
      await collegeApi.create({ collegeCode: form.collegeCode, collegeName: form.collegeName })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.college-page {
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
