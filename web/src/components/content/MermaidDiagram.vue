<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import mermaid from 'mermaid'
import { useDiagramDraw } from '@/composables/useDiagramDraw'

interface Props {
  source: string
  id?: string
  ariaLabel?: string
  animate?: boolean
}

const props = withDefaults(defineProps<Props>(), { animate: true })

const host = ref<HTMLElement | null>(null)
const rendered = ref<string>('')

const diagramId = computed(() => `mmd-${props.id ?? Math.random().toString(36).slice(2, 9)}`)

const draw = useDiagramDraw(host)

// Tema neón fijo (la presentación es dark permanente): cyan en líneas/bordes,
// fondos oscuros de tarjeta y texto claro, para que los diagramas combinen con
// la arena Tron en lugar de verse como un bloque blanco.
const NEON_THEME_VARS = {
  darkMode: true,
  fontFamily: 'Inter, system-ui, sans-serif',
  background: '#0a0a12',
  mainBkg: '#12121c',
  primaryColor: '#12121c',
  primaryTextColor: '#eeeeff',
  primaryBorderColor: '#00ffff',
  secondaryColor: '#1b1b29',
  tertiaryColor: '#101019',
  lineColor: '#33ffff',
  textColor: '#eeeeff',
  nodeBorder: '#00ffff',
  nodeTextColor: '#eeeeff',
  clusterBkg: '#101019',
  clusterBorder: 'rgba(51,255,255,0.30)',
  titleColor: '#00ffff',
  edgeLabelBackground: '#0a0a12',
  // Diagramas de secuencia
  actorBkg: '#12121c',
  actorBorder: '#00ffff',
  actorTextColor: '#eeeeff',
  signalColor: '#eeeeff',
  signalTextColor: '#eeeeff',
  labelBoxBkgColor: '#12121c',
  labelBoxBorderColor: '#00ffff',
  labelTextColor: '#eeeeff',
  noteBkgColor: '#1b1b29',
  noteTextColor: '#eeeeff',
  noteBorderColor: 'rgba(255,51,153,0.55)',
}

async function render() {
  if (!host.value) return
  mermaid.initialize({
    startOnLoad: false,
    theme: 'dark',
    themeVariables: NEON_THEME_VARS,
    securityLevel: 'loose',
    fontFamily: 'Inter, system-ui, sans-serif',
    flowchart: {
      htmlLabels: true,
      curve: 'basis',
      padding: 12,
    },
  })
  try {
    const { svg } = await mermaid.render(diagramId.value, props.source)
    rendered.value = svg
    if (props.animate) await draw()
  } catch (err) {
    console.error('Mermaid render error:', err)
    rendered.value = `<pre class="text-destructive text-xs whitespace-pre-wrap">${(err as Error).message}</pre>`
  }
}

onMounted(render)
watch(() => props.source, render)
</script>

<template>
  <div
    ref="host"
    tabindex="0"
    class="mermaid-host flex w-full justify-center overflow-x-auto rounded-lg border border-border bg-card p-3 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background sm:p-6"
    :aria-label="ariaLabel"
    role="img"
    v-html="rendered"
  />
</template>
