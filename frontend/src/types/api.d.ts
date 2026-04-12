// API 统一响应结构
export interface ApiResponse<T = any> {
  code: string
  message: string
  data: T
  requestId: string
}

// 分页响应结构
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 分页请求参数
export interface PageQuery {
  current?: number
  size?: number
}

// 用户相关类型
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  id: string
  username: string
  identity: string
}

export interface UserPageQuery extends PageQuery {
  username?: string
  collegeId?: string
  collegeName?: string
  dictId?: string
  dictName?: string
}

export interface UserRegistryRequest {
  username: string
  password: string
  collegeId?: string
  dictId: string
}

export interface UserPageItem {
  id: string
  username: string
  collegeName: string
  dictName: string
  delFlag: number
}

export interface ResetPasswordRequest {
  userId: string
  newPassword: string
}

// 学院相关类型
export interface CollegePageQuery extends PageQuery {
  collegeIds?: string[]
  collegeNames?: string[]
}

export interface CollegeSaveRequest {
  collegeCode: string
  collegeName: string
}

export interface CollegeUpdateRequest {
  collegeId: string
  collegeName: string
}

export interface CollegePageItem {
  id: string
  collegeName: string
  majors: MajorInfo[]
  courseNum: number
  majorNum: number
  delFlag: number
}

export interface MajorInfo {
  majorCode: string
  majorName: string
  courseNum: number
  category: string
}

// 专业相关类型
export interface MajorPageQuery extends PageQuery {
  majorIds?: string[]
  majorNames?: string[]
  collegeNames?: string[]
  categoryId?: number
}

export interface MajorSaveRequest {
  majorCode?: string
  collegeId: string
  majorName: string
  courseNum?: number
  category: number
}

export interface MajorUpdateRequest {
  majorId: string
  collegeId: string
  majorName: string
  category: number
}

export interface MajorPageItem {
  id: string
  majorCode: string
  collegeId: string
  collegeName: string
  majorName: string
  courseNum: number
  category: string
  delFlag: number
}

// 课程相关类型
export interface CoursePageQuery extends PageQuery {
  courseIds?: string[]
  dictIds?: string[]
  dictNames?: string[]
  collegeIds?: string[]
  collegeNames?: string[]
  courseNature?: number
  courseNames?: string[]
}

export interface CourseSaveRequest {
  courseCode?: string
  dictId: string
  collegeId: string
  courseNature: number
  courseName: string
}

export interface CourseUpdateRequest {
  id: string
  dictId: string
  collegeId: string
  courseNature: number
  courseName: string
}

export interface CoursePageItem {
  courseId: string
  courseType: string
  courseNature: number
  courseNatureDesc: string
  courseName: string
  collegeName: string
  delFlag: number
}

// 培养计划相关类型
export interface TrainingProgramPageQuery extends PageQuery {
  majorId?: string
  collegeId?: string
  year?: number
}

export interface TrainingProgramPageItem {
  id: string
  name: string
  majorId: string
  majorName: string
  collegeId: string
  collegeName: string
  year: number
  description: string
}

export interface TrainingProgramCreateRequest {
  majorId: string
  collegeId: string
  year: number
  description?: string
}

export interface TrainingProgramUpdateRequest {
  id: string
  name?: string
  year?: string
  description?: string
}

export interface TrainingProgramAddCourseRequest {
  trainingProgramId: string
  courseId: string
  collegeId?: string
  majorId?: string
  totalCredits: number
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
  version?: string
}

export interface TrainingProgramDetailItem {
  id: number
  name: string
  courseName: string
  courseNature: number
  courseType: string
  collegeName: string
  collegeId: number
  majorName: string
  majorId: number
  totalCredits: number
  totalHours: number
  totalWeeks: number | null
  hoursUnit: number
  hourTeach: number
  hourPractice: number
  hourOperation: number
  hourOutside: number
  hourWeek: number | null
  requiredElective: number | null
  term: number | null
  remark: string | null
  version: number
}

export interface TrainingProgramUpdateCourseRequest {
  id: string
  majorId?: string
  totalCredits: number
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
}

// 系统字典相关类型
export interface SysDictPageQuery extends PageQuery {
  dictType: string
  dictCode?: string
  dictName?: string
}

export interface SysDictCreateRequest {
  dictType: string
  dictCode?: string
  dictName: string
  sortOrder?: number
  remark?: string
}

export interface SysDictUpdateRequest {
  id: string
  dictType: string
  dictCode?: string
  dictName: string
  sortOrder?: number
  remark?: string
}

export interface SysDictPageItem {
  id: number
  dictType: string
  dictCode: string
  dictName: string
  sortOrder: number
  remark: string
}

// 下拉选项类型
export interface SelectOption {
  label: string
  value: string | number
}
