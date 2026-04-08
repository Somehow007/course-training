<template>
  <div class="course-page">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="开课学院">
          <CollegeSelect v-model="searchForm.collegeId" placeholder="请选择学院" clearable />
        </el-form-item>
        <el-form-item label="课程性质">
          <el-select v-model="searchForm.courseNature" placeholder="请选择" clearable>
            <el-option label="必修" :value="1" />
            <el-option label="选修" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button v-permission="'100'" type="primary" @click="handleAdd">新增课程</el-button>
        <el-button v-permission="'100'" type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
          批量禁用
        </el-button>
        <el-button v-permission="'100'" type="success" :disabled="!selectedIds.length" @click="handleBatchEnable">
          批量启用
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        class="table-flex"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="collegeName" label="开课学院" />
        <el-table-column prop="courseType" label="课程类别" width="120" />
        <el-table-column label="课程性质" width="80">
          <template #default="{ row }">
            <CourseNatureTag :nature="row.courseNature" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.delFlag === 1 ? 'danger' : 'success'">
              {{ row.delFlag === 1 ? '禁用' : '启用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-permission="'100'" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-permission="'100'" 
              :type="row.delFlag === 1 ? 'success' : 'danger'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.delFlag === 1 ? '启用' : '禁用' }}
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="开课学院" prop="collegeId">
          <CollegeSelect v-model="form.collegeId" placeholder="请选择学院" />
        </el-form-item>
        <el-form-item label="课程类别" prop="dictId">
          <DictSelect v-model="form.dictId" dict-type="course_type" placeholder="请选择课程类别" />
        </el-form-item>
        <el-form-item label="课程性质" prop="courseNature">
          <el-select v-model="form.courseNature" placeholder="请选择">
            <el-option label="必修" :value="1" />
            <el-option label="选修" :value="0" />
          </el-select>
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
import { courseApi } from '@/api/course'
import type { CoursePageItem } from '@/types/api'
import { usePagination } from '@/composables/usePagination'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import DictSelect from '@/components/business/DictSelect.vue'
import CourseNatureTag from '@/components/business/CourseNatureTag.vue'
import Pagination from '@/components/common/Pagination.vue'

// 搜索表单
const searchForm = reactive({
  courseName: '',
  collegeId: undefined as string | undefined,
  courseNature: undefined as number | undefined
})

// 分页列表
const { loading, tableData, pagination, loadData, handleCurrentChange, handleSizeChange, search } =
  usePagination<CoursePageItem>((params) => courseApi.page(params))

// 分页变化处理
function handlePageChange({ current, pageSize }: { current: number; pageSize: number }) {
  handleCurrentChange(current)
  if (pageSize !== pagination.size) {
    handleSizeChange(pageSize)
  }
}

// 多选
const selectedIds = ref<string[]>([])

function handleSelectionChange(selection: CoursePageItem[]) {
  selectedIds.value = selection.map((item) => item.courseId)
}

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  id: '',
  courseName: '',
  collegeId: '',
  dictId: '',
  courseNature: 0
})

const rules: FormRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择开课学院', trigger: 'change' }],
  dictId: [{ required: true, message: '请选择课程类别', trigger: 'change' }],
  courseNature: [{ required: true, message: '请选择课程性质', trigger: 'change' }]
}

// 搜索
function handleSearch() {
  search({
    courseNames: searchForm.courseName ? [searchForm.courseName] : undefined,
    collegeIds: searchForm.collegeId ? [searchForm.collegeId] : undefined,
    courseNature: searchForm.courseNature
  })
}

function handleReset() {
  searchForm.courseName = ''
  searchForm.collegeId = undefined
  searchForm.courseNature = undefined
  search()
}

// 新增
function handleAdd() {
  isEdit.value = false
  form.id = ''
  form.courseName = ''
  form.collegeId = ''
  form.dictId = ''
  form.courseNature = 0
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: CoursePageItem) {
  isEdit.value = true
  form.id = row.courseId
  form.courseName = row.courseName
  dialogVisible.value = true
}

// 切换课程状态
async function handleToggleStatus(row: CoursePageItem) {
  const action = row.delFlag === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(`确定要${action}该课程吗？`, '提示', { type: 'warning' })
  
  if (row.delFlag === 1) {
    await courseApi.enable([row.courseId])
    ElMessage.success('启用成功')
  } else {
    await courseApi.delete([row.courseId])
    ElMessage.success('禁用成功')
  }
  
  loadData()
}

// 批量禁用
async function handleBatchDelete() {
  await ElMessageBox.confirm(`确定要禁用选中的 ${selectedIds.value.length} 门课程吗？`, '提示', {
    type: 'warning'
  })
  await courseApi.delete(selectedIds.value)
  ElMessage.success('禁用成功')
  loadData()
}

// 批量启用
async function handleBatchEnable() {
  await ElMessageBox.confirm(`确定要启用选中的 ${selectedIds.value.length} 门课程吗？`, '提示', {
    type: 'warning'
  })
  await courseApi.enable(selectedIds.value)
  ElMessage.success('启用成功')
  loadData()
}

// 提交
async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await courseApi.update({
        id: form.id,
        courseName: form.courseName,
        collegeId: form.collegeId,
        dictId: form.dictId,
        courseNature: form.courseNature
      })
      ElMessage.success('修改成功')
    } else {
      await courseApi.create({
        courseName: form.courseName,
        collegeId: form.collegeId,
        dictId: form.dictId,
        courseNature: form.courseNature
      })
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
.course-page {
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
