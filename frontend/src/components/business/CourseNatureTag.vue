<template>
  <el-tag :type="tagType" size="small">{{ text }}</el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  nature: number
}>()

const tagType = computed(() => {
  // 0: 选修, 1: 必修
  return props.nature === 1 ? 'success' : 'warning'
})

const text = computed(() => {
  return props.nature === 1 ? '必修' : '选修'
})
</script>
