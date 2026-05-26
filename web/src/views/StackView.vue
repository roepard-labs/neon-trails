<script setup lang="ts">
import type { Component } from 'vue'
import { Gamepad2, Server, Code2, Database, Layers } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ExplainCard from '@/components/content/ExplainCard.vue'
import CodeBlock from '@/components/content/CodeBlock.vue'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import { stackLayers } from '@/data/stack'

useSectionAnimation()

// Mapa nombre lucide (del dato) → componente real.
const iconMap: Record<string, Component> = { Gamepad2, Server, Code2, Database }

// Pestaña abierta por defecto (constante segura para noUncheckedIndexedAccess).
const firstLayerId = stackLayers[0]?.id ?? 'juego'
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="9"
      titulo="Stack & Código en vivo"
      subtitulo="Las cuatro capas del monolito, explicadas dos veces: una analogía sencilla y el código real que la hace funcionar."
    />

    <!-- Hilo conductor de toda la sección -->
    <div data-anim class="mb-8">
      <ExplainCard titulo="La idea en una frase">
        <template #simple>
          El juego anota su puntaje en una <strong class="text-foreground">libreta</strong> (la API), y la
          web <strong class="text-foreground">lee</strong> esa libreta para mostrar el ranking. Todo lo
          demás son los detalles de cómo cada parte hace su trabajo.
        </template>
        <template #detalle>
          <p>
            Cuatro tecnologías, un solo flujo:
            <span class="text-glow-cyan-soft font-semibold text-brand">Java</span> (el juego) →
            <span class="font-semibold text-p2">PHP/Laravel</span> (la API + panel) →
            <span class="text-glow-cyan-soft font-semibold text-brand">Vue/TS</span> (la web) →
            <span class="font-semibold text-p2">MariaDB</span> (los datos). Cada pestaña abre una capa.
          </p>
        </template>
      </ExplainCard>
    </div>

    <Tabs data-anim :default-value="firstLayerId" class="w-full">
      <TabsList class="mb-6 flex h-auto flex-wrap justify-start gap-1">
        <TabsTrigger
          v-for="l in stackLayers"
          :key="l.id"
          :value="l.id"
          class="gap-2 data-[state=active]:text-brand"
        >
          <component :is="iconMap[l.icon] ?? Layers" class="size-4" />
          {{ l.label }}
        </TabsTrigger>
      </TabsList>

      <TabsContent v-for="l in stackLayers" :key="l.id" :value="l.id" class="space-y-5">
        <p class="font-mono text-sm uppercase tracking-wider text-muted-foreground">
          {{ l.tagline }}
        </p>

        <ExplainCard :simple="l.simple">
          <template #detalle>
            <p>{{ l.detalle }}</p>
          </template>
        </ExplainCard>

        <div v-for="snip in l.snippets" :key="snip.filename">
          <CodeBlock
            :code="snip.code"
            :lang="snip.lang"
            :filename="snip.filename"
            :player="snip.player"
          />
          <p
            v-if="snip.note"
            class="-mt-3 mb-1 flex items-start gap-2 px-1 text-sm leading-relaxed text-muted-foreground"
          >
            <span aria-hidden="true" class="mt-1 inline-block size-1.5 shrink-0 rounded-full bg-brand" />
            <span>{{ snip.note }}</span>
          </p>
        </div>
      </TabsContent>
    </Tabs>
  </section>
</template>
