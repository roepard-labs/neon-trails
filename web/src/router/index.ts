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
    path: '/problema',
    name: 'problema',
    component: () => import('@/views/ProblemaView.vue'),
    meta: {
      seccion: {
        numero: 2,
        titulo: 'Planteamiento del problema',
        subtitulo: 'Contexto, actores y necesidades',
        icon: 'FileQuestion',
      },
    },
  },
  {
    path: '/requerimientos',
    name: 'requerimientos',
    component: () => import('@/views/RequerimientosView.vue'),
    meta: {
      seccion: {
        numero: 3,
        titulo: 'Requerimientos',
        subtitulo: 'Negocio, usuario, sistema y F/NF',
        icon: 'ListChecks',
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
        subtitulo: 'Juego Java, API Laravel, MariaDB, Filament y noVNC',
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
    path: '/descripciones',
    name: 'descripciones',
    component: () => import('@/views/DescripcionesView.vue'),
    meta: {
      seccion: {
        numero: 6,
        titulo: 'Descripciones de casos de uso',
        subtitulo: '8 fichas formales (4 principales + 4 secundarios)',
        icon: 'NotebookText',
      },
    },
  },
  {
    path: '/mockups',
    name: 'mockups',
    component: () => import('@/views/MockupsView.vue'),
    meta: {
      seccion: {
        numero: 7,
        titulo: 'Mockups',
        subtitulo: 'Pantallas del juego y superficies web',
        icon: 'MonitorSmartphone',
      },
    },
  },
  {
    path: '/leaderboard',
    name: 'leaderboard',
    component: () => import('@/views/LeaderboardView.vue'),
    meta: {
      seccion: {
        numero: 8,
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
