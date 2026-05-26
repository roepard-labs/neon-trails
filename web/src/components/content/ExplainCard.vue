<script setup lang="ts">
interface Props {
  /** Título opcional de la tarjeta. */
  titulo?: string
  /** Texto de la analogía simple (alternativa al slot `#simple`). */
  simple?: string
}

defineProps<Props>()
</script>

<template>
  <!--
    Tarjeta de "doble nivel": la misma idea explicada dos veces.
    · 🧒 En simple  → analogía cotidiana (acento magenta P2)
    · ⚙️ En detalle → lo técnico real, suele contener un <CodeBlock> (acento cyan P1)
  -->
  <div class="overflow-hidden rounded-xl border border-border bg-card shadow-[var(--nt-shadow-card)]">
    <h3
      v-if="titulo"
      class="border-b border-border px-5 py-3 font-heading text-lg font-semibold text-foreground"
    >
      {{ titulo }}
    </h3>

    <div class="border-l-2 border-p2 bg-p2/5 px-5 py-4">
      <p class="mb-1.5 font-mono text-xs uppercase tracking-wider text-p2">🧒 En simple</p>
      <div class="leading-relaxed text-foreground/90">
        <slot name="simple">{{ simple }}</slot>
      </div>
    </div>

    <div class="border-l-2 border-brand bg-brand/5 px-5 py-4">
      <p class="mb-2 font-mono text-xs uppercase tracking-wider text-brand">⚙️ En detalle</p>
      <div class="space-y-3 leading-relaxed text-foreground/90">
        <slot name="detalle"><slot /></slot>
      </div>
    </div>
  </div>
</template>
