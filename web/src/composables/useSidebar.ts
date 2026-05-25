import { ref } from 'vue'
import { useLocalStorage } from '@vueuse/core'

const isCollapsed = useLocalStorage('app-sidebar-collapsed', false)
const isMobileOpen = ref(false)

export function useSidebar() {
  return {
    isCollapsed,
    toggle: () => { isCollapsed.value = !isCollapsed.value },
    expand: () => { isCollapsed.value = false },
    collapse: () => { isCollapsed.value = true },
    isMobileOpen,
    toggleMobile: () => { isMobileOpen.value = !isMobileOpen.value },
    openMobile: () => { isMobileOpen.value = true },
    closeMobile: () => { isMobileOpen.value = false },
  }
}
