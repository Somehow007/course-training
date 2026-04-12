<template>
  <div class="training-program-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>培养计划详情</span>
          <el-button type="primary" @click="handleAddCourse">添加课程</el-button>
        </div>
      </template>

      <el-table :data="detailData" v-loading="loading" stripe border empty-text="暂无课程数据">
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
        <el-table-column prop="hourTeach" label="授课学时" width="80" />
        <el-table-column prop="hourPractice" label="实验学时" width="80" />
        <el-table-column prop="hourOperation" label="上机学时" width="80" />
        <el-table-column prop="hourOutside" label="实践学时" width="80" />
        <el-table-column prop="term" label="建议学期" width="80" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditCourse(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDeleteCourse(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加课程对话框 -->
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

    <!-- 编辑课程对话框 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { trainingProgramApi } from '@/api/training-program'
import type { TrainingProgramDetailItem } from '@/types/api'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import MajorSelect from '@/components/business/MajorSelect.vue'
import CourseSelect from '@/components/business/CourseSelect.vue'
import CourseNatureTag from '@/components/business/CourseNatureTag.vue'
import { useDictStore } from '@/stores/dict'
import type { SysDictPageItem } from '@/types/api'

const route = useRoute()
const dictStore = useDictStore()
const programId = route.params.id as string

const loading = ref(false)
const detailData = ref<TrainingProgramDetailItem[]>([])
const courseTypeDict = ref<SysDictPageItem[]>([])

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
    console.error('加载详情失败:', error)
  } finally {
    loading.value = false
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
    
    if (orderA !== orderB) {
      return orderA - orderB
    }
    
    if (a.courseNature !== b.courseNature) {
      return b.courseNature - a.courseNature
    }
    
    return 0
  })
}

// 添加课程对话框
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
  addForm.courseId = ''
  addForm.collegeId = ''
  addForm.majorId = ''
  addForm.totalCredits = 0
  addForm.totalHours = 0
  addForm.hourTeach = 0
  addForm.hourPractice = 0
  addForm.hourOperation = 0
  addForm.hourOutside = 0
  addForm.term = undefined
  addForm.remark = ''
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

// 编辑课程对话框
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
  editForm.id = String(row.id)
  editForm.courseName = row.courseName
  editForm.majorId = String(row.majorId)
  editForm.totalCredits = row.totalCredits
  editForm.totalHours = row.totalHours
  editForm.hourTeach = row.hourTeach
  editForm.hourPractice = row.hourPractice
  editForm.hourOperation = row.hourOperation
  editForm.hourOutside = row.hourOutside
  editForm.term = row.term ?? undefined
  editForm.remark = row.remark ?? ''
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

// 删除课程
async function handleDeleteCourse(row: TrainingProgramDetailItem) {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${row.courseName}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await trainingProgramApi.deleteCourse(String(row.id))
    ElMessage.success('删除成功')
    loadDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
