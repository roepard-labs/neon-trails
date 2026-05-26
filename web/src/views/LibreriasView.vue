<script setup lang="ts">
import { h } from 'vue'
import type { ColumnDef } from '@tanstack/vue-table'
import { Library, Package, Boxes } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ExplainCard from '@/components/content/ExplainCard.vue'
import CodeBlock from '@/components/content/CodeBlock.vue'
import DataTable from '@/components/content/DataTable.vue'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import {
  libreriaSimple,
  mavenSimple,
  mavenDetalle,
  dependencias,
  plugins,
  pomSnippet,
  type Dependencia,
  type MavenPlugin,
} from '@/data/libraries'

useSectionAnimation()

const depColumns: ColumnDef<Dependencia>[] = [
  {
    accessorKey: 'libreria',
    header: 'Librería',
    cell: ({ getValue }) => h('code', { class: 'font-mono text-sm font-semibold text-brand' }, getValue<string>()),
  },
  {
    accessorKey: 'version',
    header: 'Versión',
    cell: ({ getValue }) => h('span', { class: 'font-mono text-muted-foreground' }, getValue<string>()),
  },
  { accessorKey: 'queEs', header: 'Qué es' },
  { accessorKey: 'paraQue', header: 'Para qué la usamos' },
]

const pluginColumns: ColumnDef<MavenPlugin>[] = [
  {
    accessorKey: 'plugin',
    header: 'Plugin de Maven',
    cell: ({ getValue }) => h('code', { class: 'font-mono text-sm font-semibold text-p2' }, getValue<string>()),
  },
  { accessorKey: 'queHace', header: 'Qué hace' },
]
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="8"
      titulo="Librerías"
      subtitulo="Qué es una librería, qué es Maven y cuáles usa el juego. No reinventamos la rueda: encajamos piezas ya probadas."
    />

    <!-- ¿Qué es una librería? -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Package class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">¿Qué es una librería?</h2>
    </div>
    <div data-anim class="mb-6">
      <ExplainCard :simple="libreriaSimple">
        <template #detalle>
          <p>
            En Java, una librería es un <code class="font-mono text-brand">.jar</code> con clases ya
            escritas. La sumas a tu proyecto como <strong class="text-foreground">dependencia</strong> y
            llamas a sus métodos como si fueran tuyos. El truco está en gestionarlas: ahí entra Maven.
          </p>
        </template>
      </ExplainCard>
    </div>

    <!-- ¿Qué es Maven? -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Library class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">¿Qué es Maven?</h2>
    </div>
    <div data-anim class="mb-6">
      <ExplainCard :simple="mavenSimple">
        <template #detalle>
          <p>{{ mavenDetalle }}</p>
        </template>
      </ExplainCard>
    </div>

    <!-- Dependencias del juego -->
    <p data-anim class="mb-3 font-mono text-sm uppercase tracking-wider text-muted-foreground">
      Las librerías del juego (pom.xml)
    </p>
    <div data-anim class="mb-6">
      <DataTable :columns="depColumns" :data="dependencias" />
    </div>

    <div data-anim class="mb-12">
      <CodeBlock
        :code="pomSnippet.code"
        :lang="pomSnippet.lang"
        :filename="pomSnippet.filename"
        :player="pomSnippet.player"
      />
      <p class="-mt-3 px-1 text-sm text-muted-foreground">{{ pomSnippet.note }}</p>
    </div>

    <!-- Plugins -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-p2/15 text-p2">
        <Boxes class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">Plugins: qué hace Maven por nosotros</h2>
    </div>
    <div data-anim class="mb-6">
      <DataTable :columns="pluginColumns" :data="plugins" />
    </div>

    <p data-anim class="text-sm leading-relaxed text-muted-foreground">
      La web (esta presentación) tiene sus propias librerías —Vue, Vite, Tailwind, Shiki…— gestionadas
      con <code class="font-mono text-brand">pnpm</code> en <code class="font-mono text-brand">web/package.json</code>.
      Es la misma idea de "piezas ya hechas", pero del lado JavaScript (ver la sección Stack &amp; Código).
    </p>
  </section>
</template>
