<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { gsap } from '@/lib/gsap'
import { Briefcase, User, Cog, ListChecks, Shield, type LucideIcon } from 'lucide-vue-next'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import type { RequirementGroup } from '@/types/domain'
import { usePrefersReducedMotion } from '@/composables/usePrefersReducedMotion'

interface Props {
  group: RequirementGroup
}

defineProps<Props>()

const root = ref<HTMLElement | null>(null)
const prefersReducedMotion = usePrefersReducedMotion()

const iconMap: Record<string, LucideIcon> = {
  negocio: Briefcase,
  usuario: User,
  sistema: Cog,
  funcional: ListChecks,
  'no-funcional': Shield,
}

onMounted(() => {
  const items = root.value?.querySelectorAll('[data-req-item]') ?? []
  if (!items.length) return
  if (prefersReducedMotion.value) {
    gsap.set(items, { opacity: 1, y: 0, clearProps: 'all' })
    return
  }
  // Animación al montar (NO scroll-trigger): dentro de los tabs, los paneles
  // inactivos no tienen layout y un ScrollTrigger jamás dispararía, dejando los
  // chips atascados en opacity:0 al cambiar de pestaña. Al montar siempre terminan visibles.
  gsap.from(items, {
    opacity: 0,
    y: 12,
    duration: 0.4,
    stagger: 0.03,
    ease: 'power2.out',
  })
})
</script>

<template>
  <article ref="root" class="rounded-2xl border border-border bg-card">
    <header class="flex items-center gap-4 border-b border-border p-5 sm:p-6">
      <span class="flex size-14 shrink-0 items-center justify-center rounded-xl bg-brand/12 text-brand">
        <component :is="iconMap[group.categoria] ?? ListChecks" class="size-7" />
      </span>
      <div class="flex-1">
        <h2 class="font-heading text-2xl font-bold tracking-tight">{{ group.titulo }}</h2>
        <p class="mt-1 text-base text-muted-foreground">{{ group.subtitulo }}</p>
      </div>
      <span class="hidden font-mono text-sm text-muted-foreground sm:inline">
        {{ group.items.length }} ítems
      </span>
    </header>

    <!-- Chips clickables — el detalle aparece en Popover al hacer click -->
    <div v-auto-animate class="flex flex-wrap gap-2 p-5 sm:gap-3 sm:p-6 print:hidden">
      <Popover v-for="req in group.items" :key="req.codigo">
        <PopoverTrigger as-child>
          <button
            data-req-item
            type="button"
            class="inline-flex items-center gap-2 rounded-lg border border-border bg-background px-3 py-2 font-mono text-sm font-semibold transition-colors hover:border-brand hover:bg-brand/8 hover:text-brand focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
          >
            {{ req.codigo }}
          </button>
        </PopoverTrigger>
        <PopoverContent class="w-96" side="bottom">
          <p class="mb-2 inline-flex items-center gap-2 rounded-md bg-brand px-2 py-0.5 font-mono text-sm font-semibold text-brand-foreground">
            {{ req.codigo }}
          </p>
          <p class="font-heading text-base font-semibold text-foreground">
            {{ req.nombre }}
          </p>
          <p class="mt-2 text-sm leading-relaxed text-muted-foreground">
            {{ req.descripcion }}
          </p>
        </PopoverContent>
      </Popover>
    </div>

    <!-- Vista print: lista completa -->
    <ul class="hidden divide-y divide-border print:block">
      <li
        v-for="req in group.items"
        :key="`p-${req.codigo}`"
        class="flex flex-col gap-2 px-6 py-4 sm:flex-row sm:items-start sm:gap-5"
      >
        <span
          class="inline-flex w-fit shrink-0 items-center rounded-md bg-brand px-2 py-0.5 font-mono text-xs font-semibold text-brand-foreground sm:mt-0.5"
        >
          {{ req.codigo }}
        </span>
        <div class="flex-1">
          <p class="font-medium text-foreground">{{ req.nombre }}</p>
          <p class="mt-1 text-sm leading-relaxed text-muted-foreground">
            {{ req.descripcion }}
          </p>
        </div>
      </li>
    </ul>
  </article>
</template>
