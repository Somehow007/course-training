import { ref, reactive } from 'vue'

export function usePagination<T>(
  fetchFn: (params: any) => Promise<{ records: T[]; total: number }>,
  defaultSize = 10
) {
  const loading = ref(false)
  const tableData = ref<T[]>([])

  const pagination = reactive({
    current: 1,
    size: defaultSize,
    total: 0
  })

  const searchParams = ref<Record<string, any>>({})

  async function loadData() {
    loading.value = true
    try {
      const res = await fetchFn({
        current: pagination.current,
        size: pagination.size,
        ...searchParams.value
      })
      tableData.value = res.records
      pagination.total = res.total
    } finally {
      loading.value = false
    }
  }

  function handleCurrentChange(val: number) {
    pagination.current = val
    loadData()
  }

  function handleSizeChange(val: number) {
    pagination.size = val
    pagination.current = 1
    loadData()
  }

  function search(params: Record<string, any> = {}) {
    searchParams.value = params
    pagination.current = 1
    loadData()
  }

  function refresh() {
    loadData()
  }

  return {
    loading,
    tableData,
    pagination,
    loadData,
    handleCurrentChange,
    handleSizeChange,
    search,
    refresh
  }
}
