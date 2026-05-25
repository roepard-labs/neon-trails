import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useColorMode } from '@vueuse/core'
import { useShortcuts } from 'vue-shortcut-manager'
import { sectionRoutes } from '@/router'
import { useSidebar } from './useSidebar'
import { useBreakpoint } from './useBreakpoint'

export const isShortcutsOpen = ref(false)
export const isCommandOpen = ref(false)

export function useAppShortcuts() {
  const router = useRouter()
  const mode = useColorMode({
    attribute: 'class',
    selector: 'html',
    modes: { dark: 'dark', light: '' },
  })
  const { toggle: toggleSidebar, toggleMobile } = useSidebar()
  const { isMobile } = useBreakpoint()

  const toggleSidebarOrMobile = () => {
    if (isMobile.value) toggleMobile()
    else toggleSidebar()
  }

  const goSection = (idx: number) => {
    const route = sectionRoutes[idx]
    if (route?.path) router.push(route.path)
  }

  const currentIndex = () =>
    sectionRoutes.findIndex((r) => r.name === router.currentRoute.value.name)

  const nextSection = () => {
    const i = currentIndex()
    if (i >= 0 && i < sectionRoutes.length - 1) goSection(i + 1)
  }
  const prevSection = () => {
    const i = currentIndex()
    if (i > 0) goSection(i - 1)
  }

  useShortcuts([
    // Navegación
    { key: 'arrowright', handler: nextSection, description: 'Siguiente sección', scope: 'Navegación' },
    { key: 'arrowleft', handler: prevSection, description: 'Sección anterior', scope: 'Navegación' },
    { key: 'h', handler: () => goSection(0), description: 'Ir a la portada', scope: 'Navegación' },

    // Saltos rápidos a sección
    { key: '1', handler: () => goSection(0), description: 'Sección 1 · Portada', scope: 'Saltos rápidos' },
    { key: '2', handler: () => goSection(1), description: 'Sección 2 · Problema', scope: 'Saltos rápidos' },
    { key: '3', handler: () => goSection(2), description: 'Sección 3 · Requerimientos', scope: 'Saltos rápidos' },
    { key: '4', handler: () => goSection(3), description: 'Sección 4 · Casos de uso', scope: 'Saltos rápidos' },
    { key: '5', handler: () => goSection(4), description: 'Sección 5 · Descripciones', scope: 'Saltos rápidos' },
    { key: '6', handler: () => goSection(5), description: 'Sección 6 · Actividad', scope: 'Saltos rápidos' },
    { key: '7', handler: () => goSection(6), description: 'Sección 7 · Mockups', scope: 'Saltos rápidos' },

    // Interfaz
    {
      key: 't',
      handler: () => {
        mode.value = mode.value === 'dark' ? 'light' : 'dark'
      },
      description: 'Alternar tema claro / oscuro',
      scope: 'Interfaz',
    },
    {
      key: 'b',
      handler: toggleSidebarOrMobile,
      description: 'Mostrar / ocultar barra lateral',
      scope: 'Interfaz',
    },
    {
      key: 'shift+slash',
      handler: () => {
        isShortcutsOpen.value = !isShortcutsOpen.value
      },
      description: 'Mostrar este diálogo de atajos',
      scope: 'Interfaz',
    },
    {
      key: 'escape',
      handler: () => {
        isShortcutsOpen.value = false
        isCommandOpen.value = false
      },
      description: 'Cerrar diálogos abiertos',
      scope: 'Interfaz',
    },
    {
      key: 'meta+k',
      handler: () => {
        isCommandOpen.value = !isCommandOpen.value
      },
      description: 'Abrir buscador rápido (command palette)',
      scope: 'Interfaz',
    },
    {
      key: 'ctrl+k',
      handler: () => {
        isCommandOpen.value = !isCommandOpen.value
      },
      description: 'Abrir buscador rápido (command palette)',
      scope: 'Interfaz',
    },
  ])
}
