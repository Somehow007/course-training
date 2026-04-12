import type { RouteRecordRaw } from 'vue-router'

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'college',
        name: 'College',
        component: () => import('@/views/college/index.vue'),
        meta: { title: '学院管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'major',
        name: 'Major',
        component: () => import('@/views/major/index.vue'),
        meta: { title: '专业管理', icon: 'Reading' }
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('@/views/course/index.vue'),
        meta: { title: '课程管理', icon: 'Collection' }
      },
      {
        path: 'training-program/list',
        name: 'TrainingProgramList',
        component: () => import('@/views/training-program/index.vue'),
        meta: { title: '培养计划管理', icon: 'Document' }
      },
      {
        path: 'training-program/:id',
        name: 'TrainingProgramDetail',
        component: () => import('@/views/training-program/detail.vue'),
        meta: { title: '培养计划详情', hidden: true }
      },
      {
        path: 'version',
        name: 'Version',
        component: () => import('@/views/version/index.vue'),
        meta: { title: '版本管理', icon: 'Clock' }
      },
      {
        path: 'version/compare',
        name: 'VersionCompare',
        component: () => import('@/views/version/compare.vue'),
        meta: { title: '版本对比', hidden: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', permission: '100' }
      },
      {
        path: 'sys-dict',
        name: 'SysDict',
        component: () => import('@/views/sys-dict/index.vue'),
        meta: { title: '系统字典', icon: 'Setting', permission: '100' }
      }
    ]
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '无权限' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]
