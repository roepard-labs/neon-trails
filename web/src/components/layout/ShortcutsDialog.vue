<script setup lang="ts">
import { computed } from 'vue'
import { useShortcutList } from 'vue-shortcut-manager'
import { Keyboard, Smartphone } from 'lucide-vue-next'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { isShortcutsOpen } from '@/composables/useAppShortcuts'
import { useBreakpoint } from '@/composables/useBreakpoint'

const { shortcuts } = useShortcutList()
const { isMobile } = useBreakpoint()

const groupOrder = ['Navegación', 'Saltos rápidos', 'Interfaz']

const grouped = computed(() => {
  const buckets = new Map<string, typeof shortcuts.value>()
  for (const s of shortcuts.value) {
    const scope = s.scope ?? 'Otros'
    if (!buckets.has(scope)) buckets.set(scope, [])
    buckets.get(scope)!.push(s)
  }
  const ordered = groupOrder
    .filter((g) => buckets.has(g))
    .map((g) => ({ scope: g, items: buckets.get(g)! }))
  // Anything not in the predefined order goes at the end
  for (const [scope, items] of buckets) {
    if (!groupOrder.includes(scope)) ordered.push({ scope, items })
  }
  return ordered
})

const symbolMap: Record<string, string> = {
  arrowright: '→',
  arrowleft: '←',
  arrowup: '↑',
  arrowdown: '↓',
  shift: 'Shift',
  ctrl: 'Ctrl',
  alt: 'Alt',
  meta: '⌘',
  escape: 'Esc',
  enter: '↵',
  space: '␣',
  slash: '/',
  backslash: '\\',
}

function formatKey(key: string): string[] {
  const normalized = key.toLowerCase().trim()
  // Casos especiales con visualización amigable
  if (normalized === 'shift+slash') return ['?']
  if (normalized === 'shift+backslash') return ['|']

  return normalized.split('+').map((part) => {
    if (symbolMap[part]) return symbolMap[part]
    // Mayúscula para teclas alfa
    return part.length === 1 ? part.toUpperCase() : part.charAt(0).toUpperCase() + part.slice(1)
  })
}
</script>

<template>
  <Dialog v-model:open="isShortcutsOpen">
    <DialogContent class="max-w-xl">
      <DialogHeader>
        <DialogTitle class="flex items-center gap-2">
          <span class="flex size-7 items-center justify-center rounded-md bg-brand/15 text-brand">
            <Keyboard class="size-4" />
          </span>
          Atajos de teclado
        </DialogTitle>
        <DialogDescription>
          Útiles para sustentar el proyecto con un teclado Bluetooth en la mano.
          Pulsa <kbd class="rounded border border-border bg-muted px-1.5 py-0.5 font-mono text-[11px]">?</kbd>
          en cualquier momento para abrir esta ayuda.
        </DialogDescription>
      </DialogHeader>

      <div
        v-if="isMobile"
        class="flex items-start gap-3 rounded-md border border-border bg-accent/40 p-3 text-xs leading-relaxed text-muted-foreground"
      >
        <Smartphone class="mt-0.5 size-4 shrink-0 text-brand" />
        <p>
          Atajos de teclado disponibles en escritorio. Usa el menú lateral para navegar.
        </p>
      </div>

      <div class="-mx-2 max-h-[60vh] space-y-5 overflow-y-auto px-2">
        <section v-for="group in grouped" :key="group.scope">
          <h3 class="mb-2 font-mono text-[10px] uppercase tracking-[0.18em] text-muted-foreground">
            {{ group.scope }}
          </h3>
          <ul class="space-y-1.5">
            <li
              v-for="s in group.items"
              :key="s.id"
              class="flex items-center justify-between gap-4 rounded-md px-3 py-2 transition-colors hover:bg-accent/40"
            >
              <span class="text-sm">{{ s.description ?? s.key }}</span>
              <span class="flex shrink-0 items-center gap-1">
                <kbd
                  v-for="(part, i) in formatKey(s.key)"
                  :key="i"
                  class="inline-flex min-w-7 items-center justify-center rounded-md border border-border bg-muted px-1.5 py-0.5 font-mono text-[11px] font-semibold text-foreground shadow-sm"
                >
                  {{ part }}
                </kbd>
              </span>
            </li>
          </ul>
        </section>
      </div>
    </DialogContent>
  </Dialog>
</template>
