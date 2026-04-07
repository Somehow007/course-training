<template>
  <div class="error-page">
    <div class="error-content">
      <h1>403</h1>
      <p>抱歉，您没有权限访问此页面</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<style scoped lang="scss">
.error-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.error-content {
  text-align: center;

  h1 {
    font-size: 120px;
    color: #f56c6c;
    margin-bottom: 20px;
  }

  p {
    font-size: 18px;
    color: #606266;
    margin-bottom: 30px;
  }
}
</style>
