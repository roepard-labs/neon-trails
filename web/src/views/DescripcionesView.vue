<script setup lang="ts">
import { computed } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import UseCaseDescriptionCard from '@/components/content/UseCaseDescriptionCard.vue'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { useCases } from '@/data/useCases'

useSectionAnimation()
useScrollReveal('[data-reveal]')

const principales = computed(() => useCases.filter((uc) => uc.tipo === 'principal'))
const secundarios = computed(() => useCases.filter((uc) => uc.tipo === 'secundario'))
</script>

<template>
  <section>
    <PageHeader
      :numero="6"
      titulo="Descripciones de casos de uso"
      subtitulo="Ocho fichas formales (cuatro primarias y cuatro secundarias) ancladas al código real de Laravel y Vue."
    />

    <p data-anim class="mb-10 max-w-3xl text-base leading-relaxed text-muted-foreground">
      Plantilla del curso: nombre, actores, tipo, precondiciones, descripción, flujos, excepciones (EX-NN) y postcondiciones. Solo la descripción está expandida; el resto se abre bajo demanda.
    </p>

    <!-- Principales -->
    <div data-anim class="mb-12">
      <div class="mb-5 flex items-center gap-3">
        <span class="h-px flex-1 bg-border" aria-hidden="true" />
        <span class="font-mono text-sm uppercase tracking-[0.18em] text-brand">
          Casos de uso principales · {{ principales.length }}
        </span>
        <span class="h-px flex-1 bg-border" aria-hidden="true" />
      </div>

      <div class="space-y-7">
        <div v-for="uc in principales" :key="uc.codigo" data-reveal>
          <UseCaseDescriptionCard :use-case="uc" />
        </div>
      </div>
    </div>

    <!-- Secundarios -->
    <div data-anim>
      <div class="mb-5 flex items-center gap-3">
        <span class="h-px flex-1 bg-border" aria-hidden="true" />
        <span class="font-mono text-sm uppercase tracking-[0.18em] text-muted-foreground">
          Casos de uso secundarios · {{ secundarios.length }}
        </span>
        <span class="h-px flex-1 bg-border" aria-hidden="true" />
      </div>

      <div class="space-y-7">
        <div v-for="uc in secundarios" :key="uc.codigo" data-reveal>
          <UseCaseDescriptionCard :use-case="uc" />
        </div>
      </div>
    </div>
  </section>
</template>
