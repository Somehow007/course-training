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
import { collegeApi } from '@/api/college'
import type { SelectOption } from '@/types/api'

const props = defineProps<{
  modelValue?: string | number
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string | number | undefined]
  change: [value: string | number | undefined]
}>()

const selectedValue = ref<string | number | undefined>(props.modelValue)
const options = ref<SelectOption[]>([])
const loading = ref(false)

watch(() => props.modelValue, (val) => {
  selectedValue.value = val
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await collegeApi.page({ size: 100 })
    options.value = res.records.map((item) => ({
      label: item.collegeName,
      value: item.id
    }))
  } finally {
    loading.value = false
  }
})

function handleChange(val: string | number | undefined) {
  emit('update:modelValue', val)
  emit('change', val)
}
</script>
