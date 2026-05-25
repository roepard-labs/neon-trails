<script setup lang="ts">
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet'
import SidebarNavContent from './SidebarNavContent.vue'
import { useSidebar } from '@/composables/useSidebar'

const route = useRoute()
const { isMobileOpen, closeMobile } = useSidebar()

watch(
  () => route.fullPath,
  () => {
    if (isMobileOpen.value) closeMobile()
  },
)
</script>

<template>
  <Sheet
    :open="isMobileOpen"
    @update:open="(v: boolean) => { isMobileOpen = v }"
  >
    <SheetContent
      side="left"
      class="w-72 max-w-[85vw] gap-0 border-r border-border bg-sidebar p-0 text-sidebar-foreground pt-[env(safe-area-inset-top)] pb-[env(safe-area-inset-bottom)] md:hidden"
    >
      <SheetHeader class="sr-only">
        <SheetTitle>Navegación principal</SheetTitle>
        <SheetDescription>
          Lista de secciones del documento del proyecto final de Teoría de Sistemas.
        </SheetDescription>
      </SheetHeader>
      <SidebarNavContent :collapsed="false" :show-toggle="false" />
    </SheetContent>
  </Sheet>
</template>
