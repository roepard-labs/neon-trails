<script setup lang="ts">
import { Heart } from 'lucide-vue-next'
</script>

<template>
  <div class="relative min-h-[420px] overflow-hidden bg-[#0b0b12]">
    <!-- HUD -->
    <div class="relative flex items-center justify-between px-5 py-3 font-mono text-xs">
      <div class="flex items-center gap-2 text-cyan-400">
        <span class="font-bold">P1</span>
        <span class="flex text-rose-400">
          <Heart class="size-3 fill-current" />
          <Heart class="size-3 fill-current" />
          <Heart class="size-3 fill-current" />
        </span>
        <span class="text-zinc-400">· 3 pts</span>
      </div>
      <div class="rounded bg-zinc-900/80 px-3 py-1 tabular-nums text-zinc-200">01:24</div>
      <div class="flex items-center gap-2 text-fuchsia-400">
        <span class="text-zinc-400">2 pts ·</span>
        <span class="flex text-rose-400">
          <Heart class="size-3 fill-current" />
          <Heart class="size-3 fill-current" />
        </span>
        <span class="font-bold">P2</span>
      </div>
    </div>

    <!-- Arena -->
    <div class="relative mx-5 mb-5 h-[330px] overflow-hidden rounded-lg border border-zinc-800">
      <div class="absolute inset-0 bg-grid opacity-30" aria-hidden="true" />
      <!-- Estela P1 (cian, horizontal) -->
      <div
        class="absolute left-6 top-20 h-1.5 w-52 rounded-full bg-cyan-400 shadow-[0_0_12px_rgb(34_211_238_/_0.8)]"
      />
      <div
        class="absolute left-[14.5rem] top-[4.6rem] size-4 rounded-sm bg-cyan-300 shadow-[0_0_16px_rgb(34_211_238)]"
      />
      <!-- Estela P2 (magenta, vertical) -->
      <div
        class="absolute right-16 top-10 h-44 w-1.5 rounded-full bg-fuchsia-500 shadow-[0_0_12px_rgb(217_70_239_/_0.8)]"
      />
      <div
        class="absolute right-[3.65rem] top-[12.5rem] size-4 rounded-sm bg-fuchsia-400 shadow-[0_0_16px_rgb(217_70_239)]"
      />
      <!-- Disco -->
      <div
        class="absolute left-1/2 top-1/2 size-3 -translate-x-1/2 -translate-y-1/2 rounded-full bg-amber-300 shadow-[0_0_12px_rgb(252_211_77)]"
      />
      <!-- Badge modo moto -->
      <span
        class="absolute bottom-3 left-3 rounded bg-cyan-400/15 px-2 py-0.5 font-mono text-[10px] font-bold text-cyan-300"
      >
        [MOTO]
      </span>
    </div>
  </div>
</template>
