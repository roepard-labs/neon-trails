<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { Check, Copy } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import { highlight, normalizeLang } from '@/lib/shiki'

interface Props {
  /** Código fuente a resaltar (se muestra tal cual y se copia tal cual). */
  code: string
  /** Lenguaje: acepta IDs de Shiki o alias (`ts`, `dockerfile`, `env`…). */
  lang?: string
  /** Nombre de archivo mostrado en la barra de título (p. ej. `GameLoop.java`). */
  filename?: string
  /** Título alternativo si no es un archivo concreto. */
  title?: string
  /** Color del punto/acento: cyan (P1) o magenta (P2). */
  player?: 'p1' | 'p2'
  /** Numeración de líneas en el canalón izquierdo. */
  showLineNumbers?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  lang: 'bash',
  player: 'p1',
  showLineNumbers: true,
})

// Recortamos saltos de línea finales para que Shiki no genere una línea vacía
// "fantasma" con número al final. Se usa el mismo valor para resaltar y copiar.
const cleanCode = computed(() => props.code.replace(/\n+$/, ''))
const langLabel = computed(() => normalizeLang(props.lang).toUpperCase())

const html = ref('')
const loading = ref(true)
const copied = ref(false)

async function render() {
  loading.value = true
  try {
    html.value = await highlight(cleanCode.value, props.lang)
  } catch (err) {
    console.error('Shiki highlight error:', err)
    html.value = ''
  } finally {
    loading.value = false
  }
}

async function copy() {
  try {
    await navigator.clipboard.writeText(cleanCode.value)
    copied.value = true
    toast.success('Código copiado', { description: props.filename ?? props.title ?? langLabel.value })
    setTimeout(() => (copied.value = false), 1600)
  } catch {
    toast.error('No se pudo copiar al portapapeles')
  }
}

onMounted(render)
watch(() => [props.code, props.lang], render)
</script>

<template>
  <figure
    class="group my-5 overflow-hidden rounded-xl border border-border bg-[#0c0c14] shadow-[var(--nt-shadow-card)]"
  >
    <figcaption class="flex items-center gap-2.5 border-b border-border bg-card/80 px-4 py-2">
      <span
        aria-hidden="true"
        class="size-2.5 shrink-0 rounded-full"
        :class="player === 'p2' ? 'bg-p2 shadow-glow-pink' : 'bg-brand shadow-glow-cyan'"
      />
      <span v-if="filename || title" class="truncate font-mono text-sm text-foreground/90">
        {{ filename ?? title }}
      </span>
      <div class="ml-auto flex items-center gap-2">
        <span
          class="rounded bg-background/70 px-2 py-0.5 font-mono text-[0.7rem] tracking-wider text-muted-foreground"
        >
          {{ langLabel }}
        </span>
        <button
          type="button"
          class="inline-flex items-center gap-1.5 rounded-md border border-border px-2 py-1 font-mono text-xs text-muted-foreground transition-colors hover:border-brand hover:text-brand focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          :aria-label="`Copiar código ${filename ?? langLabel}`"
          @click="copy"
        >
          <component :is="copied ? Check : Copy" class="size-3.5" />
          {{ copied ? 'Copiado' : 'Copiar' }}
        </button>
      </div>
    </figcaption>

    <div class="cb-body overflow-x-auto" :class="{ 'cb-ln': showLineNumbers }">
      <!-- eslint-disable-next-line vue/no-v-html — Shiki escapa el contenido -->
      <div v-if="!loading && html" v-html="html" />
      <pre v-else class="cb-fallback"><code>{{ cleanCode }}</code></pre>
    </div>
  </figure>
</template>

<style scoped>
.cb-body :deep(pre.shiki) {
  margin: 0;
  padding: 0.9rem 1rem;
  /* Anulamos el fondo del tema para que mande el marco neón (#0c0c14). */
  background: transparent !important;
  font-family: var(--font-mono);
  font-size: 0.875rem;
  line-height: 1.65;
  counter-reset: nt-ln;
  tab-size: 2;
}

.cb-body :deep(pre.shiki code) {
  display: block;
  width: max-content;
  min-width: 100%;
}

.cb-ln :deep(.line) {
  counter-increment: nt-ln;
}

/* Canalón de números pegado a la izquierda: sobrevive al scroll horizontal. */
.cb-ln :deep(.line)::before {
  content: counter(nt-ln);
  position: sticky;
  left: 0;
  display: inline-block;
  width: 2rem;
  margin-right: 1.1rem;
  padding-right: 0.6rem;
  text-align: right;
  color: rgba(238, 238, 255, 0.26);
  background: #0c0c14;
  border-right: 1px solid var(--border);
  user-select: none;
}

.cb-fallback {
  margin: 0;
  padding: 0.9rem 1rem;
  font-family: var(--font-mono);
  font-size: 0.875rem;
  line-height: 1.65;
  color: var(--muted-foreground);
  white-space: pre;
}
</style>
