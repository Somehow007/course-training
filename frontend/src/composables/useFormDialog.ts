import { ref } from 'vue'
import type { FormInstance } from 'element-plus'

export function useFormDialog<T extends Record<string, any>>(
  defaultForm: T,
  submitFn: (data: T) => Promise<void>
) {
  const visible = ref(false)
  const formRef = ref<FormInstance>()
  const formData = ref<T>({ ...defaultForm } as T)
  const loading = ref(false)
  const isEdit = ref(false)

  function open(data?: T) {
    visible.value = true
    if (data) {
      formData.value = { ...data }
      isEdit.value = true
    } else {
      formData.value = { ...defaultForm }
      isEdit.value = false
    }
  }

  function close() {
    visible.value = false
    formRef.value?.resetFields()
  }

  async function submit() {
    await formRef.value?.validate()
    loading.value = true
    try {
      await submitFn(formData.value)
      close()
      return true
    } finally {
      loading.value = false
    }
    return false
  }

  return {
    visible,
    formRef,
    formData,
    loading,
    isEdit,
    open,
    close,
    submit
  }
}
