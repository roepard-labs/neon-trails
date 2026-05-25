import { onMounted, onBeforeUnmount, type Ref } from 'vue'
import { gsap, ScrollTrigger } from '@/lib/gsap'
import { usePrefersReducedMotion } from './usePrefersReducedMotion'

interface ScrollRevealOptions {
  y?: number
  duration?: number
  stagger?: number
  delay?: number
  ease?: string
  start?: string
  once?: boolean
}

export function useScrollReveal(
  target: Ref<HTMLElement | null> | string,
  options: ScrollRevealOptions = {},
) {
  const {
    y = 24,
    duration = 0.7,
    stagger = 0.08,
    delay = 0,
    ease = 'power2.out',
    start = 'top 85%',
    once = true,
  } = options

  const prefersReducedMotion = usePrefersReducedMotion()
  let triggers: ScrollTrigger[] = []

  onMounted(() => {
    const elements =
      typeof target === 'string'
        ? Array.from(document.querySelectorAll(target))
        : target.value
        ? [target.value]
        : []

    if (!elements.length) return

    if (prefersReducedMotion.value) {
      gsap.set(elements, { opacity: 1, y: 0, clearProps: 'all' })
      return
    }

    const tween = gsap.from(elements, {
      opacity: 0,
      y,
      duration,
      delay,
      stagger,
      ease,
      scrollTrigger: {
        trigger: elements[0] as Element,
        start,
        toggleActions: once ? 'play none none none' : 'play none none reverse',
      },
    })

    if (tween.scrollTrigger) triggers.push(tween.scrollTrigger)
  })

  onBeforeUnmount(() => {
    triggers.forEach((t) => t.kill())
    triggers = []
  })
}
