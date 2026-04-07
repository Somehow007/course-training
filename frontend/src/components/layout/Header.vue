<template>
  <div class="header">
    <div class="left">
      <el-icon class="collapse-btn" @click="toggleSidebar">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
    </div>
    <div class="right">
      <template v-if="userStore.isLoggedIn">
        <el-dropdown>
          <span class="user-info">
            <el-icon><User /></el-icon>
            <span class="username">{{ userStore.username }}</span>
            <el-icon class="arrow"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <span class="not-logged-in" @click="handleGoToLogin">
          <el-icon><User /></el-icon>
          <span>未登录</span>
        </span>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { Fold, Expand, User, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

function toggleSidebar() {
  appStore.toggleSidebar()
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

function handleGoToLogin() {
  router.push('/login')
}
</script>

<style scoped lang="scss">
.header {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;

  &:hover {
    color: #409eff;
  }
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;

  .username {
    margin: 0 5px;
  }

  .arrow {
    font-size: 12px;
  }
}

.not-logged-in {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #909399;
  transition: color 0.3s;

  &:hover {
    color: #409eff;
  }

  span {
    margin-left: 5px;
  }
}
</style>
