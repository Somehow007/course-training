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
import { ref, watch, onMounted, computed } from 'vue'
import { majorApi } from '@/api/major'
import type { SelectOption } from '@/types/api'

const props = defineProps<{
  modelValue?: string | number
  collegeId?: string | number
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string | number | undefined]
  change: [value: string | number | undefined]
}>()

const selectedValue = ref<string | number | undefined>(props.modelValue)
const allOptions = ref<SelectOption[]>([])
const loading = ref(false)

interface MajorOption extends SelectOption {
  collegeId: string
}

const allMajorOptions = ref<MajorOption[]>([])

const options = computed(() => {
  if (!props.collegeId) return allOptions.value
  return allMajorOptions.value.filter(item => item.collegeId === props.collegeId)
})

watch(() => props.modelValue, (val) => {
  selectedValue.value = val
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await majorApi.page({ size: 100 })
    allMajorOptions.value = res.records.map((item) => ({
      label: item.majorName,
      value: item.id,
      collegeId: item.collegeId
    }))
    allOptions.value = allMajorOptions.value
  } finally {
    loading.value = false
  }
})

function handleChange(val: string | number | undefined) {
  emit('update:modelValue', val)
  emit('change', val)
}
</script>
