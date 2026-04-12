import request from '@/utils/request'

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

  deleteVersion: (versionId: string) => {
    return request.delete(`/api/version/delete/${versionId}`)
  },

  listVersionsByProgramId: (trainingProgramId: string) => {
    return request.get(`/api/version/list-by-program/${trainingProgramId}`)
  },

  getVersionChangeLogs: (versionId: string) => {
    return request.get(`/api/version/change-logs/${versionId}`)
  }
}
