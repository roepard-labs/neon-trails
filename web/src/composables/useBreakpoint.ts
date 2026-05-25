import { computed, type ComputedRef } from 'vue'
import { breakpointsTailwind, useBreakpoints } from '@vueuse/core'

const breakpoints = useBreakpoints(breakpointsTailwind)

const isMobile = computed(() => breakpoints.smaller('md').value)
const isTablet = computed(
  () => breakpoints.greaterOrEqual('md').value && breakpoints.smaller('lg').value,
)
const isDesktop = computed(() => breakpoints.greaterOrEqual('lg').value)

export function useBreakpoint(): {
  isMobile: ComputedRef<boolean>
  isTablet: ComputedRef<boolean>
  isDesktop: ComputedRef<boolean>
  breakpoints: typeof breakpoints
} {
  return { isMobile, isTablet, isDesktop, breakpoints }
}
