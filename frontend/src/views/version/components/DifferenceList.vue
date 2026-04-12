<template>
  <div class="difference-list">
    <el-empty v-if="differences.length === 0" description="暂无差异" />

    <div v-else class="difference-items">
      <div
        v-for="(diff, index) in differences"
        :key="index"
        class="difference-item"
        :class="'diff-' + diff.changeType.toLowerCase()"
      >
        <div class="difference-header">
          <el-tag :type="getTagType(diff.changeType)" size="large" class="change-type-tag">
            {{ getChangeTypeText(diff.changeType) }}
          </el-tag>
          <span class="course-name">{{ diff.courseName }}</span>
          <div v-if="diff.changedFields && diff.changedFields.length > 0" class="changed-fields">
            <el-tag
              v-for="field in diff.changedFields"
              :key="field"
              size="small"
              type="warning"
              effect="plain"
              class="field-tag"
            >
              {{ field }}
            </el-tag>
          </div>
        </div>

        <div class="difference-content">
          <p class="description">{{ diff.description }}</p>

          <div v-if="diff.changeType === 'UPDATE'" class="detail-compare">
            <el-row :gutter="16">
              <el-col :span="12">
                <div class="value-panel old-value">
                  <div class="value-header">
                    <el-tag type="info" size="small">旧值</el-tag>
                  </div>
                  <div class="value-content">
                    <div class="value-row" v-for="field in getCompareFields(diff)" :key="'old-' + field.key">
                      <span class="field-label">{{ field.label }}：</span>
                      <span class="field-value" :class="{ 'value-changed': isFieldChanged(diff, field.key) }">
                        {{ formatFieldValue(diff.oldValue, field.key) }}
                      </span>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="value-panel new-value">
                  <div class="value-header">
                    <el-tag type="success" size="small">新值</el-tag>
                  </div>
                  <div class="value-content">
                    <div class="value-row" v-for="field in getCompareFields(diff)" :key="'new-' + field.key">
                      <span class="field-label">{{ field.label }}：</span>
                      <span class="field-value" :class="{ 'value-changed': isFieldChanged(diff, field.key) }">
                        {{ formatFieldValue(diff.newValue, field.key) }}
                      </span>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <div v-else-if="diff.changeType === 'ADD'" class="detail-info">
            <div class="value-panel add-value">
              <div class="value-content">
                <div class="value-row" v-for="field in getDisplayFields(diff.newValue)" :key="'add-' + field.key">
                  <span class="field-label">{{ field.label }}：</span>
                  <span class="field-value">{{ field.value }}</span>
                </div>
              </div>
            </div>
          </div>

          <div v-else-if="diff.changeType === 'DELETE'" class="detail-info">
            <div class="value-panel delete-value">
              <div class="value-content">
                <div class="value-row" v-for="field in getDisplayFields(diff.oldValue)" :key="'del-' + field.key">
                  <span class="field-label">{{ field.label }}：</span>
                  <span class="field-value">{{ field.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { VersionDifference } from '@/api/version'

interface Props {
  differences: VersionDifference[]
}

defineProps<Props>()

const FIELD_LABELS: Record<string, string> = {
  courseNature: '课程性质',
  courseName: '课程名称',
  totalCredits: '总学分',
  totalHours: '总学时',
  totalWeeks: '总学时(周)',
  hourTeach: '授课学时',
  hourPractice: '实验学时',
  hourOperation: '上机学时',
  hourOutside: '实践学时',
  hourWeek: '周学时',
  requiredElective: '选修学分要求',
  term: '建议学期',
  remark: '备注'
}

const COMPARE_FIELDS = [
  { key: 'totalCredits', label: '总学分' },
  { key: 'totalHours', label: '总学时' },
  { key: 'totalWeeks', label: '总学时(周)' },
  { key: 'hourTeach', label: '授课学时' },
  { key: 'hourPractice', label: '实验学时' },
  { key: 'hourOperation', label: '上机学时' },
  { key: 'hourOutside', label: '实践学时' },
  { key: 'term', label: '建议学期' },
  { key: 'remark', label: '备注' }
]

const DISPLAY_FIELDS = [
  { key: 'totalCredits', label: '总学分' },
  { key: 'totalHours', label: '总学时' },
  { key: 'term', label: '建议学期' },
  { key: 'hourTeach', label: '授课学时' },
  { key: 'hourPractice', label: '实验学时' },
  { key: 'remark', label: '备注' }
]

function getTagType(changeType: string): string {
  const types: Record<string, string> = { ADD: 'success', UPDATE: 'warning', DELETE: 'danger' }
  return types[changeType] || 'info'
}

function getChangeTypeText(changeType: string): string {
  const texts: Record<string, string> = { ADD: '新增', UPDATE: '修改', DELETE: '删除' }
  return texts[changeType] || '未知'
}

function getCompareFields(_diff: VersionDifference) {
  return COMPARE_FIELDS
}

function getDisplayFields(data: any) {
  if (!data) return []
  return DISPLAY_FIELDS
    .filter(f => data[f.key] != null && data[f.key] !== '')
    .map(f => ({ ...f, value: String(data[f.key]) }))
}

function formatFieldValue(data: any, key: string): string {
  if (!data) return '-'
  const value = data[key]
  if (value == null) return '-'
  return String(value)
}

function isFieldChanged(diff: VersionDifference, key: string): boolean {
  if (!diff.changedFields) return false
  const label = FIELD_LABELS[key] || key
  return diff.changedFields.includes(label)
}
</script>

<style scoped lang="scss">
.difference-list {
  .difference-items {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .difference-item {
    border-radius: 8px;
    padding: 16px;
    border: 1px solid #ebeef5;
    transition: box-shadow 0.2s;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    }

    &.diff-add {
      border-left: 4px solid #67c23a;
    }

    &.diff-update {
      border-left: 4px solid #e6a23c;
    }

    &.diff-delete {
      border-left: 4px solid #f56c6c;
    }

    .difference-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 12px;
      flex-wrap: wrap;

      .change-type-tag {
        flex-shrink: 0;
      }

      .course-name {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .changed-fields {
        display: flex;
        gap: 4px;
        flex-wrap: wrap;

        .field-tag {
          font-size: 11px;
        }
      }
    }

    .difference-content {
      .description {
        margin: 0 0 12px 0;
        color: #606266;
        font-size: 14px;
      }

      .detail-compare,
      .detail-info {
        margin-top: 8px;
      }

      .value-panel {
        border-radius: 6px;
        overflow: hidden;

        .value-header {
          padding: 8px 12px;
          background-color: #f5f7fa;
          border-bottom: 1px solid #ebeef5;
        }

        .value-content {
          padding: 12px;

          .value-row {
            display: flex;
            align-items: baseline;
            padding: 4px 0;
            font-size: 13px;

            .field-label {
              color: #909399;
              min-width: 80px;
              flex-shrink: 0;
            }

            .field-value {
              color: #303133;

              &.value-changed {
                color: #e6a23c;
                font-weight: 600;
                background-color: #fdf6ec;
                padding: 1px 6px;
                border-radius: 3px;
              }
            }
          }
        }
      }

      .old-value {
        border: 1px solid #e4e7ed;
      }

      .new-value {
        border: 1px solid #e4e7ed;
      }

      .add-value {
        background-color: #f0f9eb;
        border: 1px solid #c2e7b0;
      }

      .delete-value {
        background-color: #fef0f0;
        border: 1px solid #fbc4c4;
      }
    }
  }
}
</style>
