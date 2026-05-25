<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Briefcase, User, Cog, ListChecks, Shield, type LucideIcon } from 'lucide-vue-next'
import { gsap } from '@/lib/gsap'
import PageHeader from '@/components/layout/PageHeader.vue'
import RequirementCategory from '@/components/content/RequirementCategory.vue'
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { usePrefersReducedMotion } from '@/composables/usePrefersReducedMotion'
import { requirementGroups } from '@/data/requirements'

useSectionAnimation()
useScrollReveal('[data-reveal]')

const prefersReducedMotion = usePrefersReducedMotion()

const totalRequerimientos = computed(() =>
  requirementGroups.reduce((acc, g) => acc + g.items.length, 0),
)

const counter = ref(0)

const iconMap: Record<string, LucideIcon> = {
  negocio: Briefcase,
  usuario: User,
  sistema: Cog,
  funcional: ListChecks,
  'no-funcional': Shield,
}

const categoriaCortaMap: Record<string, string> = {
  negocio: 'Negocio',
  usuario: 'Usuario',
  sistema: 'Sistema',
  funcional: 'Funcional',
  'no-funcional': 'No funcional',
}

onMounted(() => {
  if (prefersReducedMotion.value) {
    counter.value = totalRequerimientos.value
    return
  }
  gsap.to(counter, {
    value: totalRequerimientos.value,
    duration: 1.2,
    ease: 'power2.out',
    snap: { value: 1 },
  })
})
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="3"
      titulo="Requerimientos"
      subtitulo="Negocio, usuario, sistema y F/NF — 31 requerimientos en 5 categorías."
    />

    <!-- Counter + KPI tiles por categoría -->
    <div
      data-anim
      class="mb-8 flex flex-col items-center gap-4 rounded-2xl border border-border bg-card p-6 text-center sm:p-8"
    >
      <span class="font-mono text-7xl font-bold tabular-nums text-brand sm:text-8xl">
        {{ counter }}
      </span>
      <span class="font-heading text-lg uppercase tracking-[0.18em] text-muted-foreground">
        requerimientos totales
      </span>
    </div>

    <div
      data-anim
      class="mb-10 grid grid-cols-2 gap-3 sm:grid-cols-5 sm:gap-4"
    >
      <div
        v-for="g in requirementGroups"
        :key="`tile-${g.categoria}`"
        class="flex flex-col items-center rounded-xl border border-border bg-card p-5 text-center transition-shadow hover:shadow-md"
      >
        <span class="flex size-12 items-center justify-center rounded-lg bg-brand/12 text-brand">
          <component :is="iconMap[g.categoria] ?? ListChecks" class="size-6" />
        </span>
        <span class="mt-3 font-mono text-3xl font-bold tabular-nums text-foreground">
          {{ g.items.length }}
        </span>
        <span class="mt-1 text-sm font-medium text-muted-foreground">
          {{ categoriaCortaMap[g.categoria] ?? g.categoria }}
        </span>
      </div>
    </div>

    <!-- Tabs por categoría -->
    <Tabs data-anim default-value="negocio" class="w-full print:hidden">
      <TabsList class="grid h-auto w-full grid-cols-2 gap-1 bg-muted/50 p-1.5 sm:grid-cols-5">
        <TabsTrigger
          v-for="g in requirementGroups"
          :key="g.categoria"
          :value="g.categoria"
          class="flex h-auto flex-col items-center gap-1.5 py-3"
        >
          <component :is="iconMap[g.categoria] ?? ListChecks" class="size-5 text-brand" />
          <span class="text-sm font-semibold">{{ categoriaCortaMap[g.categoria] ?? g.titulo }}</span>
        </TabsTrigger>
      </TabsList>

      <TabsContent
        v-for="g in requirementGroups"
        :key="g.categoria"
        :value="g.categoria"
        class="mt-6 focus-visible:ring-0"
      >
        <div data-reveal>
          <RequirementCategory :group="g" />
        </div>
      </TabsContent>
    </Tabs>

    <!-- Vista print: expandir todas las categorías -->
    <div class="hidden space-y-7 print:block">
      <RequirementCategory
        v-for="g in requirementGroups"
        :key="g.categoria"
        :group="g"
      />
    </div>
  </section>
</template>
