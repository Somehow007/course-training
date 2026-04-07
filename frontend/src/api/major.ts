import { request } from '@/utils/request'
import type {
  MajorPageQuery,
  MajorSaveRequest,
  MajorUpdateRequest,
  MajorPageItem
} from '@/types/api'
import type { PageResponse } from '@/types/api'

export const majorApi = {
  // 分页查询专业
  page(params: MajorPageQuery): Promise<PageResponse<MajorPageItem>> {
    return request.get('/api/major/page', { params })
  },

  // 添加专业
  create(data: MajorSaveRequest): Promise<void> {
    return request.post('/api/major/mainAdmin/add', data)
  },

  // 修改专业
  update(data: MajorUpdateRequest): Promise<void> {
    return request.put('/api/major/mainAdmin/update', data)
  },

  // 删除专业
  delete(id: string): Promise<void> {
    return request.delete(`/api/major/mainAdmin/delete/${id}`)
  }
}
