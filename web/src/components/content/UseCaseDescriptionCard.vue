<script setup lang="ts">
import { computed } from 'vue'
import { AlertOctagon, GitFork, ListOrdered, Lock, Unlock, FileText } from 'lucide-vue-next'
import { Badge } from '@/components/ui/badge'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import type { UseCase } from '@/types/domain'
import { actorById } from '@/data/actors'

interface Props {
  useCase: UseCase
}

const props = defineProps<Props>()

const actorNames = computed(() =>
  props.useCase.actores.map((id) => actorById(id)?.nombre ?? id),
)

const defaultOpen = computed(() => ['descripcion'])
</script>

<template>
  <article
    class="overflow-hidden rounded-xl border border-border bg-card shadow-sm"
  >
    <!-- Header -->
    <header class="border-b border-border bg-gradient-to-br from-brand/10 via-transparent to-transparent p-4 sm:p-6">
      <div class="flex items-center justify-between gap-3">
        <Badge
          class="font-mono text-sm"
          :class="
            useCase.tipo === 'principal'
              ? 'bg-brand text-brand-foreground hover:bg-brand/90'
              : ''
          "
          :variant="useCase.tipo === 'principal' ? 'default' : 'secondary'"
        >
          {{ useCase.codigo }}
        </Badge>
        <span class="font-mono text-xs uppercase tracking-[0.18em] text-muted-foreground">
          Caso de uso · {{ useCase.tipo }}
        </span>
      </div>
      <h3 class="mt-4 font-heading text-xl font-bold tracking-tight sm:text-2xl">
        {{ useCase.nombre }}
      </h3>
      <div class="mt-3 flex flex-wrap items-center gap-2 text-xs">
        <span class="text-muted-foreground">Actor(es):</span>
        <Badge
          v-for="name in actorNames"
          :key="name"
          variant="outline"
          class="text-sm"
        >
          {{ name }}
        </Badge>
      </div>
    </header>

    <!-- Cuerpo: Accordion -->
    <Accordion
      type="multiple"
      :default-value="defaultOpen"
      class="w-full px-4 sm:px-6"
    >
      <AccordionItem value="descripcion">
        <AccordionTrigger class="text-base">
          <span class="flex items-center gap-2 font-mono text-xs uppercase tracking-[0.18em] text-brand">
            <FileText class="size-3.5" />
            Descripción
          </span>
        </AccordionTrigger>
        <AccordionContent>
          <p class="pb-2 text-base leading-relaxed text-foreground">
            {{ useCase.descripcion }}
          </p>
        </AccordionContent>
      </AccordionItem>

      <AccordionItem value="precondiciones">
        <AccordionTrigger class="text-base">
          <span class="flex items-center gap-2 font-mono text-xs uppercase tracking-[0.18em] text-brand">
            <Lock class="size-3.5" />
            Precondiciones
            <span class="font-sans normal-case tracking-normal text-muted-foreground">
              · {{ useCase.precondiciones.length }}
            </span>
          </span>
        </AccordionTrigger>
        <AccordionContent>
          <ul class="space-y-1.5 pb-2">
            <li
              v-for="(p, i) in useCase.precondiciones"
              :key="i"
              class="flex gap-2.5 text-base leading-relaxed text-foreground"
            >
              <span aria-hidden="true" class="mt-2 inline-block size-1 shrink-0 rounded-full bg-brand" />
              <span>{{ p }}</span>
            </li>
          </ul>
        </AccordionContent>
      </AccordionItem>

      <AccordionItem value="flujo-normal">
        <AccordionTrigger class="text-base">
          <span class="flex items-center gap-2 font-mono text-xs uppercase tracking-[0.18em] text-brand">
            <ListOrdered class="size-3.5" />
            Flujo normal de eventos
            <span class="font-sans normal-case tracking-normal text-muted-foreground">
              · {{ useCase.flujoNormal.length }} pasos
            </span>
          </span>
        </AccordionTrigger>
        <AccordionContent>
          <ol class="space-y-2 pb-2">
            <li
              v-for="(p, i) in useCase.flujoNormal"
              :key="i"
              class="grid grid-cols-[auto_1fr] gap-3 text-base leading-relaxed"
            >
              <span class="font-mono text-xs text-muted-foreground">
                {{ String(i + 1).padStart(2, '0') }}
              </span>
              <span>{{ p }}</span>
            </li>
          </ol>
        </AccordionContent>
      </AccordionItem>

      <AccordionItem v-if="useCase.flujosAlternos.length" value="flujos-alternos">
        <AccordionTrigger class="text-base">
          <span class="flex items-center gap-2 font-mono text-xs uppercase tracking-[0.18em] text-brand">
            <GitFork class="size-3.5" />
            Flujos alternos
            <span class="font-sans normal-case tracking-normal text-muted-foreground">
              · {{ useCase.flujosAlternos.length }}
            </span>
          </span>
        </AccordionTrigger>
        <AccordionContent>
          <div class="space-y-4 pb-2">
            <div
              v-for="(fa, i) in useCase.flujosAlternos"
              :key="i"
              class="rounded-md border border-border bg-accent/30 p-4"
            >
              <p class="text-base font-medium text-foreground">{{ fa.titulo }}</p>
              <ol class="mt-2 space-y-1.5">
                <li
                  v-for="(p, j) in fa.pasos"
                  :key="j"
                  class="grid grid-cols-[auto_1fr] gap-3 text-base leading-relaxed text-muted-foreground"
                >
                  <span class="font-mono text-xs">{{ String(j + 1).padStart(2, '0') }}</span>
                  <span>{{ p }}</span>
                </li>
              </ol>
            </div>
          </div>
        </AccordionContent>
      </AccordionItem>

      <AccordionItem v-if="useCase.excepciones.length" value="excepciones">
        <AccordionTrigger class="text-base">
          <span class="flex items-center gap-2 font-mono text-xs uppercase tracking-[0.18em] text-brand">
            <AlertOctagon class="size-3.5" />
            Excepciones
            <span class="font-sans normal-case tracking-normal text-muted-foreground">
              · {{ useCase.excepciones.length }}
            </span>
          </span>
        </AccordionTrigger>
        <AccordionContent>
          <div class="space-y-3 pb-2">
            <div
              v-for="(e, i) in useCase.excepciones"
              :key="i"
              class="rounded-md border border-destructive/30 bg-destructive/[0.04] p-4"
            >
              <p class="text-base font-medium text-foreground">{{ e.titulo }}</p>
              <p class="mt-1 text-base leading-relaxed text-muted-foreground">
                {{ e.descripcion }}
              </p>
            </div>
          </div>
        </AccordionContent>
      </AccordionItem>

      <AccordionItem value="postcondiciones">
        <AccordionTrigger class="text-base">
          <span class="flex items-center gap-2 font-mono text-xs uppercase tracking-[0.18em] text-brand">
            <Unlock class="size-3.5" />
            Postcondiciones
            <span class="font-sans normal-case tracking-normal text-muted-foreground">
              · {{ useCase.postcondiciones.length }}
            </span>
          </span>
        </AccordionTrigger>
        <AccordionContent>
          <ul class="space-y-1.5 pb-2">
            <li
              v-for="(p, i) in useCase.postcondiciones"
              :key="i"
              class="flex gap-2.5 text-base leading-relaxed text-foreground"
            >
              <span aria-hidden="true" class="mt-2 inline-block size-1 shrink-0 rounded-full bg-brand" />
              <span>{{ p }}</span>
            </li>
          </ul>
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  </article>
</template>
