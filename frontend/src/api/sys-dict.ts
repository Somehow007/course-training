import { request } from '@/utils/request'
import type {
  SysDictPageQuery,
  SysDictCreateRequest,
  SysDictPageItem,
  SysDictUpdateRequest
} from '@/types/api'
import type { PageResponse } from '@/types/api'

export const sysDictApi = {
  page(params: SysDictPageQuery): Promise<PageResponse<SysDictPageItem>> {
    return request.get('/api/sys-dict/page', { params })
  },

  create(data: SysDictCreateRequest): Promise<void> {
    return request.post('/api/sys-dict/mainAdmin/create', data)
  },

  update(data: SysDictUpdateRequest): Promise<void> {
    return request.put('/api/sys-dict/mainAdmin/update', data)
  },

  delete(id: string): Promise<void> {
    return request.delete(`/api/sys-dict/mainAdmin/delete/${id}`)
  },

  async getByType(dictType: string): Promise<SysDictPageItem[]> {
    const res = await this.page({ dictType, size: 100 })
    return res.records
  },

  getDictTypes(): Promise<string[]> {
    return request.get('/api/sys-dict/types')
  }
}
