<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useColorMode } from '@vueuse/core'
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
const mode = useColorMode()

const diagramId = computed(() => `mmd-${props.id ?? Math.random().toString(36).slice(2, 9)}`)

const draw = useDiagramDraw(host)

async function render() {
  if (!host.value) return
  const theme = mode.value === 'dark' ? 'dark' : 'default'
  mermaid.initialize({
    startOnLoad: false,
    theme,
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
watch(() => [props.source, mode.value], render)
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
