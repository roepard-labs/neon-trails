<script setup lang="ts">
import { h } from 'vue'
import type { ColumnDef } from '@tanstack/vue-table'
import { Github, FolderTree, BookText, ExternalLink } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ExplainCard from '@/components/content/ExplainCard.vue'
import CodeBlock from '@/components/content/CodeBlock.vue'
import DataTable from '@/components/content/DataTable.vue'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import {
  repoUrl,
  githubSimple,
  githubDetalle,
  docsSimple,
  docsDetalle,
  docFiles,
  treeSnippet,
  type DocFile,
} from '@/data/docs'

useSectionAnimation()

const docColumns: ColumnDef<DocFile>[] = [
  {
    accessorKey: 'archivo',
    header: 'Archivo',
    cell: ({ getValue }) => h('code', { class: 'font-mono text-sm font-semibold text-brand' }, getValue<string>()),
  },
  { accessorKey: 'queContiene', header: 'Qué contiene' },
]
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="12"
      titulo="Documentación & GitHub"
      subtitulo="Dónde vive el código y cómo se documenta para que cualquiera pueda entenderlo, ejecutarlo y continuarlo."
    />

    <!-- GitHub -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Github class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">GitHub: el código en la nube</h2>
    </div>
    <div data-anim class="mb-4">
      <ExplainCard :simple="githubSimple">
        <template #detalle>
          <p>{{ githubDetalle }}</p>
        </template>
      </ExplainCard>
    </div>
    <div data-anim class="mb-12">
      <a
        :href="repoUrl"
        target="_blank"
        rel="noopener"
        class="inline-flex items-center gap-2 rounded-lg border border-border bg-card px-4 py-2.5 font-mono text-sm text-foreground transition-colors hover:border-brand hover:text-brand focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
      >
        <Github class="size-4" />
        roepard-labs/neon-trails
        <ExternalLink class="size-3.5 opacity-60" />
      </a>
    </div>

    <!-- Árbol del repo -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-p2/15 text-p2">
        <FolderTree class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">Cómo está organizado</h2>
    </div>
    <div data-anim class="mb-12">
      <CodeBlock
        :code="treeSnippet.code"
        :lang="treeSnippet.lang"
        :filename="treeSnippet.filename"
        :player="treeSnippet.player"
        :show-line-numbers="false"
      />
      <p class="-mt-3 px-1 text-sm text-muted-foreground">{{ treeSnippet.note }}</p>
    </div>

    <!-- Documentación -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <BookText class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">La documentación escrita</h2>
    </div>
    <div data-anim class="mb-6">
      <ExplainCard :simple="docsSimple">
        <template #detalle>
          <p>{{ docsDetalle }}</p>
        </template>
      </ExplainCard>
    </div>
    <div data-anim>
      <DataTable :columns="docColumns" :data="docFiles" />
    </div>
  </section>
</template>
