<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import { Menu } from 'lucide-vue-next'
import SidebarNav from './SidebarNav.vue'
import MobileNavSheet from './MobileNavSheet.vue'
import ShortcutsDialog from './ShortcutsDialog.vue'
import CommandPalette from './CommandPalette.vue'
import SlideNavButtons from './SlideNavButtons.vue'
import { Toaster } from '@/components/ui/sonner'
import { sectionRoutes } from '@/router'
import { TooltipProvider } from '@/components/ui/tooltip'
import { useSidebar } from '@/composables/useSidebar'
import { useAppShortcuts } from '@/composables/useAppShortcuts'
import { usePrefersReducedMotion } from '@/composables/usePrefersReducedMotion'
import { usePageTitle } from '@/composables/usePageTitle'
import { usePresenterMode } from '@/composables/usePresenterMode'
import { usePresentationStore } from '@/stores/presentation'

const route = useRoute()
const { isCollapsed, toggleMobile } = useSidebar()
const prefersReducedMotion = usePrefersReducedMotion()
const presentation = usePresentationStore()

useAppShortcuts()
usePageTitle()
usePresenterMode()

const currentSection = computed(() => {
  const r = sectionRoutes.find((sr) => sr.name === route.name)
  return r?.meta?.seccion ?? null
})

const totalSections = sectionRoutes.length
</script>

<template>
  <TooltipProvider :delay-duration="200">
    <div class="relative min-h-screen bg-background text-foreground">
      <SidebarNav />
      <MobileNavSheet />

      <main
        :class="[
          'min-h-screen transition-[padding] duration-200',
          isCollapsed ? 'md:pl-16' : 'md:pl-72',
          'presenter:md:pl-0',
        ]"
      >
        <!-- Mobile topbar -->
        <div
          class="sticky top-0 z-20 flex min-h-12 items-center justify-between gap-2 border-b border-border bg-background/80 px-3 backdrop-blur sm:px-4 md:hidden pt-[env(safe-area-inset-top)]"
        >
          <button
            type="button"
            aria-label="Abrir navegación"
            class="inline-flex h-11 w-11 items-center justify-center rounded-md text-foreground transition-colors hover:bg-accent focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
            @click="toggleMobile"
          >
            <Menu class="size-5" />
          </button>
          <span class="font-heading text-base font-bold text-glow-cyan-soft">Neon Trails</span>
          <!-- Spacer para mantener el título centrado (tema fijo: sin toggle) -->
          <span class="w-11" aria-hidden="true" />
        </div>

        <!-- Breadcrumb + barra de progreso (alimentada por el store Pinia) -->
        <div
          v-if="currentSection"
          class="sticky top-0 z-10 hidden border-b border-border bg-background/80 px-8 py-3 backdrop-blur md:flex md:items-center md:justify-between relative"
        >
          <div class="flex items-center gap-3 text-sm text-muted-foreground">
            <span class="font-mono">
              Sección {{ String(currentSection.numero).padStart(2, '0') }} / {{ String(totalSections).padStart(2, '0') }}
            </span>
            <span class="text-muted-foreground/50">·</span>
            <span class="font-medium text-foreground">{{ currentSection.titulo }}</span>
          </div>
          <span class="font-mono text-xs text-brand">{{ presentation.progress }}%</span>

          <!-- Hilo de neón inferior: avance de la presentación -->
          <div class="pointer-events-none absolute inset-x-0 bottom-0 h-0.5 bg-border/40">
            <div
              class="h-full bg-brand shadow-glow-cyan transition-[width] duration-500 ease-out"
              :style="{ width: presentation.progress + '%' }"
            />
          </div>
        </div>

        <div class="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 sm:py-10 lg:px-12 md:py-12 presenter:max-w-none presenter:px-12 lg:presenter:px-16">
          <RouterView v-slot="{ Component, route: r }">
            <Transition
              v-if="prefersReducedMotion"
              mode="out-in"
              enter-active-class="transition-opacity duration-150"
              leave-active-class="transition-opacity duration-100"
              enter-from-class="opacity-0"
              enter-to-class="opacity-100"
              leave-from-class="opacity-100"
              leave-to-class="opacity-0"
            >
              <component :is="Component" :key="r.fullPath" />
            </Transition>
            <Transition
              v-else
              mode="out-in"
              enter-active-class="transition-all duration-300 ease-out"
              leave-active-class="transition-all duration-200 ease-in"
              enter-from-class="opacity-0 translate-y-2"
              enter-to-class="opacity-100 translate-y-0"
              leave-from-class="opacity-100 translate-y-0"
              leave-to-class="opacity-0 -translate-y-1"
            >
              <component :is="Component" :key="r.fullPath" />
            </Transition>
          </RouterView>
        </div>
      </main>

      <!-- Global keyboard shortcuts modal -->
      <ShortcutsDialog />

      <!-- Command palette (Ctrl/⌘+K) -->
      <CommandPalette />

      <!-- Navegación de slides: botones flotantes atrás / adelante (abajo-derecha) -->
      <SlideNavButtons />

      <!-- Notificaciones (vue-sonner): usadas por el botón "copiar" de los CodeBlock.
           Va arriba a la derecha para no chocar con los botones de navegación. -->
      <Toaster position="top-right" :duration="2200" />
    </div>
  </TooltipProvider>
</template>
