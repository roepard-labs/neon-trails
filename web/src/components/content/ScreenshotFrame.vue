<script setup lang="ts">
import { ref } from 'vue'
import NeonSprite from './NeonSprite.vue'

interface Props {
  /** Ruta de la captura (p. ej. `/screenshots/vnc-docker.png`). */
  src: string
  /** Texto alternativo accesible (obligatorio). */
  alt: string
  /** Leyenda en la barra HUD; cae a `alt` si se omite. */
  caption?: string
  /** Color del cromo HUD: cyan (P1) o magenta (P2). */
  player?: 'p1' | 'p2'
}

const props = withDefaults(defineProps<Props>(), { player: 'p1' })

// Si la imagen aún no existe, mostramos un placeholder neón en vez de romper.
const failed = ref(false)

const cornerColor = props.player === 'p2' ? 'border-p2' : 'border-brand'
</script>

<template>
  <figure
    class="nt-shot relative overflow-hidden rounded-xl border border-border bg-card shadow-[var(--nt-shadow-card)]"
  >
    <figcaption
      class="flex items-center gap-2 border-b border-border bg-background/70 px-3 py-2 font-mono text-xs text-muted-foreground"
    >
      <span
        aria-hidden="true"
        class="size-2 shrink-0 rounded-full"
        :class="player === 'p2' ? 'bg-p2' : 'bg-brand'"
      />
      <span class="truncate">{{ caption ?? alt }}</span>
    </figcaption>

    <div class="relative">
      <img
        v-if="!failed"
        :src="src"
        :alt="alt"
        loading="lazy"
        decoding="async"
        class="block w-full"
        @error="failed = true"
      />
      <div
        v-else
        class="grid place-items-center gap-3 bg-arena-grid px-6 py-16 text-center"
      >
        <NeonSprite :player="player" state="idle" :size="84" />
        <p class="font-mono text-sm text-muted-foreground">
          Captura pendiente · <code class="text-foreground">{{ src }}</code>
        </p>
      </div>

      <!-- Cromo HUD: scanlines + esquinas tipo visor -->
      <span
        aria-hidden="true"
        class="nt-scanlines pointer-events-none absolute inset-0 opacity-20"
      />
      <span aria-hidden="true" class="pointer-events-none absolute left-2 top-2 size-4 border-l-2 border-t-2" :class="cornerColor" />
      <span aria-hidden="true" class="pointer-events-none absolute right-2 top-2 size-4 border-r-2 border-t-2" :class="cornerColor" />
      <span aria-hidden="true" class="pointer-events-none absolute bottom-2 left-2 size-4 border-b-2 border-l-2" :class="cornerColor" />
      <span aria-hidden="true" class="pointer-events-none absolute bottom-2 right-2 size-4 border-b-2 border-r-2" :class="cornerColor" />
    </div>
  </figure>
</template>
