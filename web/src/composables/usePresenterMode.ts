import { useLocalStorage } from '@vueuse/core'
import { watchEffect } from 'vue'

const isPresenter = useLocalStorage('app-presenter-mode', false)

export function usePresenterMode() {
  watchEffect(() => {
    if (typeof document === 'undefined') return
    document.documentElement.classList.toggle('presenter', isPresenter.value)
  })
  return {
    isPresenter,
    toggle: () => {
      isPresenter.value = !isPresenter.value
    },
    enable: () => {
      isPresenter.value = true
    },
    disable: () => {
      isPresenter.value = false
    },
  }
}
