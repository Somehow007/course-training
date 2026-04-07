<template>
  <div class="sidebar">
    <div class="logo">
      <span v-if="!collapsed">培养计划管理系统</span>
      <span v-else>培</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :collapse-transition="false"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      router
    >
      <template v-for="route in menuRoutes" :key="route.path">
        <el-sub-menu v-if="route.children && getVisibleChildren(route).length > 1" :index="route.path">
          <template #title>
            <el-icon><component :is="route.meta?.icon" /></el-icon>
            <span>{{ route.meta?.title }}</span>
          </template>
          <el-menu-item
            v-for="child in getVisibleChildren(route)"
            :key="child.path"
            :index="`${route.path}/${child.path}`"
          >
            {{ child.meta?.title }}
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item v-else :index="getMenuIndex(route)">
          <el-icon><component :is="route.meta?.icon" /></el-icon>
          <template #title>{{ route.meta?.title }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { routes } from '@/router/routes'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  collapsed: boolean
}>()

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => {
  const { path } = route
  return path
})

// 过滤出需要显示的菜单
const menuRoutes = computed(() => {
  const mainRoute = routes.find((r) => r.path === '/')
  if (!mainRoute?.children) return []

  return mainRoute.children
    .filter((child) => !child.meta?.hidden)
    .filter((child) => {
      const permission = child.meta?.permission as string | undefined
      if (!permission) return true
      return userStore.identity === permission
    })
})

function getMenuIndex(routeItem: any) {
  const visibleChildren = getVisibleChildren(routeItem)
  if (visibleChildren.length === 1) {
    const child = visibleChildren[0]
    return `/${routeItem.path}/${child.path}`
  }
  return `/${routeItem.path}`
}

function getVisibleChildren(routeItem: any) {
  if (!routeItem.children) return []
  return routeItem.children.filter((child: any) => !child.meta?.hidden)
}
</script>

<style scoped lang="scss">
.sidebar {
  height: 100%;
}

.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  background-color: #263445;
}

.el-menu {
  border-right: none;
}
</style>
