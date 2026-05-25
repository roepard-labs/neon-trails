<script setup lang="ts">
import { ArrowRight, Link2, Maximize2 } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import MermaidDiagram from '@/components/content/MermaidDiagram.vue'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from '@/components/ui/hover-card'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import { useCaseDiagramMermaid, useCaseDiagramResumen } from '@/data/useCaseDiagram'
import { useCases } from '@/data/useCases'

useSectionAnimation()
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="5"
      titulo="Diagrama de casos de uso"
      subtitulo="Modelado UML: Jugador, Administrador y Servicio de leaderboard frente al sistema, con relaciones «include», «extend» y eventos."
    />

    <!-- Diagrama Mermaid (intocable) -->
    <div data-anim class="mb-10">
      <div class="mb-3 flex items-center justify-between">
        <p class="font-mono text-xs uppercase tracking-[0.18em] text-muted-foreground">
          Diagrama UML
        </p>
        <span class="inline-flex items-center gap-1.5 text-xs text-muted-foreground">
          <Maximize2 class="size-3.5" />
          Desplazar para ver completo
        </span>
      </div>
      <div class="relative -mx-4 sm:mx-0">
        <div
          class="pointer-events-none absolute inset-x-0 -top-px h-px animate-shimmer-brand"
          aria-hidden="true"
        />
        <MermaidDiagram
          :source="useCaseDiagramMermaid"
          id="cu-diagram"
          aria-label="Diagrama de casos de uso del sistema Neon Trails"
        />
      </div>
    </div>

    <!-- Actores y sus casos — cards (sin tabla) -->
    <div data-anim class="mb-12">
      <h2 class="font-heading text-2xl font-bold tracking-tight sm:text-3xl">
        Actores y sus casos de uso
      </h2>
      <p class="mt-2 text-base text-muted-foreground">
        Distribución de los ocho casos de uso entre los actores del sistema.
      </p>

      <div class="mt-5 grid gap-5 md:grid-cols-2">
        <Card
          v-for="r in useCaseDiagramResumen.actores"
          :key="r.actor"
          class="border-border"
        >
          <CardHeader class="pb-3">
            <CardTitle class="text-xl">{{ r.actor }}</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="flex flex-wrap gap-2">
              <Badge
                v-for="c in r.casos"
                :key="c"
                variant="secondary"
                class="font-mono text-sm"
              >
                {{ c }}
              </Badge>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>

    <!-- Casos de uso identificados -->
    <div data-anim class="mb-12">
      <h2 class="font-heading text-2xl font-bold tracking-tight sm:text-3xl">
        Casos de uso identificados
      </h2>
      <p class="mt-2 text-base text-muted-foreground">
        Cuatro principales (jugar la partida) y cuatro secundarios (leaderboard, administración y ciclo de juego).
      </p>

      <div class="mt-5 grid gap-5 md:grid-cols-2">
        <Card v-for="uc in useCases" :key="uc.codigo" class="border-border">
          <CardHeader class="pb-3">
            <div class="flex items-center justify-between">
              <Badge
                :variant="uc.tipo === 'principal' ? 'default' : 'secondary'"
                class="font-mono text-sm"
                :class="uc.tipo === 'principal' ? 'bg-brand text-brand-foreground hover:bg-brand/90' : ''"
              >
                {{ uc.codigo }}
              </Badge>
              <span class="font-mono text-xs uppercase tracking-wider text-muted-foreground">
                {{ uc.tipo }}
              </span>
            </div>
            <CardTitle class="mt-3 text-lg">{{ uc.nombre }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-3">
            <p class="text-base leading-relaxed text-muted-foreground">{{ uc.descripcion }}</p>
            <div class="flex flex-wrap items-center gap-2">
              <span class="text-sm text-muted-foreground">Actor:</span>
              <Badge
                v-for="actorId in uc.actores"
                :key="actorId"
                variant="outline"
                class="text-sm capitalize"
              >
                {{ actorId }}
              </Badge>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>

    <!-- Relaciones — grid de cards desde → hacia con HoverCard para descripción -->
    <div data-anim>
      <h2 class="font-heading text-2xl font-bold tracking-tight sm:text-3xl">
        Relaciones del diagrama
      </h2>
      <p class="mt-2 text-base text-muted-foreground">
        «include»: el caso reutilizado siempre se ejecuta. «evento»: broadcast realtime que refresca la vista.
      </p>

      <div class="mt-5 grid gap-3 sm:grid-cols-2">
        <HoverCard
          v-for="(rel, i) in useCaseDiagramResumen.relaciones"
          :key="i"
          :open-delay="120"
        >
          <HoverCardTrigger as-child>
            <button
              type="button"
              class="flex w-full items-center gap-3 rounded-lg border border-border bg-card/60 p-4 text-left transition-colors hover:border-brand/60 hover:bg-brand/5 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            >
              <Badge variant="outline" class="shrink-0 font-mono text-sm">
                <Link2 class="mr-1 size-3.5 text-brand" />
                {{ rel.tipo }}
              </Badge>
              <span class="font-medium text-foreground">{{ rel.desde }}</span>
              <ArrowRight class="size-4 shrink-0 text-muted-foreground" />
              <span class="font-medium text-foreground">{{ rel.hacia }}</span>
            </button>
          </HoverCardTrigger>
          <HoverCardContent class="w-80 text-sm">
            <p class="text-foreground">{{ rel.descripcion }}</p>
          </HoverCardContent>
        </HoverCard>
      </div>
    </div>
  </section>
</template>
