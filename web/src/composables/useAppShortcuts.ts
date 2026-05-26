import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useShortcuts } from 'vue-shortcut-manager'
import { sectionRoutes } from '@/router'
import { useSidebar } from './useSidebar'
import { useBreakpoint } from './useBreakpoint'

export const isShortcutsOpen = ref(false)
export const isCommandOpen = ref(false)

export function useAppShortcuts() {
  const router = useRouter()
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

  // Abre el juego (servido por el monolito en /game/ vía noVNC) en otra pestaña,
  // sin perder la presentación. En dev local la ruta no existe y dará 404.
  const openGame = () => window.open('/game/', '_blank', 'noopener')

  // Saltos 1..9 generados desde las rutas: las etiquetas y el número siempre
  // coinciden con sectionRoutes y escalan al añadir secciones (las 10/11 quedan
  // accesibles por flechas, sidebar y command palette).
  const jumpShortcuts = sectionRoutes.slice(0, 9).map((route, i) => ({
    key: String(i + 1),
    handler: () => goSection(i),
    description: `Sección ${i + 1} · ${route.meta?.seccion?.titulo ?? String(route.name ?? '')}`,
    scope: 'Saltos rápidos',
  }))

  useShortcuts([
    // Navegación (J/K = alias estilo Vim de ←/→)
    { key: 'arrowright', handler: nextSection, description: 'Siguiente sección', scope: 'Navegación' },
    { key: 'arrowleft', handler: prevSection, description: 'Sección anterior', scope: 'Navegación' },
    { key: 'k', handler: nextSection, description: 'Siguiente sección', scope: 'Navegación' },
    { key: 'j', handler: prevSection, description: 'Sección anterior', scope: 'Navegación' },
    { key: 'h', handler: () => goSection(0), description: 'Ir a la portada', scope: 'Navegación' },
    { key: 'g', handler: openGame, description: 'Abrir el juego (/game/)', scope: 'Navegación' },

    // Saltos rápidos a sección (1..9)
    ...jumpShortcuts,

    // Interfaz
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
