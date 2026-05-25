import { onBeforeUnmount, ref, type Ref } from 'vue'
import { gsap } from '@/lib/gsap'

type GsapContext = ReturnType<typeof gsap.context>

export function useGsapContext(scope?: Ref<HTMLElement | null>) {
  const ctx = ref<GsapContext | null>(null)

  const create = (fn: () => void) => {
    ctx.value?.revert()
    ctx.value = gsap.context(fn, scope?.value ?? undefined)
  }

  const revert = () => {
    ctx.value?.revert()
    ctx.value = null
  }

  onBeforeUnmount(revert)

  return { create, revert, ctx }
}
