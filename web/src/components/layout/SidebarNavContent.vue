<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import {
  BookOpenText,
  FileQuestion,
  ListChecks,
  Workflow,
  NotebookText,
  Network,
  MonitorSmartphone,
  Trophy,
  Gamepad2,
  type LucideIcon,
} from 'lucide-vue-next'
import { sectionRoutes } from '@/router'
import { projectInfo } from '@/data/project'
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip'
import ThemeToggle from './ThemeToggle.vue'
import ShortcutsButton from './ShortcutsButton.vue'
import SidebarToggle from './SidebarToggle.vue'
import PresenterToggle from './PresenterToggle.vue'

interface Props {
  collapsed?: boolean
  showToggle?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  collapsed: false,
  showToggle: true,
})

const iconMap: Record<string, LucideIcon> = {
  BookOpenText,
  FileQuestion,
  ListChecks,
  Workflow,
  NotebookText,
  Network,
  MonitorSmartphone,
  Trophy,
}

const route = useRoute()

const sections = computed(() =>
  sectionRoutes
    .filter((r) => r.meta?.seccion)
    .map((r) => ({
      name: r.name as string,
      path: r.path as string,
      meta: r.meta!.seccion!,
    })),
)

const activeName = computed(() => route.name)
</script>

<template>
  <div class="flex h-full min-h-0 flex-col">
    <!-- Brand -->
    <div
      :class="[
        'flex items-center gap-3 border-b border-border py-5',
        props.collapsed ? 'justify-center px-2' : 'px-6',
      ]"
    >
      <span
        class="flex size-9 shrink-0 items-center justify-center rounded-md bg-brand text-brand-foreground"
      >
        <Gamepad2 class="size-5" />
      </span>
      <div v-if="!props.collapsed" class="flex min-w-0 flex-1 flex-col">
        <span class="truncate font-heading text-base font-bold leading-tight">
          {{ projectInfo.titulo }}
        </span>
        <span class="text-xs uppercase tracking-wider text-muted-foreground">
          {{ projectInfo.asignatura }}
        </span>
      </div>
      <SidebarToggle v-if="!props.collapsed && props.showToggle" />
    </div>

    <!-- Toggle solo (cuando colapsado) -->
    <div
      v-if="props.collapsed && props.showToggle"
      class="flex justify-center border-b border-border p-1.5"
    >
      <SidebarToggle />
    </div>

    <!-- Section nav -->
    <nav
      :class="[
        'flex-1 overflow-y-auto py-4',
        props.collapsed ? 'px-2' : 'px-3',
      ]"
    >
      <p
        v-if="!props.collapsed"
        class="px-3 pb-2 text-xs font-semibold uppercase tracking-[0.18em] text-muted-foreground"
      >
        Secciones
      </p>

      <ul class="flex flex-col gap-0.5">
        <li v-for="s in sections" :key="s.name">
          <!-- Variante colapsada con Tooltip -->
          <Tooltip v-if="props.collapsed">
            <TooltipTrigger as-child>
              <RouterLink
                :to="s.path"
                class="group relative flex items-center justify-center rounded-md px-2 py-2.5 transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
                :class="{
                  'bg-sidebar-accent text-sidebar-accent-foreground': activeName === s.name,
                }"
              >
                <Transition
                  enter-active-class="transition duration-300 ease-out"
                  enter-from-class="opacity-0 -translate-x-1"
                  enter-to-class="opacity-100 translate-x-0"
                  leave-active-class="transition duration-150 ease-in"
                  leave-from-class="opacity-100"
                  leave-to-class="opacity-0"
                >
                  <span
                    v-if="activeName === s.name"
                    class="pointer-events-none absolute left-0 top-1/2 h-6 w-0.5 -translate-y-1/2 rounded-r-full bg-brand"
                    aria-hidden="true"
                  />
                </Transition>
                <span
                  class="inline-flex size-8 items-center justify-center rounded text-xs font-mono font-semibold"
                  :class="
                    activeName === s.name
                      ? 'bg-brand text-brand-foreground'
                      : 'bg-muted text-muted-foreground group-hover:bg-brand/15 group-hover:text-brand'
                  "
                >
                  {{ String(s.meta.numero).padStart(2, '0') }}
                </span>
              </RouterLink>
            </TooltipTrigger>
            <TooltipContent side="right" class="max-w-xs">
              <div class="flex items-center gap-2">
                <component :is="iconMap[s.meta.icon] ?? BookOpenText" class="size-3.5 text-brand" />
                <span class="text-xs font-semibold">{{ s.meta.titulo }}</span>
              </div>
              <p class="mt-0.5 text-xs text-muted-foreground">
                {{ s.meta.subtitulo }}
              </p>
            </TooltipContent>
          </Tooltip>

          <!-- Variante expandida -->
          <RouterLink
            v-else
            :to="s.path"
            class="group relative flex items-start gap-3 rounded-md px-3 py-2.5 text-sm transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
            :class="{
              'bg-sidebar-accent text-sidebar-accent-foreground font-medium': activeName === s.name,
            }"
          >
            <Transition
              enter-active-class="transition duration-300 ease-out"
              enter-from-class="opacity-0 -translate-x-1"
              enter-to-class="opacity-100 translate-x-0"
              leave-active-class="transition duration-150 ease-in"
              leave-from-class="opacity-100"
              leave-to-class="opacity-0"
            >
              <span
                v-if="activeName === s.name"
                class="pointer-events-none absolute left-0 top-1/2 h-6 w-0.5 -translate-y-1/2 rounded-r-full bg-brand"
                aria-hidden="true"
              />
            </Transition>
            <span
              class="mt-0.5 inline-flex size-8 shrink-0 items-center justify-center rounded text-xs font-mono font-semibold"
              :class="
                activeName === s.name
                  ? 'bg-brand text-brand-foreground'
                  : 'bg-muted text-muted-foreground group-hover:bg-brand/15 group-hover:text-brand'
              "
            >
              {{ String(s.meta.numero).padStart(2, '0') }}
            </span>
            <span class="flex flex-col leading-tight">
              <span class="flex items-center gap-2">
                <component :is="iconMap[s.meta.icon] ?? BookOpenText" class="size-3.5" />
                <span>{{ s.meta.titulo }}</span>
              </span>
              <span class="mt-0.5 text-xs text-muted-foreground">
                {{ s.meta.subtitulo }}
              </span>
            </span>
          </RouterLink>
        </li>
      </ul>
    </nav>

    <!-- Footer -->
    <div
      :class="[
        'border-t border-border',
        props.collapsed
          ? 'flex flex-col items-center gap-2 p-2'
          : 'flex flex-col gap-3 px-4 py-4',
      ]"
    >
      <template v-if="props.collapsed">
        <PresenterToggle />
        <ShortcutsButton />
        <ThemeToggle />
      </template>

      <template v-else>
        <div class="flex items-center justify-between">
          <span class="text-xs text-muted-foreground">Expositor · Atajos · Tema</span>
          <div class="flex items-center gap-1.5">
            <PresenterToggle />
            <ShortcutsButton />
            <ThemeToggle />
          </div>
        </div>
        <p class="text-xs leading-relaxed text-muted-foreground">
          {{ projectInfo.universidad }} · {{ projectInfo.ano }}
        </p>
      </template>
    </div>
  </div>
</template>
