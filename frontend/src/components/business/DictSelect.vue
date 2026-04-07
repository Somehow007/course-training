<template>
  <el-select
    v-model="selectedValue"
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    :loading="loading"
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
import { ref, watch } from 'vue'
import { useDict } from '@/composables/useDict'

const props = defineProps<{
  modelValue?: string | number
  dictType: string
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string | number | undefined]
  change: [value: string | number | undefined]
}>()

const selectedValue = ref<string | number | undefined>(props.modelValue)
const { options, loading } = useDict(props.dictType)

watch(() => props.modelValue, (val) => {
  selectedValue.value = val
})

function handleChange(val: string | number | undefined) {
  emit('update:modelValue', val)
  emit('change', val)
}
</script>
