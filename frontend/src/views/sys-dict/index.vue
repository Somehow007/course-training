<template>
  <div class="sys-dict-page">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="字典类型">
          <el-select
            v-model="searchForm.dictType"
            placeholder="请选择字典类型"
            clearable
            filterable
            allow-create
            default-first-option
          >
            <el-option
              v-for="item in dictTypes"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="字典名称">
          <el-input v-model="searchForm.dictName" placeholder="请输入字典名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button v-permission="'100'" type="primary" @click="handleAdd">新增字典</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe class="table-flex">
        <el-table-column prop="dictType" label="字典类型" />
        <el-table-column prop="dictCode" label="字典编码" />
        <el-table-column prop="dictName" label="字典名称" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="150">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字典' : '新增字典'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典类型" prop="dictType">
          <el-select
            v-model="form.dictType"
            placeholder="请选择或输入字典类型"
            filterable
            allow-create
            default-first-option
          >
            <el-option
              v-for="item in dictTypes"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="form.dictCode" placeholder="请输入字典编码" />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { sysDictApi } from '@/api/sys-dict'
import type { SysDictPageItem } from '@/types/api'
import { usePagination } from '@/composables/usePagination'
import { useDictStore } from '@/stores/dict'
import Pagination from '@/components/common/Pagination.vue'

const dictStore = useDictStore()

const dictTypes = ref<string[]>([])

const searchForm = reactive({
  dictType: '',
  dictName: ''
})

const { loading, tableData, pagination, loadData, handleCurrentChange, handleSizeChange, search } =
  usePagination<SysDictPageItem>((params) => sysDictApi.page(params))

// 分页变化处理
function handlePageChange({ current, pageSize }: { current: number; pageSize: number }) {
  handleCurrentChange(current)
  if (pageSize !== pagination.size) {
    handleSizeChange(pageSize)
  }
}

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  id: '',
  dictType: '',
  dictCode: '',
  dictName: '',
  sortOrder: 0,
  remark: ''
})

const rules: FormRules = {
  dictType: [{ required: true, message: '请选择或输入字典类型', trigger: 'change' }],
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }]
}

async function loadDictTypes() {
  try {
    dictTypes.value = await sysDictApi.getDictTypes()
  } catch (error) {
    console.error('加载字典类型失败', error)
  }
}

function handleSearch() {
  search({
    dictType: searchForm.dictType,
    dictName: searchForm.dictName
  })
}

function handleReset() {
  searchForm.dictType = ''
  searchForm.dictName = ''
  search()
}

function handleAdd() {
  isEdit.value = false
  form.id = ''
  form.dictType = ''
  form.dictCode = ''
  form.dictName = ''
  form.sortOrder = 0
  form.remark = ''
  dialogVisible.value = true
}

function handleEdit(row: SysDictPageItem) {
  isEdit.value = true
  form.id = String(row.id)
  form.dictType = row.dictType
  form.dictCode = row.dictCode
  form.dictName = row.dictName
  form.sortOrder = row.sortOrder
  form.remark = row.remark
  dialogVisible.value = true
}

async function handleDelete(row: SysDictPageItem) {
  await ElMessageBox.confirm('确定要删除该字典项吗？', '提示', { type: 'warning' })
  await sysDictApi.delete(String(row.id))
  ElMessage.success('删除成功')
  dictStore.clearCache()
  loadData()
  loadDictTypes()
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await sysDictApi.update({
        id: form.id,
        dictType: form.dictType,
        dictCode: form.dictCode,
        dictName: form.dictName,
        sortOrder: form.sortOrder,
        remark: form.remark
      })
      ElMessage.success('修改成功')
    } else {
      await sysDictApi.create({
        dictType: form.dictType,
        dictCode: form.dictCode,
        dictName: form.dictName,
        sortOrder: form.sortOrder,
        remark: form.remark
      })
      ElMessage.success('新增成功')
    }
    dictStore.clearCache()
    dialogVisible.value = false
    loadData()
    loadDictTypes()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadDictTypes()
  loadData()
})
</script>

<style scoped lang="scss">
.sys-dict-page {
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
