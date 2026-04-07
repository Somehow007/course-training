import { request } from '@/utils/request'
import type {
  CollegePageQuery,
  CollegeSaveRequest,
  CollegeUpdateRequest,
  CollegePageItem
} from '@/types/api'
import type { PageResponse } from '@/types/api'

export const collegeApi = {
  // 分页查询学院
  page(params: CollegePageQuery): Promise<PageResponse<CollegePageItem>> {
    return request.get('/college/page', { params })
  },

  // 添加学院
  create(data: CollegeSaveRequest): Promise<void> {
    return request.post('/college/mainAdmin/add', data)
  },

  // 修改学院
  update(data: CollegeUpdateRequest): Promise<void> {
    return request.put('/college/mainAdmin/update', data)
  },

  // 删除学院
  delete(id: string): Promise<void> {
    return request.delete(`/college/mainAdmin/delete/${id}`)
  }
}
