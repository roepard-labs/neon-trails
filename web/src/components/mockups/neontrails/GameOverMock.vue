<script setup lang="ts">
const top3 = [
  { medalla: '🥇', nombre: 'JUAN', puntaje: 3 },
  { medalla: '🥈', nombre: 'SARA', puntaje: 2 },
  { medalla: '🥉', nombre: 'LEO', puntaje: 1 },
]
</script>

<template>
  <div class="relative min-h-[420px] overflow-hidden bg-[#0b0b12] p-6">
    <div class="absolute inset-0 bg-grid opacity-25" aria-hidden="true" />
    <div class="relative flex flex-col items-center gap-4 text-center">
      <p class="font-mono text-xs uppercase tracking-[0.3em] text-zinc-500">Game Over</p>
      <h2
        class="font-heading text-4xl font-black text-cyan-400 [text-shadow:0_0_18px_rgb(34_211_238_/_0.6)]"
      >
        ¡Gana P1!
      </h2>

      <div class="flex items-center gap-8 font-mono">
        <div>
          <p class="text-sm text-cyan-400">P1</p>
          <p class="text-2xl font-bold text-zinc-100">3</p>
        </div>
        <span class="text-zinc-600">·</span>
        <div>
          <p class="text-sm text-fuchsia-400">P2</p>
          <p class="text-2xl font-bold text-zinc-100">2</p>
        </div>
      </div>
      <p class="font-mono text-xs text-zinc-500">Tiempo total · 01:24</p>

      <!-- Top 3 -->
      <div class="mt-1 w-full max-w-xs rounded-lg border border-zinc-800 bg-zinc-900/50 p-3 text-left">
        <p class="mb-2 font-mono text-[10px] uppercase tracking-widest text-zinc-500">
          Top 3 — Leaderboard
        </p>
        <ol class="space-y-1.5 text-sm">
          <li
            v-for="r in top3"
            :key="r.nombre"
            class="flex items-center justify-between"
          >
            <span class="text-zinc-200">{{ r.medalla }} {{ r.nombre }}</span>
            <span class="font-mono font-bold text-cyan-400">{{ r.puntaje }}</span>
          </li>
        </ol>
      </div>

      <div class="mt-2 flex gap-3">
        <span class="rounded-lg bg-cyan-400 px-5 py-2 text-xs font-bold text-zinc-950">
          Jugar de nuevo
        </span>
        <span class="rounded-lg border border-zinc-700 px-5 py-2 text-xs text-zinc-300">
          Volver al menú
        </span>
      </div>
    </div>
  </div>
</template>
