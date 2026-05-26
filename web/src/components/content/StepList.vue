<script setup lang="ts">
interface Step {
  titulo: string
  detalle?: string
}

withDefaults(defineProps<{ steps: Step[]; player?: 'p1' | 'p2' }>(), {
  player: 'p1',
})
</script>

<template>
  <!-- Lista numerada con badges neón — pensada para pasos de Docker/Dokploy. -->
  <ol class="space-y-3">
    <li
      v-for="(s, i) in steps"
      :key="i"
      class="flex gap-4 rounded-lg border border-border bg-card/60 p-4"
    >
      <span
        class="flex size-8 shrink-0 items-center justify-center rounded-full border font-mono text-sm font-semibold"
        :class="player === 'p2'
          ? 'border-p2/50 bg-p2/10 text-p2'
          : 'border-brand/50 bg-brand/10 text-brand'"
      >
        {{ i + 1 }}
      </span>
      <div class="space-y-1 pt-0.5">
        <p class="font-medium text-foreground">{{ s.titulo }}</p>
        <p v-if="s.detalle" class="text-sm leading-relaxed text-muted-foreground">
          {{ s.detalle }}
        </p>
      </div>
    </li>
  </ol>
</template>
