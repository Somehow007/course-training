import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'
import type { LoginRequest } from '@/types/api'

const EXPIRATION_TIME = 30 * 60 * 1000

export const useUserStore = defineStore('user', () => {
  const id = ref<string>('')
  const username = ref<string>('')
  const identity = ref<string>('')
  const collegeId = ref<string>('')
  const loginTime = ref<number>(0)

  function checkExpiration(): boolean {
    if (!loginTime.value) {
      return false
    }
    
    const currentTime = Date.now()
    const elapsedTime = currentTime - loginTime.value
    
    if (elapsedTime > EXPIRATION_TIME) {
      logout()
      return false
    }
    
    return true
  }

  const isLoggedIn = computed(() => {
    if (!id.value) {
      return false
    }
    return checkExpiration()
  })
  
  const isAdmin = computed(() => identity.value === '100')

  async function login(credentials: LoginRequest) {
    const res = await userApi.login(credentials)
    id.value = res.id
    username.value = res.username
    identity.value = res.identity
    loginTime.value = Date.now()
    return res
  }

  function logout() {
    id.value = ''
    username.value = ''
    identity.value = ''
    collegeId.value = ''
    loginTime.value = 0
  }

  function setUserInfo(info: { id: string; username: string; identity: string }) {
    id.value = info.id
    username.value = info.username
    identity.value = info.identity
  }

  return {
    id,
    username,
    identity,
    collegeId,
    loginTime,
    isLoggedIn,
    isAdmin,
    login,
    logout,
    setUserInfo,
    checkExpiration
  }
}, {
  persist: true
})
