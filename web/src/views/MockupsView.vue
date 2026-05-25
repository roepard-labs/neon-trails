<script setup lang="ts">
import {
  Play,
  Gamepad2,
  Trophy,
  ListOrdered,
  type LucideIcon,
} from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import MockupFrame from '@/components/mockups/MockupFrame.vue'
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs'
import { Badge } from '@/components/ui/badge'
import { useSectionAnimation } from '@/composables/useSectionAnimation'

import WelcomeMock from '@/components/mockups/neontrails/WelcomeMock.vue'
import ArenaMock from '@/components/mockups/neontrails/ArenaMock.vue'
import GameOverMock from '@/components/mockups/neontrails/GameOverMock.vue'
import LeaderboardMock from '@/components/mockups/neontrails/LeaderboardMock.vue'

useSectionAnimation()

interface MockupItem {
  id: string
  titulo: string
  caption: string
  url: string
  device: string
  component: ReturnType<typeof Object>
}

interface Grupo {
  id: 'cu-01' | 'cu-02' | 'cu-03' | 'cu-04'
  nombre: string
  icon: LucideIcon
  descripcion: string
  frame: 'desktop' | 'mobile'
  cu: string
  tipo: 'Primario' | 'Secundario'
  mockups: MockupItem[]
}

const grupos: Grupo[] = [
  {
    id: 'cu-01',
    nombre: 'Bienvenida',
    icon: Play,
    frame: 'desktop',
    cu: 'CU-PART-01',
    tipo: 'Primario',
    descripcion:
      'Pantalla de inicio (WelcomeScreen): título neón, materia y docente, y los accesos a “Jugar” e “Instrucciones”. Desde aquí se inicia la partida local de dos jugadores.',
    mockups: [
      {
        id: 'welcome',
        titulo: 'Pantalla de bienvenida',
        caption: 'CU-PART-01 · view/screens/WelcomeScreen',
        url: 'neon-trails · ventana del juego',
        device: 'Swing · 960×600',
        component: WelcomeMock,
      },
    ],
  },
  {
    id: 'cu-02',
    nombre: 'Partida (HUD)',
    icon: Gamepad2,
    frame: 'desktop',
    cu: 'CU-MOV-02',
    tipo: 'Primario',
    descripcion:
      'Arena en juego: dos motos de luz dejan estela, un disco rebota y el HUD muestra puntaje, vidas y tiempo. Cubre conducir (CU-MOV-02), disparar (CU-DISC-03) y el modo moto (CU-MOTO-04).',
    mockups: [
      {
        id: 'arena',
        titulo: 'Arena y HUD',
        caption: 'CU-MOV-02 / CU-DISC-03 / CU-MOTO-04 · view/GamePanel',
        url: 'neon-trails · partida en curso',
        device: 'Swing · 960×600',
        component: ArenaMock,
      },
    ],
  },
  {
    id: 'cu-03',
    nombre: 'Game Over + Top 3',
    icon: Trophy,
    frame: 'desktop',
    cu: 'CU-SCORE-05',
    tipo: 'Secundario',
    descripcion:
      'Fin de partida (GameOverScreen): ganador, puntaje final, tiempo total y Top 3 del ranking. Aquí LeaderboardClient envía el puntaje al backend (CU-SCORE-05) y se ofrece reiniciar (CU-CICLO-08).',
    mockups: [
      {
        id: 'gameover',
        titulo: 'Pantalla de Game Over',
        caption: 'CU-SCORE-05 / CU-CICLO-08 · POST /api/scores',
        url: 'neon-trails · game over',
        device: 'Swing · 960×600',
        component: GameOverMock,
      },
    ],
  },
  {
    id: 'cu-04',
    nombre: 'Leaderboard web',
    icon: ListOrdered,
    frame: 'desktop',
    cu: 'CU-RANK-06',
    tipo: 'Secundario',
    descripcion:
      'Sección Leaderboard de la SPA Vue: consume GET /api/scores y muestra el ranking ordenado por puntaje. El mismo historial se administra desde el panel Filament en /admin.',
    mockups: [
      {
        id: 'leaderboard',
        titulo: 'Ranking en la web',
        caption: 'CU-RANK-06 · GET /api/scores · ruta /leaderboard',
        url: 'neon-trails.app/leaderboard',
        device: 'Desktop · 1440 px',
        component: LeaderboardMock,
      },
    ],
  },
]
</script>

<template>
  <section>
    <PageHeader
      :numero="7"
      titulo="Mockups"
      subtitulo="Cada mockup enlaza un caso de uso con una pantalla concreta: las del juego Swing (bienvenida, partida, game over) y la sección Leaderboard de la SPA Vue."
    />

    <Tabs default-value="cu-01" data-anim class="w-full">
      <TabsList
        class="grid h-auto w-full grid-cols-2 gap-1 bg-muted/50 p-1 sm:grid-cols-4"
      >
        <TabsTrigger
          v-for="g in grupos"
          :key="g.id"
          :value="g.id"
          class="flex h-auto flex-col items-center gap-1 py-2.5"
        >
          <component :is="g.icon" class="size-4 text-brand" />
          <span class="text-xs font-semibold">{{ g.nombre }}</span>
          <span class="font-mono text-[9px] uppercase tracking-wider text-muted-foreground">
            {{ g.cu }}
          </span>
        </TabsTrigger>
      </TabsList>

      <TabsContent
        v-for="g in grupos"
        :key="g.id"
        :value="g.id"
        class="mt-6 focus-visible:ring-0"
      >
        <!-- Descripción del grupo -->
        <div class="mb-6 flex items-start gap-4 rounded-xl border border-border bg-card p-4 sm:p-5">
          <span class="flex size-11 shrink-0 items-center justify-center rounded-lg bg-brand/15 text-brand">
            <component :is="g.icon" class="size-5" />
          </span>
          <div class="flex-1">
            <div class="flex flex-wrap items-center gap-2">
              <h3 class="font-heading text-lg font-bold">{{ g.nombre }}</h3>
              <Badge class="bg-brand text-brand-foreground hover:bg-brand/90 font-mono text-[10px]">
                {{ g.cu }}
              </Badge>
              <Badge variant="outline" class="font-mono text-[10px]">
                {{ g.tipo }}
              </Badge>
            </div>
            <p class="mt-2 text-sm leading-relaxed text-muted-foreground">
              {{ g.descripcion }}
            </p>
          </div>
        </div>

        <!-- Mockups -->
        <div class="flex flex-col gap-6 sm:gap-8">
          <MockupFrame
            v-for="m in g.mockups"
            :key="m.id"
            :frame="g.frame"
            :url="m.url"
            :device="m.device"
            :title="m.titulo"
            :caption="m.caption"
          >
            <component :is="m.component" />
          </MockupFrame>
        </div>
      </TabsContent>
    </Tabs>
  </section>
</template>
