<script setup lang="ts">
interface Props {
  numero?: number
  titulo: string
  subtitulo?: string
  eyebrow?: string
}

defineProps<Props>()
</script>

<template>
  <header data-anim class="mb-8 border-b border-border pb-6 sm:mb-10 lg:mb-12">
    <div class="flex items-baseline gap-3">
      <span
        v-if="eyebrow ?? numero !== undefined"
        class="font-mono text-xs uppercase tracking-[0.18em] text-brand"
      >
        {{ eyebrow ?? `Sección ${String(numero).padStart(2, '0')}` }}
      </span>
    </div>
    <h1
      class="mt-3 text-balance font-bold tracking-tight text-foreground text-[clamp(2rem,2.5vw+1.25rem,3.5rem)] presenter:text-[clamp(2.5rem,3.5vw+1.25rem,4.5rem)]"
    >
      {{ titulo }}
    </h1>
    <p
      v-if="subtitulo"
      class="mt-4 max-w-3xl text-base leading-relaxed text-muted-foreground sm:text-lg md:text-xl presenter:max-w-4xl"
    >
      {{ subtitulo }}
    </p>
  </header>
</template>
