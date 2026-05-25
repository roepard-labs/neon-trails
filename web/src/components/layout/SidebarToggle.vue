<script setup lang="ts">
import { PanelLeftClose, PanelLeftOpen } from 'lucide-vue-next'
import { useSidebar } from '@/composables/useSidebar'

const { isCollapsed, toggle } = useSidebar()
</script>

<template>
  <button
    type="button"
    :aria-label="isCollapsed ? 'Expandir barra lateral' : 'Colapsar barra lateral'"
    class="inline-flex h-9 w-9 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-accent hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
    @click="toggle"
  >
    <PanelLeftClose v-if="!isCollapsed" class="size-4" />
    <PanelLeftOpen v-else class="size-4" />
  </button>
</template>
