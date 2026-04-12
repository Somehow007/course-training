<template>
  <div class="training-program-detail">
    <el-page-header @back="goBack" title="返回列表">
      <template #content>
        <div class="page-header-content">
          <span class="page-title">培养计划详情</span>
          <el-tag v-if="currentVersion" type="success" size="large" class="version-tag">
            V{{ currentVersion.versionNumber }} · {{ currentVersion.versionStatusDesc }}
          </el-tag>
        </div>
      </template>
      <template #extra>
        <div class="header-actions">
          <el-button type="primary" @click="handleAddCourse" v-permission="'100'">
            <el-icon><Plus /></el-icon>
            添加课程
          </el-button>
          <el-button type="success" @click="handleImport" v-permission="'100'">
            <el-icon><Upload /></el-icon>
            导入更新
          </el-button>
          <el-button @click="handleExport">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </div>
      </template>
    </el-page-header>

    <div class="detail-layout">
      <div class="main-content">
        <el-card>
          <el-table :data="detailData" v-loading="loading" stripe border empty-text="暂无课程数据" max-height="calc(100vh - 260px)">
            <el-table-column prop="courseName" label="课程名称" width="150" fixed />
            <el-table-column prop="courseType" label="课程类别" width="120" />
            <el-table-column label="课程性质" width="80">
              <template #default="{ row }">
                <CourseNatureTag :nature="row.courseNature" />
              </template>
            </el-table-column>
            <el-table-column prop="collegeName" label="开课学院" width="120" />
            <el-table-column prop="majorName" label="修读专业" width="150" />
            <el-table-column prop="totalCredits" label="总学分" width="80" />
            <el-table-column prop="totalHours" label="总学时" width="80" />
            <el-table-column prop="hourTeach" label="授课" width="60" />
            <el-table-column prop="hourPractice" label="实验" width="60" />
            <el-table-column prop="hourOperation" label="上机" width="60" />
            <el-table-column prop="hourOutside" label="实践" width="60" />
            <el-table-column prop="term" label="学期" width="60" />
            <el-table-column prop="remark" label="备注" min-width="100" />
            <el-table-column label="操作" width="120" fixed="right" v-if="userStore.isAdmin">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleEditCourse(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteCourse(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <div class="version-sidebar">
        <el-card>
          <template #header>
            <div class="sidebar-header">
              <span>版本历史</span>
              <el-button type="primary" link size="small" @click="goToVersionManage">管理</el-button>
            </div>
          </template>
          <div v-loading="versionLoading" class="version-timeline">
            <el-timeline v-if="versionList.length > 0">
              <el-timeline-item
                v-for="ver in versionList"
                :key="ver.id"
                :type="getVersionTimelineType(ver.versionStatus)"
                :hollow="!isCurrentVersion(ver)"
                size="large"
              >
                <div class="version-item" :class="{ active: isCurrentVersion(ver) }" @click="handleViewVersion(ver)">
                  <div class="version-item-header">
                    <span class="version-number">V{{ ver.versionNumber }}</span>
                    <el-tag :type="getStatusTagType(ver.versionStatus)" size="small">
                      {{ ver.versionStatusDesc }}
                    </el-tag>
                  </div>
                  <div class="version-item-info">
                    <span class="version-name">{{ ver.versionName }}</span>
                    <span class="version-creator">{{ ver.creatorName }}</span>
                    <span class="version-time">{{ formatTime(ver.createTime) }}</span>
                  </div>
                  <div v-if="ver.changeDescription" class="version-desc">
                    {{ ver.changeDescription }}
                  </div>
                  <div v-if="isCurrentVersion(ver)" class="version-current-badge">当前版本</div>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无版本记录" :image-size="60" />
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="addDialogVisible" title="添加课程" width="600px">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <CourseSelect v-model="addForm.courseId" placeholder="请选择课程" />
        </el-form-item>
        <el-form-item label="开课学院" prop="collegeId">
          <CollegeSelect v-model="addForm.collegeId" placeholder="请选择学院" />
        </el-form-item>
        <el-form-item label="修读专业" prop="majorId">
          <MajorSelect v-model="addForm.majorId" placeholder="请选择专业" />
        </el-form-item>
        <el-form-item label="总学分" prop="totalCredits">
          <el-input-number v-model="addForm.totalCredits" :min="0" :precision="1" />
        </el-form-item>
        <el-form-item label="总学时(小时)">
          <el-input-number v-model="addForm.totalHours" :min="0" />
        </el-form-item>
        <el-form-item label="授课学时">
          <el-input-number v-model="addForm.hourTeach" :min="0" />
        </el-form-item>
        <el-form-item label="实验学时">
          <el-input-number v-model="addForm.hourPractice" :min="0" />
        </el-form-item>
        <el-form-item label="上机学时">
          <el-input-number v-model="addForm.hourOperation" :min="0" />
        </el-form-item>
        <el-form-item label="实践学时">
          <el-input-number v-model="addForm.hourOutside" :min="0" />
        </el-form-item>
        <el-form-item label="建议学期">
          <el-select v-model="addForm.term" placeholder="请选择">
            <el-option v-for="i in 8" :key="i" :label="`第${i}学期`" :value="i" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAddSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑课程" width="600px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="editForm.courseName" disabled />
        </el-form-item>
        <el-form-item label="修读专业">
          <MajorSelect v-model="editForm.majorId" placeholder="请选择专业" />
        </el-form-item>
        <el-form-item label="总学分" prop="totalCredits">
          <el-input-number v-model="editForm.totalCredits" :min="0" :precision="1" />
        </el-form-item>
        <el-form-item label="总学时(小时)">
          <el-input-number v-model="editForm.totalHours" :min="0" />
        </el-form-item>
        <el-form-item label="授课学时">
          <el-input-number v-model="editForm.hourTeach" :min="0" />
        </el-form-item>
        <el-form-item label="实验学时">
          <el-input-number v-model="editForm.hourPractice" :min="0" />
        </el-form-item>
        <el-form-item label="上机学时">
          <el-input-number v-model="editForm.hourOperation" :min="0" />
        </el-form-item>
        <el-form-item label="实践学时">
          <el-input-number v-model="editForm.hourOutside" :min="0" />
        </el-form-item>
        <el-form-item label="建议学期">
          <el-select v-model="editForm.term" placeholder="请选择">
            <el-option v-for="i in 8" :key="i" :label="`第${i}学期`" :value="i" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="导入更新培养计划" width="500px" :close-on-click-modal="false">
      <el-alert type="info" :closable="false" show-icon class="import-tip">
        <template #title>
          导入Excel将创建新版本，当前版本数据将被保留在历史版本中。
        </template>
      </el-alert>
      <el-form ref="importFormRef" :model="importForm" :rules="importRules" label-width="100px" class="import-form">
        <el-form-item label="版本说明" prop="changeDescription">
          <el-input
            v-model="importForm.changeDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入本次更新的变更说明（如：调整了3门课程的学分）"
          />
        </el-form-item>
        <el-form-item label="Excel文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".xlsx,.xls"
            :on-change="handleFileChange"
            drag
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">仅支持 .xlsx / .xls 格式文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" @click="handleImportSubmit">
          确认导入
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="versionDetailDialogVisible" :title="`版本 V${viewingVersion?.versionNumber} 详情`" width="700px">
      <div v-if="viewingVersion" class="version-detail-dialog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="版本号">V{{ viewingVersion.versionNumber }}</el-descriptions-item>
          <el-descriptions-item label="版本状态">
            <el-tag :type="getStatusTagType(viewingVersion.versionStatus)">{{ viewingVersion.versionStatusDesc }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="版本名称" :span="2">{{ viewingVersion.versionName }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ viewingVersion.creatorName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(viewingVersion.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="变更说明" :span="2">{{ viewingVersion.changeDescription || '无' }}</el-descriptions-item>
        </el-descriptions>
        <div class="version-detail-actions">
          <el-button
            type="warning"
            @click="handleRollbackFromDetail(viewingVersion)"
            v-if="viewingVersion.versionStatus === 1"
            v-permission="'100'"
          >
            <el-icon><RefreshLeft /></el-icon>
            回滚到此版本
          </el-button>
          <el-button
            type="primary"
            @click="handleCompareFromDetail(viewingVersion)"
            v-if="currentVersion && viewingVersion.id !== currentVersion.id"
          >
            <el-icon><Sort /></el-icon>
            与当前版本对比
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { Plus, Upload, Download, UploadFilled, RefreshLeft, Sort } from '@element-plus/icons-vue'
import { trainingProgramApi } from '@/api/training-program'
import { versionApi, type VersionListResponse } from '@/api/version'
import type { TrainingProgramDetailItem } from '@/types/api'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import MajorSelect from '@/components/business/MajorSelect.vue'
import CourseSelect from '@/components/business/CourseSelect.vue'
import CourseNatureTag from '@/components/business/CourseNatureTag.vue'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import type { SysDictPageItem } from '@/types/api'
import { exportExcel } from '@/utils/download'

const route = useRoute()
const router = useRouter()
const dictStore = useDictStore()
const userStore = useUserStore()
const programId = route.params.id as string

const loading = ref(false)
const detailData = ref<TrainingProgramDetailItem[]>([])
const courseTypeDict = ref<SysDictPageItem[]>([])

const versionLoading = ref(false)
const versionList = ref<VersionListResponse[]>([])
const currentVersion = computed(() => versionList.value.length > 0 ? versionList.value[0] : null)

const viewingVersion = ref<VersionListResponse | null>(null)
const versionDetailDialogVisible = ref(false)

async function loadDetail() {
  if (!programId) {
    ElMessage.error('缺少培养计划ID')
    return
  }
  loading.value = true
  try {
    const [data, dict] = await Promise.all([
      trainingProgramApi.getDetail(programId),
      dictStore.getDictByType('course_type')
    ])
    courseTypeDict.value = dict
    detailData.value = sortCourses(data)
  } catch (error) {
    ElMessage.error('加载详情失败')
  } finally {
    loading.value = false
  }
}

async function loadVersionList() {
  if (!programId) return
  versionLoading.value = true
  try {
    const data = await versionApi.listVersionsByProgramId(programId)
    versionList.value = data
  } catch (error) {
    console.error('加载版本列表失败:', error)
  } finally {
    versionLoading.value = false
  }
}

function sortCourses(courses: TrainingProgramDetailItem[]): TrainingProgramDetailItem[] {
  const sortOrderMap = new Map<string, number>()
  courseTypeDict.value.forEach(item => {
    sortOrderMap.set(item.dictName, item.sortOrder)
  })

  return [...courses].sort((a, b) => {
    const orderA = sortOrderMap.get(a.courseType) ?? 999
    const orderB = sortOrderMap.get(b.courseType) ?? 999
    if (orderA !== orderB) return orderA - orderB
    if (a.courseNature !== b.courseNature) return b.courseNature - a.courseNature
    return 0
  })
}

function isCurrentVersion(ver: VersionListResponse): boolean {
  if (!currentVersion.value) return false
  return ver.id === currentVersion.value.id
}

function getVersionTimelineType(status: number): string {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return types[status] || 'info'
}

function getStatusTagType(status: number): string {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return types[status] || 'info'
}

function formatTime(time: string): string {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

function goBack() {
  router.push('/training-program/list')
}

function goToVersionManage() {
  router.push('/version')
}

function handleViewVersion(ver: VersionListResponse) {
  viewingVersion.value = ver
  versionDetailDialogVisible.value = true
}

async function handleRollbackFromDetail(ver: VersionListResponse) {
  try {
    await ElMessageBox.confirm(
      `确定要回滚到版本 V${ver.versionNumber} 吗？当前版本数据将被保存为新版本。`,
      '版本回滚确认',
      { type: 'warning', confirmButtonText: '确定回滚', cancelButtonText: '取消' }
    )
    await versionApi.rollbackVersion(ver.id)
    ElMessage.success('回滚成功')
    versionDetailDialogVisible.value = false
    loadDetail()
    loadVersionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('回滚失败')
    }
  }
}

function handleCompareFromDetail(ver: VersionListResponse) {
  if (!currentVersion.value) return
  router.push({
    path: '/version/compare',
    query: {
      sourceId: currentVersion.value.id,
      targetId: ver.id
    }
  })
}

function handleExport() {
  exportExcel(programId)
}

const addDialogVisible = ref(false)
const addFormRef = ref<FormInstance>()
const submitLoading = ref(false)

const addForm = reactive({
  courseId: '',
  collegeId: '',
  majorId: '',
  totalCredits: 0,
  totalHours: 0,
  hourTeach: 0,
  hourPractice: 0,
  hourOperation: 0,
  hourOutside: 0,
  term: undefined as number | undefined,
  remark: ''
})

const addRules: FormRules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  totalCredits: [{ required: true, message: '请输入总学分', trigger: 'blur' }]
}

function handleAddCourse() {
  Object.assign(addForm, {
    courseId: '', collegeId: '', majorId: '', totalCredits: 0, totalHours: 0,
    hourTeach: 0, hourPractice: 0, hourOperation: 0, hourOutside: 0,
    term: undefined, remark: ''
  })
  addDialogVisible.value = true
}

async function handleAddSubmit() {
  await addFormRef.value?.validate()
  submitLoading.value = true
  try {
    await trainingProgramApi.addCourse({
      trainingProgramId: programId,
      courseId: addForm.courseId,
      collegeId: addForm.collegeId,
      majorId: addForm.majorId,
      totalCredits: addForm.totalCredits,
      totalHours: addForm.totalHours,
      hourTeach: addForm.hourTeach,
      hourPractice: addForm.hourPractice,
      hourOperation: addForm.hourOperation,
      hourOutside: addForm.hourOutside,
      term: addForm.term,
      remark: addForm.remark
    })
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadDetail()
  } catch (error) {
    console.error('添加失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()

const editForm = reactive({
  id: '',
  courseName: '',
  majorId: '',
  totalCredits: 0,
  totalHours: 0,
  hourTeach: 0,
  hourPractice: 0,
  hourOperation: 0,
  hourOutside: 0,
  term: undefined as number | undefined,
  remark: ''
})

const editRules: FormRules = {
  totalCredits: [{ required: true, message: '请输入总学分', trigger: 'blur' }]
}

function handleEditCourse(row: TrainingProgramDetailItem) {
  Object.assign(editForm, {
    id: String(row.id),
    courseName: row.courseName,
    majorId: String(row.majorId),
    totalCredits: row.totalCredits,
    totalHours: row.totalHours,
    hourTeach: row.hourTeach,
    hourPractice: row.hourPractice,
    hourOperation: row.hourOperation,
    hourOutside: row.hourOutside,
    term: row.term ?? undefined,
    remark: row.remark ?? ''
  })
  editDialogVisible.value = true
}

async function handleEditSubmit() {
  await editFormRef.value?.validate()
  submitLoading.value = true
  try {
    await trainingProgramApi.updateCourse({
      id: editForm.id,
      majorId: editForm.majorId,
      totalCredits: editForm.totalCredits,
      totalHours: editForm.totalHours,
      hourTeach: editForm.hourTeach,
      hourPractice: editForm.hourPractice,
      hourOperation: editForm.hourOperation,
      hourOutside: editForm.hourOutside,
      term: editForm.term,
      remark: editForm.remark
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    loadDetail()
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    submitLoading.value = false
  }
}

async function handleDeleteCourse(row: TrainingProgramDetailItem) {
  try {
    await ElMessageBox.confirm(`确定要删除课程"${row.courseName}"吗？`, '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await trainingProgramApi.deleteCourse(String(row.id))
    ElMessage.success('删除成功')
    loadDetail()
  } catch (error) {
    if (error !== 'cancel') console.error('删除失败:', error)
  }
}

const importDialogVisible = ref(false)
const importFormRef = ref<FormInstance>()
const importLoading = ref(false)
const importForm = reactive({
  changeDescription: '',
  file: null as File | null
})

const importRules: FormRules = {
  changeDescription: [{ required: true, message: '请输入版本变更说明', trigger: 'blur' }]
}

function handleImport() {
  importForm.changeDescription = ''
  importForm.file = null
  importDialogVisible.value = true
}

function handleFileChange(uploadFile: UploadFile) {
  importForm.file = uploadFile.raw || null
}

async function handleImportSubmit() {
  await importFormRef.value?.validate()
  if (!importForm.file) {
    ElMessage.warning('请选择要导入的Excel文件')
    return
  }

  const program = versionList.value.length > 0 ? null : null
  const collegeId = route.query.collegeId as string
  const majorId = route.query.majorId as string

  importLoading.value = true
  try {
    await trainingProgramApi.importExcel(
      collegeId || '',
      majorId || '',
      importForm.file,
      importForm.changeDescription
    )
    ElMessage.success('导入成功，已创建新版本')
    importDialogVisible.value = false
    loadDetail()
    loadVersionList()
  } catch (error) {
    console.error('导入失败:', error)
  } finally {
    importLoading.value = false
  }
}

onMounted(() => {
  loadDetail()
  loadVersionList()
})
</script>

<style scoped lang="scss">
.training-program-detail {
  padding: 20px;

  .page-header-content {
    display: flex;
    align-items: center;
    gap: 12px;

    .page-title {
      font-size: 18px;
      font-weight: 600;
    }

    .version-tag {
      font-weight: 500;
    }
  }

  .header-actions {
    display: flex;
    gap: 8px;
  }

  .detail-layout {
    display: flex;
    gap: 20px;
    margin-top: 20px;

    .main-content {
      flex: 1;
      min-width: 0;
    }

    .version-sidebar {
      width: 300px;
      flex-shrink: 0;

      .sidebar-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .version-timeline {
        max-height: calc(100vh - 320px);
        overflow-y: auto;
        padding-right: 4px;

        .version-item {
          cursor: pointer;
          padding: 8px;
          border-radius: 6px;
          transition: background-color 0.2s;

          &:hover {
            background-color: #f5f7fa;
          }

          &.active {
            background-color: #ecf5ff;
            border: 1px solid #b3d8ff;
          }

          .version-item-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 4px;

            .version-number {
              font-weight: 600;
              font-size: 14px;
              color: #303133;
            }
          }

          .version-item-info {
            display: flex;
            flex-direction: column;
            gap: 2px;
            font-size: 12px;
            color: #909399;

            .version-name {
              color: #606266;
              font-size: 13px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          .version-desc {
            margin-top: 4px;
            font-size: 12px;
            color: #909399;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .version-current-badge {
            margin-top: 4px;
            font-size: 11px;
            color: #409eff;
            font-weight: 500;
          }
        }
      }
    }
  }

  .import-tip {
    margin-bottom: 16px;
  }

  .import-form {
    margin-top: 12px;
  }

  .version-detail-dialog {
    .version-detail-actions {
      margin-top: 20px;
      display: flex;
      gap: 10px;
      justify-content: flex-end;
    }
  }
}
</style>
