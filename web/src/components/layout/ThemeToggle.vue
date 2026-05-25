<script setup lang="ts">
import { useColorMode } from '@vueuse/core'
import { Sun, Moon } from 'lucide-vue-next'

const mode = useColorMode({
  attribute: 'class',
  selector: 'html',
  modes: { dark: 'dark', light: '' },
})

function toggle() {
  mode.value = mode.value === 'dark' ? 'light' : 'dark'
}
</script>

<template>
  <button
    type="button"
    :aria-label="mode === 'dark' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
    class="inline-flex h-10 w-10 items-center justify-center rounded-md border border-border bg-background text-muted-foreground transition-colors hover:bg-accent hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
    @click="toggle"
  >
    <Sun v-if="mode === 'dark'" class="size-4" />
    <Moon v-else class="size-4" />
  </button>
</template>
