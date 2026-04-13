<template>
  <div class="version-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学院">
          <CollegeSelect
            v-model="searchForm.collegeId"
            placeholder="请选择学院"
            clearable
            style="width: 180px"
            @change="handleCollegeChange"
          />
        </el-form-item>
        <el-form-item label="专业">
          <MajorSelect
            v-model="searchForm.majorId"
            :college-id="searchForm.collegeId"
            placeholder="请选择专业"
            clearable
            style="width: 180px"
            @change="handleMajorChange"
          />
        </el-form-item>
        <el-form-item label="版本状态">
          <el-select
            v-model="searchForm.versionStatus"
            placeholder="全部状态"
            clearable
            style="width: 120px"
          >
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已归档" :value="2" />
            <el-option label="已回滚" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>版本列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleCreate" v-permission="'100'">
              <el-icon><Plus /></el-icon>
              创建版本
            </el-button>
            <el-button
              @click="handleCompare"
              :disabled="selectedVersions.length !== 2"
              type="warning"
              plain
            >
              <el-icon><Sort /></el-icon>
              版本对比
              <span v-if="selectedVersions.length > 0" class="compare-hint">
                (已选{{ selectedVersions.length }}/2)
              </span>
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="versionList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="selection" width="55" :selectable="() => true" />
        <el-table-column prop="versionNumber" label="版本号" width="90" sortable>
          <template #default="{ row }">
            <span class="version-number-cell">V{{ row.versionNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="versionName" label="版本名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="trainingProgramName" label="培养方案" min-width="200" show-overflow-tooltip />
        <el-table-column prop="versionStatusDesc" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.versionStatus)" effect="dark" size="small">
              {{ row.versionStatusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="changeDescription" label="变更说明" min-width="180" show-overflow-tooltip />
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" sortable>
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
            <el-button
              link type="success" size="small"
              @click="handlePublish(row)"
              v-if="row.versionStatus === 0"
              v-permission="'100'"
            >
              发布
            </el-button>
            <el-button
              link type="warning" size="small"
              @click="handleRollback(row)"
              v-if="row.versionStatus === 1"
              v-permission="'100'"
            >
              回滚
            </el-button>
            <el-button
              link type="info" size="small"
              @click="handleArchive(row)"
              v-if="row.versionStatus === 1"
              v-permission="'100'"
            >
              归档
            </el-button>
            <el-button
              link type="danger" size="small"
              @click="handleDelete(row)"
              v-if="row.versionStatus === 0"
              v-permission="'100'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination
        :total="pagination.total"
        :current="pagination.current"
        :page-size="pagination.size"
        @change="handlePageChange"
      />
    </el-card>

    <el-dialog
      v-model="createDialogVisible"
      title="创建新版本"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <el-form-item label="学院" prop="collegeId">
          <CollegeSelect
            v-model="createForm.collegeId"
            placeholder="请选择学院"
            style="width: 100%"
            @change="handleCreateCollegeChange"
          />
        </el-form-item>
        <el-form-item label="专业" prop="majorId">
          <MajorSelect
            v-model="createForm.majorId"
            :college-id="createForm.collegeId"
            placeholder="请先选择学院"
            style="width: 100%"
            @change="handleCreateMajorChange"
          />
        </el-form-item>
        <el-form-item label="培养方案" prop="trainingProgramId">
          <el-select
            v-model="createForm.trainingProgramId"
            placeholder="请先选择专业"
            filterable
            style="width: 100%"
            :disabled="!createForm.majorId"
          >
            <el-option
              v-for="item in filteredTrainingProgramList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="版本名称" prop="versionName">
          <el-input v-model="createForm.versionName" placeholder="请输入版本名称" />
        </el-form-item>
        <el-form-item label="变更说明">
          <el-input
            v-model="createForm.changeDescription"
            type="textarea"
            :rows="4"
            placeholder="请输入变更说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Sort } from '@element-plus/icons-vue'
import { versionApi, trainingProgramApi, type VersionListResponse } from '@/api'
import Pagination from '@/components/common/Pagination.vue'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import MajorSelect from '@/components/business/MajorSelect.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const versionList = ref<VersionListResponse[]>([])
const trainingProgramList = ref<any[]>([])
const selectedVersions = ref<any[]>([])
const createDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()

const searchForm = reactive({
  collegeId: '' as string | number | undefined,
  majorId: '' as string | number | undefined,
  trainingProgramId: '',
  versionStatus: undefined as number | undefined,
  versionName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const createForm = reactive({
  collegeId: '' as string | number | undefined,
  majorId: '' as string | number | undefined,
  trainingProgramId: '',
  versionName: '',
  changeDescription: ''
})

const createRules: FormRules = {
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }],
  trainingProgramId: [{ required: true, message: '请选择培养方案', trigger: 'change' }],
  versionName: [{ required: true, message: '请输入版本名称', trigger: 'blur' }]
}

const filteredTrainingProgramList = computed(() => {
  if (!createForm.majorId) return []
  return trainingProgramList.value.filter(tp => String(tp.majorId) === String(createForm.majorId))
})

function getStatusTagType(status: number): string {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return types[status] || 'info'
}

function formatTime(time: string): string {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

async function fetchVersionList() {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      versionStatus: searchForm.versionStatus,
      versionName: searchForm.versionName
    }
    if (searchForm.trainingProgramId) {
      params.trainingProgramId = searchForm.trainingProgramId
    }
    const { records, total } = await versionApi.pageVersionHistory(params)
    versionList.value = records
    pagination.total = total
  } catch (error) {
    ElMessage.error('获取版本列表失败')
  } finally {
    loading.value = false
  }
}

async function fetchTrainingProgramList() {
  try {
    const { records } = await trainingProgramApi.page({
      current: 1,
      size: 500
    })
    trainingProgramList.value = records
  } catch (error) {
    ElMessage.error('获取培养方案列表失败')
  }
}

function handleCollegeChange() {
  searchForm.majorId = ''
  searchForm.trainingProgramId = ''
}

function handleMajorChange(val: string | number | undefined) {
  if (val) {
    const tp = trainingProgramList.value.find(tp => String(tp.majorId) === String(val))
    searchForm.trainingProgramId = tp ? tp.id : ''
  } else {
    searchForm.trainingProgramId = ''
  }
}

function handleCreateCollegeChange() {
  createForm.majorId = ''
  createForm.trainingProgramId = ''
}

function handleCreateMajorChange(val: string | number | undefined) {
  if (val) {
    const tp = trainingProgramList.value.find(tp => String(tp.majorId) === String(val))
    createForm.trainingProgramId = tp ? tp.id : ''
  } else {
    createForm.trainingProgramId = ''
  }
}

function handleSearch() {
  pagination.current = 1
  fetchVersionList()
}

function handleReset() {
  Object.assign(searchForm, {
    collegeId: '',
    majorId: '',
    trainingProgramId: '',
    versionStatus: undefined,
    versionName: ''
  })
  handleSearch()
}

function handlePageChange(value: { current: number; pageSize: number }) {
  pagination.current = value.current
  pagination.size = value.pageSize
  fetchVersionList()
}

function handleSelectionChange(selection: any[]) {
  selectedVersions.value = selection
}

function handleCreate() {
  Object.assign(createForm, {
    collegeId: '',
    majorId: '',
    trainingProgramId: '',
    versionName: '',
    changeDescription: ''
  })
  createDialogVisible.value = true
}

async function handleCreateSubmit() {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await versionApi.createVersion({
          trainingProgramId: createForm.trainingProgramId,
          versionName: createForm.versionName,
          changeDescription: createForm.changeDescription
        })
        ElMessage.success('创建版本成功')
        createDialogVisible.value = false
        createFormRef.value?.resetFields()
        fetchVersionList()
      } catch (error) {
        ElMessage.error('创建版本失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

function handleViewDetail(row: any) {
  router.push({
    path: '/training-program/' + row.trainingProgramId,
    query: { versionId: row.id }
  })
}

async function handlePublish(row: any) {
  try {
    await ElMessageBox.confirm('确定要发布该版本吗？发布后将生效。', '发布确认', {
      type: 'warning',
      confirmButtonText: '确定发布',
      cancelButtonText: '取消'
    })
    await versionApi.publishVersion(row.id)
    ElMessage.success('发布成功')
    fetchVersionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

async function handleRollback(row: any) {
  try {
    await ElMessageBox.confirm(
      '确定要回滚到该版本吗？当前版本数据将被保存为新版本。',
      '回滚确认',
      {
        type: 'warning',
        confirmButtonText: '确定回滚',
        cancelButtonText: '取消'
      }
    )
    await versionApi.rollbackVersion(row.id)
    ElMessage.success('回滚成功')
    fetchVersionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('回滚失败')
    }
  }
}

async function handleArchive(row: any) {
  try {
    await ElMessageBox.confirm('确定要归档该版本吗？', '归档确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await versionApi.archiveVersion(row.id)
    ElMessage.success('归档成功')
    fetchVersionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('归档失败')
    }
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该版本吗？此操作不可恢复。', '删除确认', {
      type: 'error',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await versionApi.deleteVersion(row.id)
    ElMessage.success('删除成功')
    fetchVersionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

function handleCompare() {
  if (selectedVersions.value.length !== 2) {
    ElMessage.warning('请选择两个版本进行对比')
    return
  }

  router.push({
    path: '/version/compare',
    query: {
      sourceId: selectedVersions.value[0].id,
      targetId: selectedVersions.value[1].id
    }
  })
}

onMounted(() => {
  fetchVersionList()
  fetchTrainingProgramList()
})
</script>

<style scoped lang="scss">
.version-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;

    .search-form {
      display: flex;
      flex-wrap: wrap;
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 10px;

        .compare-hint {
          font-size: 12px;
          color: #909399;
          margin-left: 4px;
        }
      }
    }

    .version-number-cell {
      font-weight: 600;
      color: #409eff;
    }
  }
}
</style>
