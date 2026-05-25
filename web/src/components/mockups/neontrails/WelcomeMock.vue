<script setup lang="ts">
import { Play } from 'lucide-vue-next'
</script>

<template>
  <div class="relative min-h-[420px] overflow-hidden bg-[#0b0b12] p-8 text-center">
    <div class="absolute inset-0 bg-grid opacity-40" aria-hidden="true" />
    <div class="relative flex min-h-[356px] flex-col items-center justify-center gap-5">
      <p class="font-mono text-[11px] uppercase tracking-[0.3em] text-cyan-400/80">
        Técnicas de Programación · UAM
      </p>
      <h2 class="font-heading text-5xl font-black tracking-tight">
        <span class="text-cyan-400 [text-shadow:0_0_18px_rgb(34_211_238_/_0.6)]">NEON</span>
        <span class="text-fuchsia-500 [text-shadow:0_0_18px_rgb(217_70_239_/_0.6)]"> TRAILS</span>
      </h2>
      <p class="max-w-sm text-sm text-zinc-400">
        Arena local para dos jugadores · motos de luz · discos que rebotan
      </p>
      <div class="mt-2 flex flex-col items-center gap-3">
        <span
          class="inline-flex items-center gap-2 rounded-lg bg-cyan-400 px-8 py-2.5 text-sm font-bold text-zinc-950"
        >
          <Play class="size-4" /> Jugar
        </span>
        <span class="rounded-lg border border-zinc-700 px-8 py-2 text-sm font-medium text-zinc-300">
          Instrucciones
        </span>
      </div>
      <p class="mt-3 font-mono text-[11px] text-zinc-600">Docente: Leonardo Montes</p>
    </div>
  </div>
</template>
