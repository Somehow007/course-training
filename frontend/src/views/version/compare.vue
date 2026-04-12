<template>
  <div class="version-compare-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-page-header @back="goBack" title="返回">
            <template #content>
              <span class="text-large font-600 mr-3">版本对比</span>
            </template>
          </el-page-header>
        </div>
      </template>

      <div v-loading="loading" class="compare-content">
        <div class="version-info">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card shadow="hover" class="version-card source-card">
                <template #header>
                  <div class="version-card-header">
                    <el-tag type="primary">源版本</el-tag>
                    <span class="version-name">{{ compareData?.sourceVersion?.versionName }}</span>
                  </div>
                </template>
                <div class="version-detail">
                  <p><strong>版本号：</strong>V{{ compareData?.sourceVersion?.versionNumber }}</p>
                  <p><strong>创建时间：</strong>{{ formatTime(compareData?.sourceVersion?.createTime) }}</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover" class="version-card target-card">
                <template #header>
                  <div class="version-card-header">
                    <el-tag type="success">目标版本</el-tag>
                    <span class="version-name">{{ compareData?.targetVersion?.versionName }}</span>
                  </div>
                </template>
                <div class="version-detail">
                  <p><strong>版本号：</strong>V{{ compareData?.targetVersion?.versionNumber }}</p>
                  <p><strong>创建时间：</strong>{{ formatTime(compareData?.targetVersion?.createTime) }}</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="statistics-section">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-card stat-add">
                <div class="stat-value">{{ compareData?.statistics?.addCount || 0 }}</div>
                <div class="stat-label">新增课程</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card stat-update">
                <div class="stat-value">{{ compareData?.statistics?.updateCount || 0 }}</div>
                <div class="stat-label">修改课程</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card stat-delete">
                <div class="stat-value">{{ compareData?.statistics?.deleteCount || 0 }}</div>
                <div class="stat-label">删除课程</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card stat-total">
                <div class="stat-value">{{ compareData?.statistics?.totalCount || 0 }}</div>
                <div class="stat-label">总差异数</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="differences-section">
          <div class="differences-header">
            <span class="differences-title">差异详情</span>
            <el-radio-group v-model="activeTab" size="small">
              <el-radio-button value="all">全部</el-radio-button>
              <el-radio-button value="add">新增</el-radio-button>
              <el-radio-button value="update">修改</el-radio-button>
              <el-radio-button value="delete">删除</el-radio-button>
            </el-radio-group>
          </div>

          <DifferenceList :differences="filteredDifferences" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { versionApi, type VersionDifference } from '@/api/version'
import DifferenceList from './components/DifferenceList.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const activeTab = ref('all')
const compareData = ref<any>(null)

const filteredDifferences = computed(() => {
  if (!compareData.value?.differences) return []
  if (activeTab.value === 'all') return compareData.value.differences
  const typeMap: Record<string, string> = { add: 'ADD', update: 'UPDATE', delete: 'DELETE' }
  return compareData.value.differences.filter((d: VersionDifference) => d.changeType === typeMap[activeTab.value])
})

function formatTime(time: string | undefined): string {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

async function fetchCompareData() {
  const { sourceId, targetId } = route.query

  if (!sourceId || !targetId) {
    ElMessage.error('缺少版本对比参数')
    goBack()
    return
  }

  loading.value = true
  try {
    const data = await versionApi.compareVersions({
      sourceVersionId: sourceId as string,
      targetVersionId: targetId as string
    })
    compareData.value = data
  } catch (error) {
    ElMessage.error('获取版本对比数据失败')
    goBack()
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/version')
}

onMounted(() => {
  fetchCompareData()
})
</script>

<style scoped lang="scss">
.version-compare-container {
  padding: 20px;

  .card-header {
    display: flex;
    align-items: center;
  }

  .compare-content {
    .version-info {
      margin-bottom: 24px;

      .version-card {
        .version-card-header {
          display: flex;
          align-items: center;
          gap: 10px;

          .version-name {
            font-size: 16px;
            font-weight: 600;
          }
        }

        .version-detail {
          p {
            margin: 6px 0;
            color: #606266;
            font-size: 14px;
          }
        }
      }
    }

    .statistics-section {
      margin-bottom: 24px;

      .stat-card {
        text-align: center;
        padding: 20px 16px;
        border-radius: 8px;
        transition: transform 0.2s;

        &:hover {
          transform: translateY(-2px);
        }

        .stat-value {
          font-size: 32px;
          font-weight: 700;
          line-height: 1.2;
        }

        .stat-label {
          font-size: 14px;
          margin-top: 4px;
          color: #606266;
        }
      }

      .stat-add {
        background-color: #f0f9eb;
        .stat-value { color: #67c23a; }
      }

      .stat-update {
        background-color: #fdf6ec;
        .stat-value { color: #e6a23c; }
      }

      .stat-delete {
        background-color: #fef0f0;
        .stat-value { color: #f56c6c; }
      }

      .stat-total {
        background-color: #ecf5ff;
        .stat-value { color: #409eff; }
      }
    }

    .differences-section {
      .differences-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 1px solid #ebeef5;

        .differences-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }
}
</style>
