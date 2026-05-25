<script setup lang="ts">
import { Presentation, PresentationIcon } from 'lucide-vue-next'
import { usePresenterMode } from '@/composables/usePresenterMode'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip'

const { isPresenter, toggle } = usePresenterMode()
</script>

<template>
  <Tooltip>
    <TooltipTrigger as-child>
      <button
        type="button"
        :aria-label="isPresenter ? 'Salir del modo expositor' : 'Activar modo expositor'"
        :aria-pressed="isPresenter"
        class="relative inline-flex h-10 w-10 items-center justify-center rounded-md border transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
        :class="
          isPresenter
            ? 'border-brand bg-brand text-brand-foreground hover:bg-brand/90'
            : 'border-border bg-background text-muted-foreground hover:bg-accent hover:text-foreground'
        "
        @click="toggle"
      >
        <PresentationIcon v-if="isPresenter" class="size-4" />
        <Presentation v-else class="size-4" />
        <span
          v-if="isPresenter"
          class="absolute -bottom-1 -right-1 inline-flex h-3.5 min-w-3.5 items-center justify-center rounded-sm bg-foreground px-1 font-mono text-[8px] font-bold text-background"
          aria-hidden="true"
        >
          ON
        </span>
      </button>
    </TooltipTrigger>
    <TooltipContent side="right">
      <span class="text-xs">
        {{ isPresenter ? 'Modo expositor: ON' : 'Modo expositor' }}
      </span>
    </TooltipContent>
  </Tooltip>
</template>
