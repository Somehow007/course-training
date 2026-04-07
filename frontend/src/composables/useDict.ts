import { ref, onMounted } from 'vue'
import { useDictStore } from '@/stores/dict'
import type { SelectOption } from '@/types/api'

export function useDict(dictType: string) {
  const dictStore = useDictStore()
  const options = ref<SelectOption[]>([])
  const loading = ref(false)

  onMounted(async () => {
    loading.value = true
    try {
      const data = await dictStore.getDictByType(dictType)
      options.value = data.map((item) => ({
        label: item.dictName,
        value: item.id
      }))
    } finally {
      loading.value = false
    }
  })

  function getLabelByValue(value: string | number): string {
    const option = options.value.find((opt) => opt.value === value)
    return option?.label || ''
  }

  return {
    options,
    loading,
    getLabelByValue
  }
}
