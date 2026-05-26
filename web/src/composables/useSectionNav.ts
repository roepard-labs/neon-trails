import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { sectionRoutes } from '@/router'

/**
 * Navegación entre secciones derivada del ORDEN de `sectionRoutes` (única fuente
 * de verdad). La consumen los botones flotantes (SlideNavButtons) y los atajos de
 * teclado (useAppShortcuts), para no duplicar la lógica de "siguiente/anterior".
 */
export function useSectionNav() {
  const route = useRoute()
  const router = useRouter()

  const total = sectionRoutes.length
  const index = computed(() => sectionRoutes.findIndex((r) => r.name === route.name))
  const hasPrev = computed(() => index.value > 0)
  const hasNext = computed(() => index.value >= 0 && index.value < total - 1)

  function go(idx: number) {
    const target = sectionRoutes[idx]
    if (target?.path) router.push(target.path)
  }
  function prev() {
    if (hasPrev.value) go(index.value - 1)
  }
  function next() {
    if (hasNext.value) go(index.value + 1)
  }

  return { index, total, hasPrev, hasNext, go, prev, next }
}
