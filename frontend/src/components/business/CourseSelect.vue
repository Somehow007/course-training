<template>
  <el-select
    v-model="selectedValue"
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    :loading="loading"
    filterable
    popper-class="custom-select-dropdown"
    @change="handleChange"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { courseApi } from '@/api/course'
import type { SelectOption } from '@/types/api'

interface CourseInfo {
  courseId: string
  courseName: string
  courseNature: number
  collegeId: string
}

const props = defineProps<{
  modelValue?: string | number
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string | number | undefined]
  'change': [value: string | number | undefined, courseInfo?: CourseInfo]
}>()

const selectedValue = ref<string | number | undefined>(props.modelValue)
const options = ref<SelectOption[]>([])
const courseMap = ref<Map<string, CourseInfo>>(new Map())
const loading = ref(false)

watch(() => props.modelValue, (val) => {
  selectedValue.value = val
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await courseApi.page({ size: 1000 })
    options.value = res.records.map((item) => ({
      label: item.courseName,
      value: item.courseId
    }))
    res.records.forEach((item) => {
      courseMap.value.set(item.courseId, {
        courseId: item.courseId,
        courseName: item.courseName,
        courseNature: item.courseNature,
        collegeId: String(item.collegeId || '')
      })
    })
  } finally {
    loading.value = false
  }
})

function handleChange(val: string | number | undefined) {
  emit('update:modelValue', val)
  const courseInfo = val ? courseMap.value.get(String(val)) : undefined
  emit('change', val, courseInfo)
}

function getCourseInfo(courseId: string): CourseInfo | undefined {
  return courseMap.value.get(courseId)
}

defineExpose({
  getCourseInfo
})
</script>
