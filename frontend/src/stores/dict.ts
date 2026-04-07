import { defineStore } from 'pinia'
import { ref } from 'vue'
import { sysDictApi } from '@/api/sys-dict'
import type { SysDictPageItem } from '@/types/api'

export const useDictStore = defineStore('dict', () => {
  // 按类型缓存字典数据
  const dictCache = ref<Record<string, SysDictPageItem[]>>({})

  // 获取字典数据（带缓存）
  async function getDictByType(dictType: string): Promise<SysDictPageItem[]> {
    if (dictCache.value[dictType]) {
      return dictCache.value[dictType]
    }
    const res = await sysDictApi.getByType(dictType)
    dictCache.value[dictType] = res
    return res
  }

  // 清除缓存
  function clearCache() {
    dictCache.value = {}
  }

  return {
    dictCache,
    getDictByType,
    clearCache
  }
})
