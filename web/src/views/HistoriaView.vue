<script setup lang="ts">
import { Dna, Sparkles, GraduationCap, Boxes } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ExplainCard from '@/components/content/ExplainCard.vue'
import StepList from '@/components/content/StepList.vue'
import NeonSprite from '@/components/content/NeonSprite.vue'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import {
  origenSimple,
  origenDetalle,
  tronElementos,
  retoSimple,
  retoDetalle,
  pooPilares,
  adnDiseno,
  evolucion,
} from '@/data/history'

useSectionAnimation()
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="2"
      titulo="Historia & ADN"
      subtitulo="Por qué hicimos Neon Trails y en qué nos basamos: la inspiración (Tron), el reto académico (demostrar POO) y cómo creció el proyecto."
    />

    <!-- ── De dónde viene ────────────────────────────────────────── -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Sparkles class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">La inspiración: Tron</h2>
    </div>

    <div data-anim class="mb-6 grid items-center gap-5 lg:grid-cols-[1fr_auto]">
      <ExplainCard :simple="origenSimple">
        <template #detalle>
          <p>{{ origenDetalle }}</p>
        </template>
      </ExplainCard>
      <div class="hidden items-center justify-center gap-4 lg:flex">
        <NeonSprite player="p1" state="bike" :size="92" alt="Moto del jugador 1" />
        <NeonSprite player="p2" state="bike" :size="92" alt="Moto del jugador 2" />
      </div>
    </div>

    <div data-anim class="mb-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <div
        v-for="el in tronElementos"
        :key="el.titulo"
        class="rounded-xl border border-border bg-card p-4"
      >
        <p class="font-heading text-lg font-semibold text-brand">{{ el.titulo }}</p>
        <p class="mt-1.5 text-sm leading-relaxed text-muted-foreground">{{ el.descripcion }}</p>
      </div>
    </div>

    <!-- ── El reto académico ─────────────────────────────────────── -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-p2/15 text-p2">
        <GraduationCap class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">El reto académico (el ADN)</h2>
    </div>

    <div data-anim class="mb-6">
      <ExplainCard :simple="retoSimple">
        <template #detalle>
          <p>{{ retoDetalle }}</p>
        </template>
      </ExplainCard>
    </div>

    <p data-anim class="mb-4 font-mono text-sm uppercase tracking-wider text-muted-foreground">
      Los 4 pilares de la POO, en este proyecto
    </p>
    <div data-anim class="mb-12 grid gap-4 sm:grid-cols-2">
      <div
        v-for="p in pooPilares"
        :key="p.pilar"
        class="rounded-xl border border-border bg-card p-4"
      >
        <p class="font-heading text-lg font-semibold text-foreground">{{ p.pilar }}</p>
        <p class="mt-1 text-sm leading-relaxed text-muted-foreground">{{ p.queEs }}</p>
        <code class="mt-2 block break-words font-mono text-xs text-brand">{{ p.dondeSeVe }}</code>
      </div>
    </div>

    <!-- ── Cómo creció ───────────────────────────────────────────── -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Boxes class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">Cómo creció el proyecto</h2>
    </div>
    <div data-anim class="mb-8">
      <StepList :steps="evolucion" player="p1" />
    </div>

    <div data-anim class="flex items-start gap-3 rounded-xl border border-p2/30 bg-p2/5 p-4">
      <Dna class="mt-0.5 size-5 shrink-0 text-p2" />
      <p class="text-sm leading-relaxed text-foreground/90">{{ adnDiseno }}</p>
    </div>
  </section>
</template>
