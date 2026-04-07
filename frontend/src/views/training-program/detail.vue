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
      </el-table>
    </el-card>

    <!-- 添加课程对话框 -->
    <el-dialog v-model="dialogVisible" title="添加课程" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-input v-model="form.courseId" placeholder="请输入课程ID" />
        </el-form-item>
        <el-form-item label="开课学院" prop="collegeId">
          <CollegeSelect v-model="form.collegeId" placeholder="请选择学院" />
        </el-form-item>
        <el-form-item label="修读专业" prop="majorId">
          <MajorSelect v-model="form.majorId" placeholder="请选择专业" />
        </el-form-item>
        <el-form-item label="总学分" prop="totalCredits">
          <el-input-number v-model="form.totalCredits" :min="0" :precision="1" />
        </el-form-item>
        <el-form-item label="总学时(小时)">
          <el-input-number v-model="form.totalHours" :min="0" />
        </el-form-item>
        <el-form-item label="授课学时">
          <el-input-number v-model="form.hourTeach" :min="0" />
        </el-form-item>
        <el-form-item label="实验学时">
          <el-input-number v-model="form.hourPractice" :min="0" />
        </el-form-item>
        <el-form-item label="上机学时">
          <el-input-number v-model="form.hourOperation" :min="0" />
        </el-form-item>
        <el-form-item label="实践学时">
          <el-input-number v-model="form.hourOutside" :min="0" />
        </el-form-item>
        <el-form-item label="建议学期">
          <el-select v-model="form.term" placeholder="请选择">
            <el-option v-for="i in 8" :key="i" :label="`第${i}学期`" :value="i" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
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
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { trainingProgramApi } from '@/api/training-program'
import type { TrainingProgramDetailItem } from '@/types/api'
import CollegeSelect from '@/components/business/CollegeSelect.vue'
import MajorSelect from '@/components/business/MajorSelect.vue'
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
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
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

const rules: FormRules = {
  courseId: [{ required: true, message: '请输入课程ID', trigger: 'blur' }],
  totalCredits: [{ required: true, message: '请输入总学分', trigger: 'blur' }]
}

function handleAddCourse() {
  form.courseId = ''
  form.collegeId = ''
  form.majorId = ''
  form.totalCredits = 0
  form.totalHours = 0
  form.hourTeach = 0
  form.hourPractice = 0
  form.hourOperation = 0
  form.hourOutside = 0
  form.term = undefined
  form.remark = ''
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    await trainingProgramApi.addCourse({
      trainingProgramId: programId,
      courseId: form.courseId,
      collegeId: form.collegeId,
      majorId: form.majorId,
      totalCredits: form.totalCredits,
      totalHours: form.totalHours,
      hourTeach: form.hourTeach,
      hourPractice: form.hourPractice,
      hourOperation: form.hourOperation,
      hourOutside: form.hourOutside,
      term: form.term,
      remark: form.remark
    })
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadDetail()
  } finally {
    submitLoading.value = false
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
