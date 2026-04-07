<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon college">
              <el-icon :size="30"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.collegeCount }}</div>
              <div class="stat-label">学院数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon major">
              <el-icon :size="30"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.majorCount }}</div>
              <div class="stat-label">专业数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon course">
              <el-icon :size="30"><Collection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.courseCount }}</div>
              <div class="stat-label">课程数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon program">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.programCount }}</div>
              <div class="stat-label">培养计划</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="welcome-card">
      <h2>欢迎使用培养计划管理系统</h2>
      <p>当前登录用户：{{ userStore.username }}</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { OfficeBuilding, Reading, Collection, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { collegeApi } from '@/api/college'
import { majorApi } from '@/api/major'
import { courseApi } from '@/api/course'
import { trainingProgramApi } from '@/api/training-program'

const userStore = useUserStore()

const stats = reactive({
  collegeCount: 0,
  majorCount: 0,
  courseCount: 0,
  programCount: 0
})

onMounted(async () => {
  try {
    const [collegeRes, majorRes, courseRes, programRes] = await Promise.all([
      collegeApi.page({ size: 1 }),
      majorApi.page({ size: 1 }),
      courseApi.page({ size: 1 }),
      trainingProgramApi.page({ size: 1 })
    ])
    stats.collegeCount = collegeRes.total
    stats.majorCount = majorRes.total
    stats.courseCount = courseRes.total
    stats.programCount = programRes.total
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    margin-bottom: 20px;

    .stat-content {
      display: flex;
      align-items: center;
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;

      &.college {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.major {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.course {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      &.program {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }

    .stat-info {
      margin-left: 15px;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .welcome-card {
    h2 {
      margin-bottom: 10px;
      color: #303133;
    }

    p {
      color: #606266;
    }
  }
}
</style>
