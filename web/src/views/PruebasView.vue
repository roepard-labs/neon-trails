<script setup lang="ts">
import { h } from 'vue'
import type { ColumnDef } from '@tanstack/vue-table'
import { FlaskConical, CheckCheck, ShieldCheck } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ExplainCard from '@/components/content/ExplainCard.vue'
import CodeBlock from '@/components/content/CodeBlock.vue'
import DataTable from '@/components/content/DataTable.vue'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import {
  pruebaSimple,
  pruebaDetalle,
  porQueLogicaSimple,
  porQueLogicaDetalle,
  testFiles,
  assertSnippet,
  aaaSnippet,
  type TestFile,
} from '@/data/tests'

useSectionAnimation()

const testColumns: ColumnDef<TestFile>[] = [
  {
    accessorKey: 'archivo',
    header: 'Archivo de prueba',
    cell: ({ getValue }) => h('code', { class: 'font-mono text-sm font-semibold text-brand' }, getValue<string>()),
  },
  { accessorKey: 'queVerifica', header: 'Qué comprueba' },
]
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="9"
      titulo="Pruebas unitarias"
      subtitulo="Pedirle a la computadora que verifique sola, pieza por pieza, que el juego hace lo que debe — antes de cada entrega."
    />

    <!-- ¿Qué es? -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <FlaskConical class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">¿Qué es una prueba unitaria?</h2>
    </div>
    <div data-anim class="mb-6">
      <ExplainCard :simple="pruebaSimple">
        <template #detalle>
          <p>{{ pruebaDetalle }}</p>
        </template>
      </ExplainCard>
    </div>

    <!-- Ejemplo más simple -->
    <p data-anim class="mb-3 font-mono text-sm uppercase tracking-wider text-muted-foreground">
      El ejemplo más simple: afirmar un hecho
    </p>
    <div data-anim class="mb-8">
      <CodeBlock
        :code="assertSnippet.code"
        :lang="assertSnippet.lang"
        :filename="assertSnippet.filename"
        :player="assertSnippet.player"
      />
      <p class="-mt-3 px-1 text-sm text-muted-foreground">{{ assertSnippet.note }}</p>
    </div>

    <!-- Arrange-Act-Assert -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-p2/15 text-p2">
        <CheckCheck class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">El patrón: preparar → actuar → comprobar</h2>
    </div>
    <div data-anim class="mb-10">
      <CodeBlock
        :code="aaaSnippet.code"
        :lang="aaaSnippet.lang"
        :filename="aaaSnippet.filename"
        :player="aaaSnippet.player"
      />
      <p class="-mt-3 px-1 text-sm text-muted-foreground">{{ aaaSnippet.note }}</p>
    </div>

    <!-- Tabla de los 6 tests -->
    <p data-anim class="mb-3 font-mono text-sm uppercase tracking-wider text-muted-foreground">
      Las 6 pruebas del proyecto (src/test/java/logic/)
    </p>
    <div data-anim class="mb-12">
      <DataTable :columns="testColumns" :data="testFiles" />
    </div>

    <!-- Por qué solo logic/ -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <ShieldCheck class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">¿Por qué solo se prueba la lógica?</h2>
    </div>
    <div data-anim>
      <ExplainCard :simple="porQueLogicaSimple">
        <template #detalle>
          <p>{{ porQueLogicaDetalle }}</p>
        </template>
      </ExplainCard>
    </div>
  </section>
</template>
