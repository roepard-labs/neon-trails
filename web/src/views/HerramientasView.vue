<script setup lang="ts">
import { h } from 'vue'
import type { ColumnDef } from '@tanstack/vue-table'
import { Boxes, Wand2, Bot } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ExplainCard from '@/components/content/ExplainCard.vue'
import CodeBlock from '@/components/content/CodeBlock.vue'
import ScreenshotFrame from '@/components/content/ScreenshotFrame.vue'
import DataTable from '@/components/content/DataTable.vue'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import {
  miseSimple,
  miseDetalle,
  makeSimple,
  makeDetalle,
  ciSimple,
  ciDetalle,
  toolVersions,
  makeTargets,
  ciWorkflows,
  miseSnippet,
  makefileSnippet,
  ciSnippet,
  type ToolVersion,
  type MakeTarget,
} from '@/data/tools'

useSectionAnimation()

// ── Columnas de la tabla de versiones ───────────────────────────────────────
const versionColumns: ColumnDef<ToolVersion>[] = [
  { accessorKey: 'herramienta', header: 'Herramienta' },
  {
    accessorKey: 'version',
    header: 'Versión',
    cell: ({ getValue }) => h('span', { class: 'font-mono font-semibold text-brand' }, getValue<string>()),
  },
  {
    accessorKey: 'fijadaEn',
    header: 'Fijada en',
    cell: ({ getValue }) => h('code', { class: 'font-mono text-xs text-muted-foreground' }, getValue<string>()),
  },
  { accessorKey: 'para', header: 'Para qué' },
]

// ── Columnas de la tabla de targets de make ──────────────────────────────────
const BADGE = 'inline-block rounded-md border px-2 py-0.5 font-mono text-xs'
const grupoColor: Record<string, string> = {
  Java: 'border-brand/40 bg-brand/10 text-brand',
  Web: 'border-p2/40 bg-p2/10 text-p2',
  API: 'border-border bg-accent text-foreground',
  Docker: 'border-border bg-accent text-foreground',
}

const targetColumns: ColumnDef<MakeTarget>[] = [
  {
    accessorKey: 'target',
    header: 'make …',
    cell: ({ getValue }) => h('code', { class: 'font-mono text-sm font-semibold text-brand' }, getValue<string>()),
  },
  {
    accessorKey: 'grupo',
    header: 'Capa',
    cell: ({ getValue }) => {
      const g = getValue<string>()
      return h('span', { class: `${BADGE} ${grupoColor[g] ?? ''}` }, g)
    },
  },
  { accessorKey: 'descripcion', header: 'Qué hace' },
]
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="11"
      titulo="Herramientas"
      subtitulo="Las tres ayudas que mantienen el proyecto ordenado y reproducible: mise (versiones), make (atajos) y CI (el robot que revisa)."
    />

    <!-- ── mise ─────────────────────────────────────────────────── -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Boxes class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">mise · versiones por proyecto</h2>
    </div>

    <div data-anim class="mb-6">
      <ExplainCard :simple="miseSimple">
        <template #detalle>
          <p>{{ miseDetalle }}</p>
        </template>
      </ExplainCard>
    </div>

    <div data-anim class="mb-6">
      <DataTable :columns="versionColumns" :data="toolVersions" />
    </div>

    <div data-anim class="mb-6 grid items-start gap-5 lg:grid-cols-2">
      <CodeBlock
        :code="miseSnippet.code"
        :lang="miseSnippet.lang"
        :filename="miseSnippet.filename"
        :player="miseSnippet.player"
      />
      <ScreenshotFrame
        src="/screenshots/mise.png"
        alt="Salida de mise mostrando la versión de Java instalada para el proyecto"
        caption="mise resolviendo el toolchain del proyecto"
        player="p1"
      />
    </div>

    <!-- ── make ─────────────────────────────────────────────────── -->
    <div data-anim class="mb-4 mt-12 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-p2/15 text-p2">
        <Wand2 class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">make · un botón para cada tarea</h2>
    </div>

    <div data-anim class="mb-6">
      <ExplainCard :simple="makeSimple">
        <template #detalle>
          <p>{{ makeDetalle }}</p>
        </template>
      </ExplainCard>
    </div>

    <div data-anim class="mb-6">
      <DataTable :columns="targetColumns" :data="makeTargets" />
    </div>

    <div data-anim class="mb-6 grid items-start gap-5 lg:grid-cols-2">
      <CodeBlock
        :code="makefileSnippet.code"
        :lang="makefileSnippet.lang"
        :filename="makefileSnippet.filename"
        :player="makefileSnippet.player"
      />
      <ScreenshotFrame
        src="/screenshots/make.png"
        alt="Salida de make mostrando la ayuda autogenerada con los targets disponibles"
        caption="make sin argumentos: la ayuda autodocumentada"
        player="p2"
      />
    </div>

    <!-- ── CI ───────────────────────────────────────────────────── -->
    <div data-anim class="mb-4 mt-12 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Bot class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">CI · el robot que revisa cada push</h2>
    </div>

    <div data-anim class="mb-6">
      <ExplainCard :simple="ciSimple">
        <template #detalle>
          <p>{{ ciDetalle }}</p>
        </template>
      </ExplainCard>
    </div>

    <div data-anim class="mb-6 grid gap-4 sm:grid-cols-3">
      <div
        v-for="wf in ciWorkflows"
        :key="wf.archivo"
        class="rounded-xl border border-border bg-card p-4"
      >
        <p class="font-heading text-lg font-semibold text-foreground">{{ wf.nombre }}</p>
        <code class="mt-1 block break-all font-mono text-xs text-brand">{{ wf.archivo }}</code>
        <p class="mt-2 text-sm leading-relaxed text-muted-foreground">{{ wf.descripcion }}</p>
      </div>
    </div>

    <div data-anim>
      <CodeBlock
        :code="ciSnippet.code"
        :lang="ciSnippet.lang"
        :filename="ciSnippet.filename"
        :player="ciSnippet.player"
      />
      <p class="-mt-3 px-1 text-sm text-muted-foreground">{{ ciSnippet.note }}</p>
    </div>
  </section>
</template>
