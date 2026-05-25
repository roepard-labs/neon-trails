<script setup lang="ts">
import { AlertTriangle, ClipboardList, Target } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ActorCard from '@/components/content/ActorCard.vue'
import { Card } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from '@/components/ui/hover-card'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import { actors } from '@/data/actors'
import { problemaContexto } from '@/data/project'

useSectionAnimation()

const tiles = [
  {
    key: 'actualmente',
    title: 'Actualmente',
    icon: ClipboardList,
    tone: 'muted' as const,
    items: problemaContexto.actualmente,
  },
  {
    key: 'problemas',
    title: 'Problemas',
    icon: AlertTriangle,
    tone: 'destructive' as const,
    items: problemaContexto.problemas,
  },
  {
    key: 'seRequiere',
    title: 'Se requiere',
    icon: Target,
    tone: 'brand' as const,
    items: problemaContexto.seRequiere,
  },
] as const

const toneClass = (tone: 'muted' | 'destructive' | 'brand') => {
  if (tone === 'destructive')
    return {
      card: 'border-destructive/40 bg-destructive/[0.04]',
      icon: 'bg-destructive/12 text-destructive',
      bullet: 'bg-destructive',
    }
  if (tone === 'brand')
    return {
      card: 'border-brand/40 bg-brand/[0.05]',
      icon: 'bg-brand/15 text-brand',
      bullet: 'bg-brand',
    }
  return {
    card: 'border-border',
    icon: 'bg-muted text-muted-foreground',
    bullet: 'bg-muted-foreground',
  }
}
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="2"
      titulo="Planteamiento del problema"
      subtitulo="Un juego de arena en tiempo real tiende a acoplar simulación, render y entrada. Neon Trails separa logic / view / events y persiste el ranking en un backend web."
    />

    <!-- Actores -->
    <div class="mt-2">
      <h2 data-anim class="font-heading text-2xl font-bold tracking-tight sm:text-3xl">
        Actores del sistema
      </h2>
      <p data-anim class="mt-2 max-w-3xl text-base text-muted-foreground">
        Dos roles humanos (jugador y administrador) y un actor «sistema»: el servicio de leaderboard.
      </p>

      <div class="mt-6 grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        <div v-for="actor in actors" :key="actor.id" data-anim>
          <ActorCard :actor="actor" class="h-full" />
        </div>
      </div>
    </div>

    <!-- 3 tiles grandes: Actualmente / Problemas / Se requiere — detalle en HoverCard -->
    <div class="mt-14 grid gap-5 md:grid-cols-3">
      <HoverCard v-for="t in tiles" :key="t.key" :open-delay="120">
        <HoverCardTrigger as-child>
          <Card
            data-anim
            tabindex="0"
            class="group cursor-help p-8 text-center transition-shadow hover:shadow-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
            :class="toneClass(t.tone).card"
          >
            <span
              class="mx-auto flex size-16 items-center justify-center rounded-xl transition-transform group-hover:scale-105"
              :class="toneClass(t.tone).icon"
            >
              <component :is="t.icon" class="size-8" />
            </span>
            <h3 class="mt-5 font-heading text-2xl font-bold">{{ t.title }}</h3>
            <p class="mt-3 text-base leading-relaxed text-foreground/85">
              {{ t.items[0] }}
            </p>
            <Badge
              v-if="t.items.length > 1"
              variant="outline"
              class="mt-4 font-mono text-xs"
            >
              + {{ t.items.length - 1 }} más
            </Badge>
          </Card>
        </HoverCardTrigger>
        <HoverCardContent side="bottom" class="w-96">
          <p class="mb-3 font-heading text-sm font-bold uppercase tracking-wider text-muted-foreground">
            {{ t.title }}
          </p>
          <ul class="space-y-2.5">
            <li
              v-for="(item, i) in t.items"
              :key="i"
              class="flex gap-2.5 text-sm leading-relaxed"
            >
              <span
                aria-hidden="true"
                class="mt-2 inline-block size-1.5 shrink-0 rounded-full"
                :class="toneClass(t.tone).bullet"
              />
              <span>{{ item }}</span>
            </li>
          </ul>
        </HoverCardContent>
      </HoverCard>
    </div>
  </section>
</template>
