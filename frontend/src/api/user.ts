import { request } from '@/utils/request'
import type {
  LoginRequest,
  LoginResponse,
  UserPageQuery,
  UserRegistryRequest,
  UserPageItem
} from '@/types/api'
import type { PageResponse } from '@/types/api'

export const userApi = {
  // 登录
  login(data: LoginRequest): Promise<LoginResponse> {
    return request.post('/api/login', data)
  },

  // 检查会话状态
  checkSession(): Promise<LoginResponse | null> {
    return request.get('/api/check-session')
  },

  // 注册用户
  registry(data: UserRegistryRequest): Promise<void> {
    return request.post('/api/user/mainAdmin/registry', data)
  },

  // 分页查询用户
  pageQuery(params: UserPageQuery): Promise<PageResponse<UserPageItem>> {
    return request.get('/api/user/mainAdmin/page', { params })
  },

  // 删除用户
  delete(id: string): Promise<void> {
    return request.delete(`/api/user/mainAdmin/delete/${id}`)
  }
}
