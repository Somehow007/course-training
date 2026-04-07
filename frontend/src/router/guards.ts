import type { Router } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'

let sessionChecked = false

export function setupRouterGuards(router: Router) {
  router.beforeEach(async (to, _from, next) => {
    document.title = to.meta.title
      ? `${to.meta.title} - 培养计划管理系统`
      : '培养计划管理系统'

    const userStore = useUserStore()

    if (to.meta.requiresAuth === false) {
      if (to.path === '/login' && userStore.isLoggedIn) {
        next({ path: '/' })
        return
      }
      next()
      return
    }

    if (!sessionChecked && userStore.isLoggedIn) {
      try {
        const sessionData = await userApi.checkSession()
        if (sessionData) {
          userStore.setUserInfo({
            id: sessionData.id,
            username: sessionData.username,
            identity: sessionData.identity
          })
        } else {
          userStore.logout()
        }
      } catch (error) {
        userStore.logout()
      }
      sessionChecked = true
    }

    if (!userStore.isLoggedIn) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }

    const requiredPermission = to.meta.permission as string | undefined
    if (requiredPermission && userStore.identity !== requiredPermission) {
      next({ path: '/403' })
      return
    }

    next()
  })
}
