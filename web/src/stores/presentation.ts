import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { sectionRoutes } from '@/router'

/**
 * Estado transversal de la presentación: en qué sección estamos, cuántas hay y
 * cuáles se han visitado. Lo alimenta un `router.afterEach` (ver `main.ts`) y lo
 * consumen el breadcrumb del header y la barra de progreso del AppShell, sin que
 * cada componente tenga que recalcular el índice de la ruta por su cuenta.
 */
export const usePresentationStore = defineStore('presentation', () => {
  /** Total de secciones navegables (crece automáticamente al añadir rutas). */
  const total = computed(() => sectionRoutes.length)

  /** `name` de la ruta activa (lo fija el guard de navegación). */
  const currentName = ref<string>('')

  /** Conjunto de secciones ya vistas en esta sesión. */
  const visited = ref<Set<string>>(new Set())

  /** Índice 0-based de la sección activa dentro de `sectionRoutes`. */
  const index = computed(() =>
    sectionRoutes.findIndex((r) => r.name === currentName.value),
  )

  /** Número humano 1-based (cae a 1 si la ruta no es una sección). */
  const numero = computed(() => (index.value >= 0 ? index.value + 1 : 1))

  /** Avance 0..100 para la barra de progreso. */
  const progress = computed(() =>
    total.value > 0 ? Math.round((numero.value / total.value) * 100) : 0,
  )

  const visitedCount = computed(() => visited.value.size)
  const allVisited = computed(() => visitedCount.value >= total.value)

  /** Registra la sección activa y la marca como visitada. */
  function setCurrent(name: string) {
    currentName.value = name
    if (sectionRoutes.some((r) => r.name === name)) {
      visited.value.add(name)
    }
  }

  function isVisited(name: string) {
    return visited.value.has(name)
  }

  return {
    total,
    currentName,
    visited,
    index,
    numero,
    progress,
    visitedCount,
    allVisited,
    setCurrent,
    isVisited,
  }
})
