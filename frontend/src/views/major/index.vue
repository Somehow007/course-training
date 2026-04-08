<template>
  <div class="major-page">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="专业名称">
          <el-input v-model="searchForm.majorName" placeholder="请输入专业名称" clearable />
        </el-form-item>
        <el-form-item label="所属学院">
          <CollegeSelect v-model="searchForm.collegeId" placeholder="请选择学院" clearable />
        </el-form-item>
        <el-form-item label="专业分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option label="工学" :value="0" />
            <el-option label="理学" :value="1" />
            <el-option label="文科" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button v-permission="'100'" type="primary" @click="handleAdd">新增专业</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe class="table-flex">
        <el-table-column prop="majorCode" label="专业代码" width="100" />
        <el-table-column prop="majorName" label="专业名称" />
        <el-table-column prop="collegeName" label="所属学院" />
        <el-table-column prop="courseNum" label="课程数量" width="100" />
        <el-table-column prop="category" label="专业分类" width="80" />
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑专业' : '新增专业'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="专业代码" prop="majorCode">
          <el-input v-model="form.majorCode" placeholder="请输入专业代码" />
        </el-form-item>
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="form.majorName" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeId">
          <CollegeSelect v-model="form.collegeId" placeholder="请选择学院" />
        </el-form-item>
        <el-form-item label="专业分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="工学" :value="0" />
            <el-option label="理学" :value="1" />
            <el-option label="文科" :value="2" />
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
import { majorApi } from '@/api/major'
import { collegeApi } from '@/api/college'
import type { MajorPageItem, CollegePageItem } from '@/types/api'
import { usePagination } from '@/composables/usePagination'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import Pagination from '@/components/common/Pagination.vue'

// 搜索表单
const searchForm = reactive({
  majorName: '',
  collegeId: undefined as string | undefined,
  categoryId: undefined as number | undefined
})

// 学院列表（用于ID转名称）
const collegeList = ref<CollegePageItem[]>([])

// 分页列表
const { loading, tableData, pagination, loadData, handleCurrentChange, handleSizeChange, search } =
  usePagination<MajorPageItem>((params) => majorApi.page(params))

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
  majorId: '',
  majorCode: '',
  majorName: '',
  collegeId: '',
  category: 0
})

const rules: FormRules = {
  majorName: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }],
  category: [{ required: true, message: '请选择专业分类', trigger: 'change' }]
}

// 搜索
function handleSearch() {
  // 根据学院ID查找学院名称
  let collegeNames: string[] | undefined
  if (searchForm.collegeId) {
    const college = collegeList.value.find(c => c.id === searchForm.collegeId)
    if (college) {
      collegeNames = [college.collegeName]
    }
  }
  
  search({
    majorNames: searchForm.majorName ? [searchForm.majorName] : undefined,
    collegeNames: collegeNames,
    categoryId: searchForm.categoryId
  })
}

function handleReset() {
  searchForm.majorName = ''
  searchForm.collegeId = undefined
  searchForm.categoryId = undefined
  search()
}

// 新增
function handleAdd() {
  isEdit.value = false
  form.majorId = ''
  form.majorCode = ''
  form.majorName = ''
  form.collegeId = ''
  form.category = 0
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: MajorPageItem) {
  isEdit.value = true
  form.majorId = row.id
  form.majorName = row.majorName
  dialogVisible.value = true
}

// 切换专业状态
async function handleToggleStatus(row: MajorPageItem) {
  const action = row.delFlag === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(`确定要${action}该专业吗？`, '提示', { type: 'warning' })
  
  if (row.delFlag === 1) {
    await majorApi.enable(row.id)
    ElMessage.success('启用成功')
  } else {
    await majorApi.delete(row.id)
    ElMessage.success('禁用成功')
  }
  
  loadData()
}

// 提交
async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await majorApi.update({
        majorId: form.majorId,
        majorName: form.majorName,
        collegeId: form.collegeId,
        category: form.category
      })
      ElMessage.success('修改成功')
    } else {
      await majorApi.create({
        majorCode: form.majorCode,
        majorName: form.majorName,
        collegeId: form.collegeId,
        category: form.category
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  // 加载学院列表
  try {
    const res = await collegeApi.page({ size: 100 })
    collegeList.value = res.records
  } catch (error) {
    console.error('加载学院列表失败:', error)
  }
  
  // 加载专业列表
  loadData()
})
</script>

<style scoped lang="scss">
.major-page {
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
