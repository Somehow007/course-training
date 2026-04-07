<template>
  <el-container class="app-layout">
    <el-aside :width="sidebarWidth" class="app-aside">
      <Sidebar :collapsed="appStore.sidebarCollapsed" />
    </el-aside>
    <el-container>
      <el-header class="app-header">
        <Header />
      </el-header>
      <el-main class="app-main">
        <Breadcrumb />
        <div class="main-content">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'
import Breadcrumb from './Breadcrumb.vue'

const appStore = useAppStore()

const sidebarWidth = computed(() =>
  appStore.sidebarCollapsed ? '64px' : '210px'
)
</script>

<style scoped lang="scss">
.app-layout {
  height: 100vh;
}

.app-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.app-header {
  height: 50px;
  padding: 0;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.app-main {
  background-color: #f5f7fa;
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.main-content {
  padding: 20px;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: auto;
}
</style>
