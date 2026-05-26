import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

export interface SectionMeta {
  numero: number
  titulo: string
  subtitulo: string
  icon: string
}

declare module 'vue-router' {
  interface RouteMeta {
    seccion?: SectionMeta
  }
}

export const sectionRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'portada',
    component: () => import('@/views/PortadaView.vue'),
    meta: {
      seccion: {
        numero: 1,
        titulo: 'Portada',
        subtitulo: 'Información del proyecto',
        icon: 'BookOpenText',
      },
    },
  },
  {
    path: '/historia',
    name: 'historia',
    component: () => import('@/views/HistoriaView.vue'),
    meta: {
      seccion: {
        numero: 2,
        titulo: 'Historia & ADN',
        subtitulo: 'Por qué Tron, el reto de POO y cómo creció el proyecto',
        icon: 'Dna',
      },
    },
  },
  {
    path: '/problema',
    name: 'problema',
    component: () => import('@/views/ProblemaView.vue'),
    meta: {
      seccion: {
        numero: 3,
        titulo: 'Planteamiento del problema',
        subtitulo: 'Contexto, actores y necesidades',
        icon: 'FileQuestion',
      },
    },
  },
  {
    path: '/arquitectura',
    name: 'arquitectura',
    component: () => import('@/views/ArquitecturaView.vue'),
    meta: {
      seccion: {
        numero: 4,
        titulo: 'Arquitectura del sistema',
        subtitulo: 'Carpetas, paquetes/imports y el monolito (Java, Laravel, Vue, noVNC)',
        icon: 'Network',
      },
    },
  },
  {
    path: '/casos-de-uso',
    name: 'casos-de-uso',
    component: () => import('@/views/CasosUsoView.vue'),
    meta: {
      seccion: {
        numero: 5,
        titulo: 'Diagrama de casos de uso',
        subtitulo: 'Actores, casos y relaciones UML',
        icon: 'Workflow',
      },
    },
  },
  {
    path: '/mockups',
    name: 'mockups',
    component: () => import('@/views/MockupsView.vue'),
    meta: {
      seccion: {
        numero: 6,
        titulo: 'Mockups',
        subtitulo: 'Pantallas del juego y superficies web',
        icon: 'MonitorSmartphone',
      },
    },
  },
  {
    path: '/stack',
    name: 'stack',
    component: () => import('@/views/StackView.vue'),
    meta: {
      seccion: {
        numero: 7,
        titulo: 'Stack & Código',
        subtitulo: 'Las 4 capas con código real (Java, PHP, Vue/TS, SQL)',
        icon: 'Code2',
      },
    },
  },
  {
    path: '/librerias',
    name: 'librerias',
    component: () => import('@/views/LibreriasView.vue'),
    meta: {
      seccion: {
        numero: 8,
        titulo: 'Librerías',
        subtitulo: 'Qué son las dependencias (Maven) y para qué sirven',
        icon: 'Library',
      },
    },
  },
  {
    path: '/pruebas',
    name: 'pruebas',
    component: () => import('@/views/PruebasView.vue'),
    meta: {
      seccion: {
        numero: 9,
        titulo: 'Pruebas unitarias',
        subtitulo: 'Qué son los tests (JUnit) y para qué sirven',
        icon: 'FlaskConical',
      },
    },
  },
  {
    path: '/despliegue',
    name: 'despliegue',
    component: () => import('@/views/DespliegueView.vue'),
    meta: {
      seccion: {
        numero: 10,
        titulo: 'Despliegue',
        subtitulo: 'Imagen Docker monolítica, noVNC y Dokploy',
        icon: 'Rocket',
      },
    },
  },
  {
    path: '/herramientas',
    name: 'herramientas',
    component: () => import('@/views/HerramientasView.vue'),
    meta: {
      seccion: {
        numero: 11,
        titulo: 'Herramientas',
        subtitulo: 'mise, make y la integración continua (CI)',
        icon: 'Wrench',
      },
    },
  },
  {
    path: '/documentacion',
    name: 'documentacion',
    component: () => import('@/views/DocumentacionView.vue'),
    meta: {
      seccion: {
        numero: 12,
        titulo: 'Documentación & GitHub',
        subtitulo: 'Dónde vive el código y cómo se documenta',
        icon: 'Github',
      },
    },
  },
  {
    path: '/leaderboard',
    name: 'leaderboard',
    component: () => import('@/views/LeaderboardView.vue'),
    meta: {
      seccion: {
        numero: 13,
        titulo: 'Leaderboard',
        subtitulo: 'Ranking en vivo (GET /api/scores) y jugar',
        icon: 'Trophy',
      },
    },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...sectionRoutes,
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
  scrollBehavior(to) {
    if (to.hash) return { el: to.hash, behavior: 'smooth' }
    return { top: 0, behavior: 'smooth' }
  },
})

export default router
