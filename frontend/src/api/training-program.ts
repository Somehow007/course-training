import { request } from '@/utils/request'
import type {
  TrainingProgramPageQuery,
  TrainingProgramPageItem,
  TrainingProgramCreateRequest,
  TrainingProgramUpdateRequest,
  TrainingProgramAddCourseRequest,
  TrainingProgramUpdateCourseRequest,
  TrainingProgramDetailItem
} from '@/types/api'
import type { PageResponse } from '@/types/api'

export const trainingProgramApi = {
  // 分页查询培养计划
  page(params: TrainingProgramPageQuery): Promise<PageResponse<TrainingProgramPageItem>> {
    return request.get('/api/training-program/page', { params })
  },

  // 创建培养计划
  create(data: TrainingProgramCreateRequest): Promise<void> {
    return request.post('/api/training-program/mainAdmin/create', data)
  },

  // 修改培养计划基本信息
  update(data: TrainingProgramUpdateRequest): Promise<void> {
    return request.post('/api/training-program/mainAdmin/update', data)
  },

  // 删除培养计划
  delete(id: string): Promise<void> {
    return request.delete(`/api/training-program/mainAdmin/delete/${id}`)
  },

  // 为培养计划添加课程
  addCourse(data: TrainingProgramAddCourseRequest): Promise<void> {
    return request.post('/api/training-program/mainAdmin/add', data)
  },

  // 更新培养计划课程信息
  updateCourse(data: TrainingProgramUpdateCourseRequest): Promise<void> {
    return request.post('/api/training-program/mainAdmin/updateCourse', data)
  },

  // 删除培养计划中的单个课程
  deleteCourse(id: string): Promise<void> {
    return request.delete(`/api/training-program/mainAdmin/deleteCourse/${id}`)
  },

  // 查询培养计划详情
  getDetail(trainingProgramId: string): Promise<TrainingProgramDetailItem[]> {
    return request.get(`/api/training-program/detail/select/${trainingProgramId}`)
  },

  // 导入培养计划
  importExcel(collegeId: string, majorId: string, file: File): Promise<void> {
    const formData = new FormData()
    formData.append('collegeId', collegeId)
    formData.append('majorId', majorId)
    formData.append('file', file)
    return request.post('/api/training-program/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
