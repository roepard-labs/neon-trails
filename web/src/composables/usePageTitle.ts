import { computed, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import { useTitle } from '@vueuse/core'
import { sectionRoutes } from '@/router'

export function usePageTitle() {
  const route = useRoute()
  const title = useTitle()

  const seccion = computed(() => {
    const r = sectionRoutes.find((sr) => sr.name === route.name)
    return r?.meta?.seccion ?? null
  })

  watchEffect(() => {
    const base = 'Logística — Proyecto Final TS'
    title.value = seccion.value ? `${seccion.value.titulo} · ${base}` : base
  })
}
