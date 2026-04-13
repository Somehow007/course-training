import { request } from '@/utils/request'
import { ElMessage } from 'element-plus'

export interface VersionListParams {
  current: number
  size: number
  trainingProgramId?: string
  versionStatus?: number
  versionName?: string
  creatorName?: string
}

export interface VersionCreateParams {
  trainingProgramId: string
  versionName: string
  changeDescription?: string
}

export interface VersionCompareParams {
  sourceVersionId: string
  targetVersionId: string
}

export interface VersionListResponse {
  id: string
  trainingProgramId: string
  trainingProgramName: string
  versionNumber: number
  versionName: string
  versionStatus: number
  versionStatusDesc: string
  changeDescription: string
  creatorId: string
  creatorName: string
  publishTime: string
  publishUserName: string
  createTime: string
}

export interface VersionDifference {
  changeType: string
  changeTarget: string
  courseName: string
  oldValue: any
  newValue: any
  description: string
  changedFields?: string[]
}

export interface VersionCompareResponse {
  sourceVersion: {
    id: string
    versionNumber: number
    versionName: string
    createTime: string
  }
  targetVersion: {
    id: string
    versionNumber: number
    versionName: string
    createTime: string
  }
  differences: VersionDifference[]
  statistics: {
    addCount: number
    updateCount: number
    deleteCount: number
    totalCount: number
  }
}

export interface VersionChangeLog {
  id: string
  versionId: string
  changeType: number
  changeTarget: string
  targetId: number
  oldValue: string | null
  newValue: string | null
  changeDescription: string
  createTime: string
}

export interface CourseChangeItem {
  id?: string
  courseId: string
  collegeId?: string
  majorId?: string
  totalCredits?: number
  totalHours?: number
  totalWeeks?: number
  hoursUnit?: number
  hourTeach?: number
  hourPractice?: number
  hourOperation?: number
  hourOutside?: number
  hourWeek?: number
  requiredElective?: number
  term?: number
  remark?: string
  courseNature?: number
  courseName?: string
}

export interface VersionSaveChangesParams {
  trainingProgramId: string
  versionName?: string
  changeDescription?: string
  addedCourses?: CourseChangeItem[]
  updatedCourses?: CourseChangeItem[]
  deletedCourseIds?: string[]
}

export const versionApi = {
  pageVersionHistory: (params: VersionListParams) => {
    return request.get('/api/version/page', { params })
  },

  createVersion: (params: VersionCreateParams) => {
    return request.post('/api/version/create', params)
  },

  publishVersion: (versionId: string) => {
    return request.post(`/api/version/publish/${versionId}`)
  },

  rollbackVersion: (versionId: string) => {
    return request.post(`/api/version/rollback/${versionId}`)
  },

  archiveVersion: (versionId: string) => {
    return request.post(`/api/version/archive/${versionId}`)
  },

  compareVersions: (params: VersionCompareParams) => {
    return request.post('/api/version/compare', params)
  },

  getVersionDetail: (versionId: string) => {
    return request.get(`/api/version/detail/${versionId}`)
  },

  getVersionSnapshotDetail: (versionId: string) => {
    return request.get(`/api/version/snapshot-detail/${versionId}`)
  },

  deleteVersion: (versionId: string) => {
    return request.delete(`/api/version/delete/${versionId}`)
  },

  listVersionsByProgramId: (trainingProgramId: string) => {
    return request.get(`/api/version/list-by-program/${trainingProgramId}`)
  },

  getVersionChangeLogs: (versionId: string) => {
    return request.get(`/api/version/change-logs/${versionId}`)
  },

  saveChangesAndCreateVersion: (params: VersionSaveChangesParams) => {
    return request.post('/api/version/save-changes', params)
  },

  exportVersionToExcel: (versionId: string, versionNumber: number, programName: string) => {
    const url = `/api/version/export/${versionId}`
    const filename = `${programName}_V${versionNumber}_专业设置及学时分配表.xlsx`
    return downloadFile(url, filename)
  }
}

async function downloadFile(url: string, filename: string) {
  try {
    const response = await fetch(import.meta.env.VITE_API_BASE_URL + url, {
      credentials: 'include'
    })
    if (!response.ok) {
      throw new Error('下载失败')
    }
    const blob = await response.blob()
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = filename
    link.click()
    URL.revokeObjectURL(link.href)
  } catch (error) {
    ElMessage.error('下载失败')
    throw error
  }
}
