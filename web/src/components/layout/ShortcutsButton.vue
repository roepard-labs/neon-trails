<script setup lang="ts">
import { Keyboard } from 'lucide-vue-next'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip'
import { isShortcutsOpen } from '@/composables/useAppShortcuts'
</script>

<template>
  <Tooltip>
    <TooltipTrigger as-child>
      <button
        type="button"
        aria-label="Ver atajos de teclado"
        class="inline-flex h-10 w-10 items-center justify-center rounded-md border border-border bg-background text-muted-foreground transition-colors hover:bg-accent hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
        @click="isShortcutsOpen = true"
      >
        <Keyboard class="size-4" />
      </button>
    </TooltipTrigger>
    <TooltipContent side="top">
      <span class="inline-flex items-center gap-1.5">
        Atajos
        <kbd class="rounded border border-border bg-muted px-1 font-mono text-[10px]">?</kbd>
      </span>
    </TooltipContent>
  </Tooltip>
</template>
