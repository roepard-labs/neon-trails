<script setup lang="ts">
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { useSectionNav } from '@/composables/useSectionNav'

// Botones flotantes (abajo a la derecha) para pasar de slide en slide, además
// de las flechas del teclado. Comparten lógica con useAppShortcuts vía useSectionNav.
const { prev, next, hasPrev, hasNext } = useSectionNav()
</script>

<template>
  <div
    class="fixed right-5 bottom-[calc(1.25rem+env(safe-area-inset-bottom))] z-30 flex items-center gap-2 print:hidden"
  >
    <button
      type="button"
      :disabled="!hasPrev"
      aria-label="Sección anterior"
      class="inline-flex size-11 items-center justify-center rounded-full border border-border bg-card/90 text-foreground shadow-[var(--nt-shadow-card)] backdrop-blur transition-all hover:border-brand hover:text-brand hover:shadow-glow-cyan focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-30"
      @click="prev"
    >
      <ChevronLeft class="size-5" />
    </button>
    <button
      type="button"
      :disabled="!hasNext"
      aria-label="Siguiente sección"
      class="inline-flex size-11 items-center justify-center rounded-full border border-border bg-card/90 text-foreground shadow-[var(--nt-shadow-card)] backdrop-blur transition-all hover:border-brand hover:text-brand hover:shadow-glow-cyan focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-30"
      @click="next"
    >
      <ChevronRight class="size-5" />
    </button>
  </div>
</template>
