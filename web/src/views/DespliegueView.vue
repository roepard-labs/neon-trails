<script setup lang="ts">
import { Container, MonitorPlay, Rocket, Workflow } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import ExplainCard from '@/components/content/ExplainCard.vue'
import CodeBlock from '@/components/content/CodeBlock.vue'
import StepList from '@/components/content/StepList.vue'
import ScreenshotFrame from '@/components/content/ScreenshotFrame.vue'
import MermaidDiagram from '@/components/content/MermaidDiagram.vue'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import {
  dockerSimple,
  vncSimple,
  dokploySimple,
  buildMermaid,
  runtimeMermaid,
  dockerfileSnippet,
  composeSnippet,
  nginxSnippet,
  supervisordSnippet,
  envSnippet,
  dokploySteps,
} from '@/data/deploy'

useSectionAnimation()
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="10"
      titulo="Despliegue"
      subtitulo="Una sola imagen Docker que trae las tres capas y el juego jugable en el navegador, lista para subir a Dokploy."
    />

    <!-- ── 1) Docker ─────────────────────────────────────────────── -->
    <div data-anim class="mb-4 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Container class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">La caja: Docker multi-stage</h2>
    </div>

    <div data-anim class="mb-6">
      <ExplainCard :simple="dockerSimple">
        <template #detalle>
          <p>
            El <code class="font-mono text-brand">Dockerfile</code> tiene 4 etapas. Las tres primeras
            son "talleres" desechables (Node, Composer, Maven) que solo producen un artefacto cada una;
            la cuarta (<strong class="text-foreground">runtime</strong>) los junta. Así la imagen final
            no carga las herramientas de build.
          </p>
        </template>
      </ExplainCard>
    </div>

    <div data-anim class="mb-6 rounded-xl border border-border bg-card p-4 sm:p-6">
      <p class="mb-3 font-mono text-xs uppercase tracking-wider text-muted-foreground">
        3 talleres → 1 imagen
      </p>
      <MermaidDiagram
        :source="buildMermaid"
        id="deploy-build"
        aria-label="Build multi-stage del Dockerfile de Neon Trails"
      />
    </div>

    <div data-anim>
      <CodeBlock
        :code="dockerfileSnippet.code"
        :lang="dockerfileSnippet.lang"
        :filename="dockerfileSnippet.filename"
        :player="dockerfileSnippet.player"
      />
      <p class="-mt-3 mb-5 px-1 text-sm text-muted-foreground">{{ dockerfileSnippet.note }}</p>
      <CodeBlock
        :code="composeSnippet.code"
        :lang="composeSnippet.lang"
        :filename="composeSnippet.filename"
        :player="composeSnippet.player"
      />
      <p class="-mt-3 mb-2 px-1 text-sm text-muted-foreground">{{ composeSnippet.note }}</p>
    </div>

    <!-- ── 2) VNC / noVNC ────────────────────────────────────────── -->
    <div data-anim class="mb-4 mt-12 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-p2/15 text-p2">
        <MonitorPlay class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">El juego en el navegador: noVNC</h2>
    </div>

    <div data-anim class="mb-6">
      <ExplainCard :simple="vncSimple">
        <template #detalle>
          <p>
            Dentro del contenedor, <strong class="text-foreground">supervisord</strong> arranca y vigila
            5 procesos. Un servidor X virtual (TigerVNC) dibuja el juego; websockify lo convierte en
            WebSocket; y nginx lo publica en <code class="font-mono text-p2">/game/</code>. Tus teclas
            viajan por ese mismo canal.
          </p>
        </template>
      </ExplainCard>
    </div>

    <div data-anim class="mb-6 rounded-xl border border-border bg-card p-4 sm:p-6">
      <p class="mb-3 font-mono text-xs uppercase tracking-wider text-muted-foreground">
        supervisord (PID 1) → 5 procesos
      </p>
      <MermaidDiagram
        :source="runtimeMermaid"
        id="deploy-runtime"
        aria-label="Procesos del runtime orquestados por supervisord"
      />
    </div>

    <div data-anim class="mb-6">
      <CodeBlock
        :code="nginxSnippet.code"
        :lang="nginxSnippet.lang"
        :filename="nginxSnippet.filename"
        :player="nginxSnippet.player"
      />
      <p class="-mt-3 mb-5 px-1 text-sm text-muted-foreground">{{ nginxSnippet.note }}</p>
      <CodeBlock
        :code="supervisordSnippet.code"
        :lang="supervisordSnippet.lang"
        :filename="supervisordSnippet.filename"
        :player="supervisordSnippet.player"
      />
      <p class="-mt-3 mb-2 px-1 text-sm text-muted-foreground">{{ supervisordSnippet.note }}</p>
    </div>

    <div data-anim class="mb-6">
      <ScreenshotFrame
        src="/screenshots/vnc-docker.png"
        alt="El juego Neon Trails corriendo dentro de Docker, jugable en el navegador vía noVNC"
        caption="El juego Swing, jugándose en una pestaña del navegador (noVNC)"
        player="p2"
      />
    </div>

    <!-- ── 3) Dokploy ────────────────────────────────────────────── -->
    <div data-anim class="mb-4 mt-12 flex items-center gap-3">
      <span class="flex size-11 items-center justify-center rounded-xl bg-brand/15 text-brand">
        <Rocket class="size-6" />
      </span>
      <h2 class="font-heading text-2xl font-bold text-foreground">A la nube: Dokploy</h2>
    </div>

    <div data-anim class="mb-6">
      <ExplainCard :simple="dokploySimple">
        <template #detalle>
          <p>
            Dokploy construye desde el mismo <code class="font-mono text-brand">Dockerfile</code> y
            expone el puerto <strong class="text-foreground">80</strong>; Traefik le pone el HTTPS.
            La base de datos (MariaDB 11) es un servicio aparte. Estos son los pasos:
          </p>
        </template>
      </ExplainCard>
    </div>

    <div data-anim class="mb-6 flex items-center gap-2">
      <Workflow class="size-5 text-brand" />
      <h3 class="font-heading text-lg font-semibold text-foreground">Pasos en Dokploy</h3>
    </div>
    <div data-anim class="mb-8">
      <StepList :steps="dokploySteps" />
    </div>

    <div data-anim>
      <CodeBlock
        :code="envSnippet.code"
        :lang="envSnippet.lang"
        :filename="envSnippet.filename"
        :player="envSnippet.player"
      />
      <p class="-mt-3 px-1 text-sm text-muted-foreground">{{ envSnippet.note }}</p>
    </div>
  </section>
</template>
