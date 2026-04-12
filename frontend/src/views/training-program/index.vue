<template>
  <div class="training-program-page">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="专业">
          <MajorSelect v-model="searchForm.majorId" placeholder="请选择专业" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button v-permission="'100'" type="primary" @click="handleAdd">新增培养计划</el-button>
        <el-button type="success" @click="handleImport">导入Excel</el-button>
        <el-button type="info" @click="handleDownloadTemplate">下载模板</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe class="table-flex">
        <el-table-column prop="name" label="培养计划名称" />
        <el-table-column prop="majorName" label="所属专业" />
        <el-table-column prop="year" label="年份" width="100" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">查看详情</el-button>
            <el-button type="success" link @click="handleExport(row)">导出</el-button>
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

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="新增培养计划" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属学院" prop="collegeId">
          <CollegeSelect v-model="form.collegeId" placeholder="请选择学院" @change="handleFormCollegeChange" />
        </el-form-item>
        <el-form-item label="所属专业" prop="majorId">
          <MajorSelect v-model="form.majorId" :college-id="form.collegeId" placeholder="请选择专业" />
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-date-picker v-model="form.year" type="year" placeholder="请选择年份" value-format="YYYY" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入培养计划" width="500px">
      <el-form ref="importFormRef" :model="importForm" :rules="importRules" label-width="80px">
        <el-form-item label="所属学院" prop="collegeId">
          <CollegeSelect v-model="importForm.collegeId" placeholder="请选择学院" @change="handleCollegeChange" />
        </el-form-item>
        <el-form-item label="所属专业" prop="majorId">
          <MajorSelect v-model="importForm.majorId" :college-id="importForm.collegeId" placeholder="请选择专业" />
        </el-form-item>
        <el-form-item label="Excel文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".xlsx,.xls"
            :on-change="handleFileChange"
          >
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" @click="handleImportSubmit">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { trainingProgramApi } from '@/api/training-program'
import { exportExcel, downloadTemplate } from '@/utils/download'
import { usePagination } from '@/composables/usePagination'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import MajorSelect from '@/components/business/MajorSelect.vue'
import type { TrainingProgramPageItem } from '@/types/api'
import Pagination from '@/components/common/Pagination.vue'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  majorId: undefined as string | undefined
})

// 分页列表
const { loading, tableData, pagination, loadData, handleCurrentChange, handleSizeChange, search } =
  usePagination<TrainingProgramPageItem>((params) => trainingProgramApi.page(params))

// 分页变化处理
function handlePageChange({ current, pageSize }: { current: number; pageSize: number }) {
  handleCurrentChange(current)
  if (pageSize !== pagination.size) {
    handleSizeChange(pageSize)
  }
}

function handleSearch() {
  search({
    majorId: searchForm.majorId
  })
}

function handleReset() {
  searchForm.majorId = undefined
  search()
}

// 新增对话框
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  majorId: '',
  collegeId: '',
  year: '',
  description: ''
})

const rules: FormRules = {
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  year: [{ required: true, message: '请选择年份', trigger: 'change' }]
}

function handleAdd() {
  form.majorId = ''
  form.collegeId = ''
  form.year = ''
  form.description = ''
  dialogVisible.value = true
}

function handleFormCollegeChange() {
  form.majorId = ''
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    await trainingProgramApi.create({
      majorId: form.majorId,
      collegeId: form.collegeId,
      year: parseInt(form.year),
      description: form.description
    })
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

// 查看详情
function handleViewDetail(row: TrainingProgramPageItem) {
  router.push(`/training-program/${row.id}`)
}

// 导出
function handleExport(row: TrainingProgramPageItem) {
  exportExcel(row.id, `${row.name}.xlsx`)
}

// 删除
async function handleDelete(row: TrainingProgramPageItem) {
  await ElMessageBox.confirm('确定要删除该培养计划吗？', '提示', { type: 'warning' })
  await trainingProgramApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

// 导入
const importDialogVisible = ref(false)
const importFormRef = ref<FormInstance>()
const importLoading = ref(false)
const importForm = reactive({
  collegeId: '',
  majorId: '',
  file: null as File | null
})

const importRules: FormRules = {
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }]
}

function handleImport() {
  importForm.collegeId = ''
  importForm.majorId = ''
  importForm.file = null
  importDialogVisible.value = true
}

function handleCollegeChange() {
  importForm.majorId = ''
}

function handleFileChange(uploadFile: UploadFile) {
  importForm.file = uploadFile.raw || null
}

async function handleImportSubmit() {
  await importFormRef.value?.validate()
  if (!importForm.file) {
    ElMessage.warning('请选择文件')
    return
  }
  importLoading.value = true
  try {
    await trainingProgramApi.importExcel(importForm.collegeId, importForm.majorId, importForm.file)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
  } finally {
    importLoading.value = false
  }
}

// 下载模板
function handleDownloadTemplate() {
  downloadTemplate()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.training-program-page {
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
