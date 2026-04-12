<template>
  <el-dialog
    v-model="visible"
    title="重置密码"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="目标用户">
        <el-input :model-value="username" disabled />
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
          @input="handlePasswordChange"
        />
      </el-form-item>
      
      <el-form-item label="密码强度">
        <div class="password-strength">
          <div class="strength-bar">
            <div
              class="strength-fill"
              :style="{
                width: strengthPercentage + '%',
                backgroundColor: strengthColor
              }"
            />
          </div>
          <span class="strength-text" :style="{ color: strengthColor }">
            {{ strengthText }}
          </span>
        </div>
        <div class="password-tips">
          <p>密码建议：</p>
          <ul>
            <li :class="{ active: hasMinLength }">至少8个字符</li>
            <li :class="{ active: hasUpperCase }">包含大写字母</li>
            <li :class="{ active: hasLowerCase }">包含小写字母</li>
            <li :class="{ active: hasNumber }">包含数字</li>
            <li :class="{ active: hasSpecialChar }">包含特殊字符</li>
          </ul>
        </div>
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button
        type="primary"
        :loading="loading"
        :disabled="!isFormValid"
        @click="handleSubmit"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { userApi } from '@/api/user'

interface Props {
  modelValue: boolean
  userId: string
  username: string
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = ref({
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const hasMinLength = computed(() => form.value.newPassword.length >= 8)
const hasUpperCase = computed(() => /[A-Z]/.test(form.value.newPassword))
const hasLowerCase = computed(() => /[a-z]/.test(form.value.newPassword))
const hasNumber = computed(() => /[0-9]/.test(form.value.newPassword))
const hasSpecialChar = computed(() => /[!@#$%^&*(),.?":{}|<>]/.test(form.value.newPassword))

const strengthScore = computed(() => {
  let score = 0
  if (hasMinLength.value) score++
  if (hasUpperCase.value) score++
  if (hasLowerCase.value) score++
  if (hasNumber.value) score++
  if (hasSpecialChar.value) score++
  return score
})

const strengthPercentage = computed(() => {
  return (strengthScore.value / 5) * 100
})

const strengthText = computed(() => {
  if (form.value.newPassword === '') return ''
  if (strengthScore.value <= 1) return '弱'
  if (strengthScore.value <= 2) return '较弱'
  if (strengthScore.value <= 3) return '中等'
  if (strengthScore.value <= 4) return '较强'
  return '强'
})

const strengthColor = computed(() => {
  if (form.value.newPassword === '') return '#dcdfe6'
  if (strengthScore.value <= 1) return '#f56c6c'
  if (strengthScore.value <= 2) return '#e6a23c'
  if (strengthScore.value <= 3) return '#409eff'
  if (strengthScore.value <= 4) return '#67c23a'
  return '#67c23a'
})

const isFormValid = computed(() => {
  return (
    form.value.newPassword.length >= 6 &&
    form.value.confirmPassword !== '' &&
    form.value.newPassword === form.value.confirmPassword
  )
})

function handlePasswordChange() {
  if (form.value.confirmPassword !== '') {
    formRef.value?.validateField('confirmPassword')
  }
}

async function handleSubmit() {
  await formRef.value?.validate()
  
  loading.value = true
  try {
    await userApi.resetPassword({
      userId: props.userId,
      newPassword: form.value.newPassword
    })
    ElMessage.success('密码重置成功')
    emit('success')
    handleClose()
  } catch (error: any) {
    ElMessage.error(error.message || '密码重置失败')
  } finally {
    loading.value = false
  }
}

function handleClose() {
  formRef.value?.resetFields()
  form.value.newPassword = ''
  form.value.confirmPassword = ''
  visible.value = false
}

watch(visible, (newVal) => {
  if (!newVal) {
    handleClose()
  }
})
</script>

<style scoped lang="scss">
.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  
  .strength-bar {
    flex: 1;
    height: 8px;
    background-color: #ebeef5;
    border-radius: 4px;
    overflow: hidden;
    
    .strength-fill {
      height: 100%;
      transition: all 0.3s ease;
    }
  }
  
  .strength-text {
    font-size: 14px;
    font-weight: 500;
    min-width: 40px;
  }
}

.password-tips {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  
  p {
    margin: 0 0 4px 0;
    font-weight: 500;
  }
  
  ul {
    margin: 0;
    padding-left: 20px;
    
    li {
      margin: 2px 0;
      transition: color 0.3s ease;
      
      &.active {
        color: #67c23a;
      }
    }
  }
}
</style>
