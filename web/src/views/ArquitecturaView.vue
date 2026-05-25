<script setup lang="ts">
import { Server, Monitor, ArrowRightLeft, FileJson, Route as RouteIcon, Plug, Workflow } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import MermaidDiagram from '@/components/content/MermaidDiagram.vue'
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from '@/components/ui/hover-card'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import {
  archPillars,
  rfRnfMapping,
  archFlowMermaid,
  archSequenceMermaid,
  scrambleDocs,
  vueRoutes,
  apiEndpoints,
} from '@/data/architecture'

useSectionAnimation()
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="4"
      titulo="Arquitectura del sistema"
      subtitulo="Monolito Docker: el juego Java envía puntajes a la API Laravel sobre MariaDB; Vue y Filament leen el ranking, y el juego se sirve en el navegador vía noVNC."
    />

    <!-- Pilares Backend / Frontend -->
    <div data-anim class="mb-10 grid gap-5 md:grid-cols-2">
      <Card v-for="(p, i) in archPillars" :key="p.titulo" class="border-border">
        <CardHeader class="flex flex-row items-center gap-3 space-y-0 pb-3">
          <span class="flex size-12 items-center justify-center rounded-xl bg-brand/15 text-brand">
            <component :is="i === 0 ? Server : Monitor" class="size-6" />
          </span>
          <CardTitle class="text-xl">{{ p.titulo }}</CardTitle>
        </CardHeader>
        <CardContent>
          <ul class="space-y-3">
            <li
              v-for="(b, j) in p.bullets"
              :key="j"
              class="flex gap-3 text-base leading-relaxed text-foreground"
            >
              <span
                aria-hidden="true"
                class="mt-2.5 inline-block size-1.5 shrink-0 rounded-full bg-brand"
              />
              <span>{{ b }}</span>
            </li>
          </ul>
        </CardContent>
      </Card>
    </div>

    <!-- Mapeo RF / RNF — grid de cards (sin tabla densa) -->
    <Card data-anim class="mb-10 border-border">
      <CardHeader class="flex flex-row items-center gap-3 space-y-0 pb-3">
        <span class="flex size-12 items-center justify-center rounded-xl bg-brand/15 text-brand">
          <ArrowRightLeft class="size-6" />
        </span>
        <CardTitle class="text-xl">De requerimientos a software</CardTitle>
      </CardHeader>
      <CardContent>
        <div class="grid gap-3 sm:grid-cols-2">
          <HoverCard v-for="(row, i) in rfRnfMapping" :key="i" :open-delay="120">
            <HoverCardTrigger as-child>
              <button
                type="button"
                class="flex w-full items-center gap-3 rounded-lg border border-border bg-card/60 p-4 text-left transition-colors hover:border-brand/60 hover:bg-brand/5 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                <Badge
                  :variant="row.tipo === 'RF' ? 'default' : 'secondary'"
                  class="shrink-0 font-mono text-sm"
                  :class="row.tipo === 'RF' ? 'bg-brand text-brand-foreground hover:bg-brand/90' : ''"
                >
                  {{ row.tipo }}
                </Badge>
                <span class="font-medium text-foreground">{{ row.requerimiento }}</span>
              </button>
            </HoverCardTrigger>
            <HoverCardContent class="w-96">
              <p class="mb-2 font-mono text-xs uppercase tracking-wider text-muted-foreground">
                Se materializa en
              </p>
              <code class="break-all font-mono text-sm text-brand">{{ row.ubicacion }}</code>
            </HoverCardContent>
          </HoverCard>
        </div>
      </CardContent>
    </Card>

    <!-- Flujo general (Mermaid — intocable) -->
    <Card data-anim class="mb-10 border-border">
      <CardHeader class="flex flex-row items-center gap-3 space-y-0 pb-3">
        <span class="flex size-12 items-center justify-center rounded-xl bg-brand/15 text-brand">
          <Workflow class="size-6" />
        </span>
        <CardTitle class="text-xl">Flujo general</CardTitle>
      </CardHeader>
      <CardContent>
        <MermaidDiagram
          :source="archFlowMermaid"
          id="arch-flow"
          aria-label="Diagrama de arquitectura del monolito Neon Trails"
        />
      </CardContent>
    </Card>

    <!-- Documentación Scramble — chips con HoverCard -->
    <Card data-anim class="mb-10 border-border">
      <CardHeader class="flex flex-row items-center gap-3 space-y-0 pb-3">
        <span class="flex size-12 items-center justify-center rounded-xl bg-brand/15 text-brand">
          <FileJson class="size-6" />
        </span>
        <CardTitle class="text-xl">Superficies del monolito (gateway nginx)</CardTitle>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-3">
          <HoverCard v-for="d in scrambleDocs" :key="d.ruta" :open-delay="120">
            <HoverCardTrigger as-child>
              <button
                type="button"
                class="inline-flex items-center gap-2 rounded-lg border border-border bg-card/60 px-3 py-2 transition-colors hover:border-brand hover:bg-brand/8 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                <code class="font-mono text-sm font-semibold text-brand">{{ d.ruta }}</code>
              </button>
            </HoverCardTrigger>
            <HoverCardContent class="w-80">
              <p class="text-sm text-foreground">{{ d.descripcion }}</p>
            </HoverCardContent>
          </HoverCard>
        </div>
      </CardContent>
    </Card>

    <!-- Rutas Vue — chips con HoverCard + Popover para lógica -->
    <Card data-anim class="mb-10 border-border">
      <CardHeader class="flex flex-row items-center justify-between gap-3 space-y-0 pb-3">
        <div class="flex items-center gap-3">
          <span class="flex size-12 items-center justify-center rounded-xl bg-brand/15 text-brand">
            <RouteIcon class="size-6" />
          </span>
          <CardTitle class="text-xl">Rutas Vue Router</CardTitle>
        </div>
        <Popover>
          <PopoverTrigger as-child>
            <button
              type="button"
              class="inline-flex items-center gap-1.5 rounded-md border border-border bg-background px-3 py-1.5 text-sm font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            >
              Navegación de la SPA
            </button>
          </PopoverTrigger>
          <PopoverContent class="w-96 text-sm" side="bottom" align="end">
            <p class="leading-relaxed text-foreground">
              <strong>SPA pública:</strong> la presentación se organiza en secciones con
              <code class="font-mono text-xs">createWebHistory</code> y scroll-snap. El
              <code class="font-mono text-xs">leaderboard</code> consume
              <code class="font-mono text-xs">GET /api/scores</code> en el mismo origen. Cualquier ruta
              desconocida (<code class="font-mono text-xs">:pathMatch(.*)*</code>) redirige a
              <code class="font-mono text-xs">/</code>.
            </p>
          </PopoverContent>
        </Popover>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-2">
          <HoverCard v-for="r in vueRoutes" :key="r.nombre" :open-delay="120">
            <HoverCardTrigger as-child>
              <button
                type="button"
                class="inline-flex items-center gap-2 rounded-lg border border-border bg-card/60 px-3 py-2 font-mono text-sm transition-colors hover:border-brand hover:bg-brand/8 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                <span class="text-brand">{{ r.ruta }}</span>
                <span class="text-muted-foreground">·</span>
                <span class="text-foreground">{{ r.nombre }}</span>
              </button>
            </HoverCardTrigger>
            <HoverCardContent class="w-80 text-sm">
              <p class="text-foreground">{{ r.meta }}</p>
            </HoverCardContent>
          </HoverCard>
        </div>
      </CardContent>
    </Card>

    <!-- Endpoints API — chips método + ruta con HoverCard -->
    <Card data-anim class="mb-10 border-border">
      <CardHeader class="flex flex-row items-center gap-3 space-y-0 pb-3">
        <span class="flex size-12 items-center justify-center rounded-xl bg-brand/15 text-brand">
          <Plug class="size-6" />
        </span>
        <CardTitle class="text-xl">Endpoints API relevantes</CardTitle>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-2">
          <HoverCard v-for="(e, i) in apiEndpoints" :key="i" :open-delay="120">
            <HoverCardTrigger as-child>
              <button
                type="button"
                class="inline-flex items-center gap-2 rounded-lg border border-border bg-card/60 px-3 py-2 transition-colors hover:border-brand hover:bg-brand/8 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                <Badge variant="outline" class="shrink-0 font-mono text-xs">
                  {{ e.metodo }}
                </Badge>
                <code class="font-mono text-sm text-brand">{{ e.ruta }}</code>
              </button>
            </HoverCardTrigger>
            <HoverCardContent class="w-80 text-sm">
              <p class="text-foreground">{{ e.descripcion }}</p>
            </HoverCardContent>
          </HoverCard>
        </div>
      </CardContent>
    </Card>

    <!-- Secuencia (Mermaid — intocable) -->
    <Card data-anim class="border-border">
      <CardHeader class="flex flex-row items-center gap-3 space-y-0 pb-3">
        <span class="flex size-12 items-center justify-center rounded-xl bg-brand/15 text-brand">
          <Workflow class="size-6" />
        </span>
        <CardTitle class="text-xl">Secuencia: partida → puntaje → ranking</CardTitle>
      </CardHeader>
      <CardContent>
        <MermaidDiagram
          :source="archSequenceMermaid"
          id="arch-sequence"
          aria-label="Diagrama de secuencia: partida, envío de puntaje y consulta del ranking"
        />
      </CardContent>
    </Card>
  </section>
</template>
