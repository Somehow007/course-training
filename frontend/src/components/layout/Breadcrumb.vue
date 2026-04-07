<template>
  <el-breadcrumb separator="/" class="breadcrumb">
    <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

interface BreadcrumbItem {
  path: string
  title: string
}

const breadcrumbs = computed<BreadcrumbItem[]>(() => {
  const matched = route.matched.filter((item) => item.meta?.title)
  const result: BreadcrumbItem[] = []

  // 添加首页
  result.push({ path: '/dashboard', title: '首页' })

  // 添加匹配的路由
  matched.forEach((item) => {
    if (item.path !== '/dashboard' && item.meta?.title) {
      result.push({
        path: item.path,
        title: item.meta.title as string
      })
    }
  })

  return result
})
</script>

<style scoped lang="scss">
.breadcrumb {
  height: 40px;
  line-height: 40px;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
}
</style>
