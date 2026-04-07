<template>
  <div class="custom-pagination" :class="{ 'is-loading': loading }">
    <!-- 分页信息 -->
    <div class="pagination-info">
      <span class="info-text">共 {{ total }} 条记录</span>
    </div>

    <!-- 分页控制按钮组 -->
    <div class="pagination-controls">
      <!-- 每页条数选择器 -->
      <div class="size-selector">
        <el-select
          v-model="internalPageSize"
          :disabled="loading || total === 0"
          size="default"
          @change="handleSizeChange"
        >
          <el-option
            v-for="size in pageSizes"
            :key="size"
            :label="`${size} 条/页`"
            :value="size"
          />
        </el-select>
      </div>

      <!-- 上一页按钮 -->
      <el-button
        :disabled="isFirstPage || loading || total === 0"
        @click="goToPrevPage"
        class="nav-button prev-button"
      >
        <el-icon><ArrowLeft /></el-icon>
        <span class="button-text">上一页</span>
      </el-button>

      <!-- 页码按钮组 -->
      <div class="page-numbers">
        <button
          v-for="page in visiblePages"
          :key="page"
          :class="['page-number', {
            'active': page === internalCurrentPage,
            'ellipsis': page === -1,
            'disabled': loading
          }]"
          :disabled="page === -1 || loading"
          @click="page !== -1 && goToPage(page)"
        >
          {{ page === -1 ? '...' : page }}
        </button>
      </div>

      <!-- 下一页按钮 -->
      <el-button
        :disabled="isLastPage || loading || total === 0"
        @click="goToNextPage"
        class="nav-button next-button"
      >
        <span class="button-text">下一页</span>
        <el-icon><ArrowRight /></el-icon>
      </el-button>

      <!-- 页码跳转 -->
      <div class="jumper" v-if="showJumper">
        <span class="jumper-text">跳至</span>
        <el-input-number
          v-model="jumpPage"
          :min="1"
          :max="totalPages"
          :controls="false"
          size="default"
          :disabled="loading || total === 0"
          @change="handleJump"
          class="jumper-input"
        />
        <span class="jumper-text">页</span>
        <el-button
          type="primary"
          size="default"
          :disabled="loading || total === 0 || !isValidJumpPage"
          @click="handleJump"
          class="jump-button"
        >
          跳转
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

interface Props {
  current: number
  pageSize: number
  total: number
  pageSizes?: number[]
  showJumper?: boolean
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  pageSizes: () => [10, 20, 50, 100],
  showJumper: true,
  loading: false
})

const emit = defineEmits<{
  (e: 'update:current', value: number): void
  (e: 'update:pageSize', value: number): void
  (e: 'change', value: { current: number; pageSize: number }): void
}>()

// 内部状态
const internalCurrentPage = ref(props.current)
const internalPageSize = ref(props.pageSize)
const jumpPage = ref(props.current)

// 计算属性
const totalPages = computed(() => {
  if (props.total === 0 || internalPageSize.value === 0) return 1
  return Math.ceil(props.total / internalPageSize.value)
})

const isFirstPage = computed(() => internalCurrentPage.value <= 1)

const isLastPage = computed(() => internalCurrentPage.value >= totalPages.value)

const isValidJumpPage = computed(() => {
  const page = jumpPage.value
  return Number.isInteger(page) && page >= 1 && page <= totalPages.value && page !== internalCurrentPage.value
})

// 生成可见的页码数组
const visiblePages = computed(() => {
  const current = internalCurrentPage.value
  const total = totalPages.value
  const pages: (number | -1)[] = []

  if (total <= 7) {
    // 总页数小于等于7，显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 总页数大于7，显示部分页码带省略号
    pages.push(1)

    if (current > 3) {
      pages.push(-1) // 省略号
    }

    const start = Math.max(2, current - 1)
    const end = Math.min(total - 1, current + 1)

    for (let i = start; i <= end; i++) {
      if (!pages.includes(i)) {
        pages.push(i)
      }
    }

    if (current < total - 2) {
      pages.push(-1) // 省略号
    }

    pages.push(total)
  }

  return pages
})

// 监听外部props变化
watch(() => props.current, (val) => {
  internalCurrentPage.value = val
  jumpPage.value = val
})

watch(() => props.pageSize, (val) => {
  internalPageSize.value = val
})

// 方法
function goToPage(page: number) {
  if (page === internalCurrentPage.value || page < 1 || page > totalPages.value) return

  internalCurrentPage.value = page
  jumpPage.value = page
  emit('update:current', page)
  emit('change', { current: page, pageSize: internalPageSize.value })
}

function goToPrevPage() {
  if (isFirstPage.value) return
  goToPage(internalCurrentPage.value - 1)
}

function goToNextPage() {
  if (isLastPage.value) return
  goToPage(internalCurrentPage.value + 1)
}

function handleSizeChange(size: number) {
  internalPageSize.value = size
  // 切换每页大小时重置到第一页
  internalCurrentPage.value = 1
  jumpPage.value = 1
  emit('update:pageSize', size)
  emit('update:current', 1)
  emit('change', { current: 1, pageSize: size })
}

function handleJump() {
  if (!isValidJumpPage.value) return
  goToPage(jumpPage.value)
}
</script>

<style scoped lang="scss">
.custom-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  gap: 20px;
  flex-wrap: wrap;

  &.is-loading {
    opacity: 0.6;
    pointer-events: none;
  }
}

.pagination-info {
  .info-text {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.size-selector {
  :deep(.el-select) {
    width: 120px;
  }
}

.nav-button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 6px;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
  background-color: #fff;
  color: #606266;
  cursor: pointer;

  &:hover:not(:disabled) {
    color: #409eff;
    border-color: #c6e2ff;
    background-color: #ecf5ff;
  }

  &:active:not(:disabled) {
    transform: scale(0.95);
  }

  &:disabled {
    cursor: not-allowed;
    opacity: 0.5;
    background-color: #f5f7fa;
  }

  .button-text {
    font-size: 14px;
  }
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 6px;
}

.page-number {
  min-width: 36px;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background-color: #fff;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  &:hover:not(.active):not(.ellipsis):not(.disabled) {
    color: #409eff;
    border-color: #c6e2ff;
    background-color: #ecf5ff;
  }

  &:active:not(.active):not(.ellipsis):not(.disabled) {
    transform: scale(0.95);
  }

  &.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-color: transparent;
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    &:hover {
      background: linear-gradient(135deg, #5a6fd6 0%, #6a4192 100%);
    }
  }

  &.ellipsis {
    border: none;
    background: none;
    cursor: default;
    color: #909399;
    letter-spacing: 2px;

    &:hover {
      background: none;
      color: #909399;
    }
  }

  &.disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }
}

.jumper {
  display: flex;
  align-items: center;
  gap: 8px;

  .jumper-text {
    font-size: 14px;
    color: #606266;
  }

  .jumper-input {
    width: 80px;
  }

  .jump-button {
    padding: 8px 16px;
    border-radius: 6px;
    font-size: 14px;
  }
}

/* 响应式设计 */
@media screen and (max-width: 1200px) {
  .custom-pagination {
    gap: 16px;
  }

  .pagination-controls {
    gap: 8px;
  }

  .nav-button .button-text {
    display: none;
  }

  .jumper {
    .jumper-text:last-of-type {
      display: none;
    }
  }
}

@media screen and (max-width: 768px) {
  .custom-pagination {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .pagination-info {
    text-align: center;
  }

  .pagination-controls {
    justify-content: center;
  }

  .size-selector {
    :deep(.el-select) {
      width: 100%;
    }
  }

  .page-numbers {
    order: -1;
  }
}
</style>
