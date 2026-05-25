<script setup lang="ts">
import SidebarNavContent from './SidebarNavContent.vue'
import { useSidebar } from '@/composables/useSidebar'

const { isCollapsed } = useSidebar()
</script>

<template>
  <aside
    :class="[
      'fixed inset-y-0 left-0 z-30 hidden flex-col border-r border-border bg-sidebar text-sidebar-foreground transition-[width] duration-200 md:flex',
      isCollapsed ? 'w-16' : 'w-72',
    ]"
  >
    <SidebarNavContent :collapsed="isCollapsed" />
  </aside>
</template>
