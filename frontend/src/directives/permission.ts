import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    const userStore = useUserStore()
    const { value } = binding

    if (!value) return

    const permissions = Array.isArray(value) ? value : [value]
    const hasPermission = permissions.includes(userStore.identity)

    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

export function setupPermissionDirective(app: any) {
  app.directive('permission', permission)
}
