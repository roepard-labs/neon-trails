<script setup lang="ts">
import { Trophy } from 'lucide-vue-next'

const rows = [
  { nombre: 'JUAN', puntaje: 9, fecha: '2026-05-25' },
  { nombre: 'SARA', puntaje: 7, fecha: '2026-05-25' },
  { nombre: 'LEO', puntaje: 6, fecha: '2026-05-24' },
  { nombre: 'MARI', puntaje: 4, fecha: '2026-05-24' },
  { nombre: 'P2', puntaje: 2, fecha: '2026-05-23' },
]
</script>

<template>
  <div class="min-h-[420px] p-6">
    <div class="mb-4 flex items-center gap-2">
      <Trophy class="size-5 text-brand" />
      <h2 class="font-heading text-xl font-bold">Leaderboard</h2>
      <span class="ml-auto rounded border border-border bg-muted/40 px-2 py-0.5 font-mono text-[11px] text-muted-foreground">
        GET /api/scores
      </span>
    </div>
    <table class="w-full text-sm">
      <thead>
        <tr
          class="border-b border-border text-left font-mono text-[11px] uppercase tracking-wider text-muted-foreground"
        >
          <th class="py-2 pr-2 font-medium">#</th>
          <th class="py-2 font-medium">Jugador</th>
          <th class="py-2 text-right font-medium">Puntaje</th>
          <th class="py-2 text-right font-medium">Fecha</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(r, i) in rows" :key="i" class="border-b border-border/60">
          <td class="py-2.5 pr-2 font-mono text-muted-foreground">{{ i + 1 }}</td>
          <td class="py-2.5 font-medium text-foreground">{{ r.nombre }}</td>
          <td class="py-2.5 text-right font-mono font-bold text-brand">{{ r.puntaje }}</td>
          <td class="py-2.5 text-right font-mono text-xs text-muted-foreground">{{ r.fecha }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
