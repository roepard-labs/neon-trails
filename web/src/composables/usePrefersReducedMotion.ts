import { computed, type ComputedRef } from 'vue'
import { usePreferredReducedMotion } from '@vueuse/core'

export function usePrefersReducedMotion(): ComputedRef<boolean> {
  const motion = usePreferredReducedMotion()
  return computed(() => motion.value === 'reduce')
}
