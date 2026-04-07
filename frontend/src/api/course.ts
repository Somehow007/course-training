import { request } from '@/utils/request'
import type {
  CoursePageQuery,
  CourseSaveRequest,
  CourseUpdateRequest,
  CoursePageItem
} from '@/types/api'
import type { PageResponse } from '@/types/api'

export const courseApi = {
  // 分页查询课程
  page(params: CoursePageQuery): Promise<PageResponse<CoursePageItem>> {
    return request.get('/api/course/mainAdmin/page', { params })
  },

  // 添加课程
  create(data: CourseSaveRequest): Promise<void> {
    return request.post('/api/course/mainAdmin/create', data)
  },

  // 修改课程
  update(data: CourseUpdateRequest): Promise<void> {
    return request.put('/api/course/mainAdmin/update', data)
  },

  // 删除课程（支持批量）
  delete(ids: string[]): Promise<void> {
    return request.delete('/api/course/mainAdmin/delete', { data: ids })
  }
}
