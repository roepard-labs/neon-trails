<script setup lang="ts">
import { Signal, Wifi, BatteryFull, Lock } from 'lucide-vue-next'

interface Props {
  frame: 'desktop' | 'mobile'
  url?: string
  title?: string
  device?: string
  time?: string
  caption?: string
}

withDefaults(defineProps<Props>(), {
  url: 'task-manager.app',
  time: '09:41',
})
</script>

<template>
  <figure
    class="mockup-frame group"
    :class="frame === 'mobile' ? 'mockup-frame--mobile' : 'mockup-frame--desktop'"
  >
    <!-- DESKTOP CHROME -->
    <template v-if="frame === 'desktop'">
      <div
        class="overflow-hidden rounded-xl border border-border bg-card shadow-[0_22px_60px_-30px_rgb(0_0_0_/_0.25)] transition-shadow group-hover:shadow-[0_30px_70px_-30px_rgb(0_0_0_/_0.3)]"
      >
        <!-- Title bar -->
        <header
          class="flex items-center gap-3 border-b border-border bg-muted/40 px-4 py-2.5"
        >
          <div class="flex items-center gap-1.5" aria-hidden="true">
            <span class="block size-2.5 rounded-full bg-rose-400/80" />
            <span class="block size-2.5 rounded-full bg-amber-300/90" />
            <span class="block size-2.5 rounded-full bg-emerald-400/90" />
          </div>

          <div
            class="ml-2 hidden flex-1 items-center justify-center sm:flex"
          >
            <div
              class="inline-flex items-center gap-2 rounded-md border border-border bg-background/80 px-3 py-1 font-mono text-[11px] text-muted-foreground"
            >
              <Lock class="size-3 text-emerald-500" />
              <span class="truncate">{{ url }}</span>
            </div>
          </div>

          <span
            v-if="device"
            class="ml-auto hidden font-mono text-[10px] uppercase tracking-widest text-muted-foreground sm:inline"
          >
            {{ device }}
          </span>
        </header>

        <div class="bg-background">
          <slot />
        </div>
      </div>
    </template>

    <!-- MOBILE CHROME -->
    <template v-else>
      <div class="mx-auto w-full max-w-[340px] sm:max-w-[380px] lg:max-w-[400px]">
        <div
          class="relative overflow-hidden rounded-[2.25rem] border border-border bg-card p-1.5 shadow-[0_22px_60px_-30px_rgb(0_0_0_/_0.35)] transition-shadow group-hover:shadow-[0_30px_70px_-30px_rgb(0_0_0_/_0.4)]"
        >
          <div
            class="overflow-hidden rounded-[1.85rem] border border-border bg-background"
          >
            <!-- Status bar -->
            <div
              class="relative flex items-center justify-between px-6 py-2 text-[11px]"
            >
              <span class="font-mono font-semibold tabular-nums">
                {{ time }}
              </span>
              <!-- Notch -->
              <span
                aria-hidden="true"
                class="absolute left-1/2 top-1.5 h-4 w-20 -translate-x-1/2 rounded-full bg-foreground/90"
              />
              <span class="flex items-center gap-1 text-foreground/80">
                <Signal class="size-3" />
                <Wifi class="size-3" />
                <BatteryFull class="size-3.5" />
              </span>
            </div>

            <div class="min-h-[460px] sm:min-h-[520px]">
              <slot />
            </div>

            <!-- Home indicator -->
            <div class="flex items-center justify-center pb-2 pt-1">
              <span
                aria-hidden="true"
                class="inline-block h-1 w-28 rounded-full bg-foreground/60"
              />
            </div>
          </div>
        </div>
      </div>
    </template>

    <figcaption
      v-if="title || caption"
      class="mt-3 flex flex-col gap-1 px-1 text-xs sm:flex-row sm:items-baseline sm:gap-2"
    >
      <span v-if="title" class="font-medium text-foreground">{{ title }}</span>
      <span v-if="caption" class="text-muted-foreground">{{ caption }}</span>
    </figcaption>
  </figure>
</template>
