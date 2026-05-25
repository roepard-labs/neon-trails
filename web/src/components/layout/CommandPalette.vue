<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  BookOpenText,
  FileQuestion,
  ListChecks,
  Workflow,
  NotebookText,
  Network,
  MonitorSmartphone,
  Hash,
  Sun,
  Presentation,
  type LucideIcon,
} from 'lucide-vue-next'
import {
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from '@/components/ui/command'
import { sectionRoutes } from '@/router'
import { useCases } from '@/data/useCases'
import { isCommandOpen } from '@/composables/useAppShortcuts'
import { usePresenterMode } from '@/composables/usePresenterMode'
import { useColorMode } from '@vueuse/core'

const router = useRouter()
const { toggle: togglePresenter } = usePresenterMode()
const mode = useColorMode({
  attribute: 'class',
  selector: 'html',
  modes: { dark: 'dark', light: '' },
})

const iconMap: Record<string, LucideIcon> = {
  BookOpenText,
  FileQuestion,
  ListChecks,
  Workflow,
  NotebookText,
  Network,
  MonitorSmartphone,
}

const sections = sectionRoutes
  .filter((r) => r.meta?.seccion)
  .map((r) => ({
    name: r.name as string,
    path: r.path as string,
    titulo: r.meta!.seccion!.titulo,
    subtitulo: r.meta!.seccion!.subtitulo,
    icon: r.meta!.seccion!.icon,
    numero: r.meta!.seccion!.numero,
  }))

function go(path: string) {
  isCommandOpen.value = false
  router.push(path)
}

function goSection(path: string) {
  go(path)
}

function goUseCase(codigo: string) {
  isCommandOpen.value = false
  router.push(`/descripciones#${codigo}`)
}

function toggleTheme() {
  mode.value = mode.value === 'dark' ? 'light' : 'dark'
  isCommandOpen.value = false
}

function togglePresenterAction() {
  togglePresenter()
  isCommandOpen.value = false
}
</script>

<template>
  <CommandDialog v-model:open="isCommandOpen">
    <CommandInput placeholder="Buscar sección, caso de uso o acción…" />
    <CommandList>
      <CommandEmpty>Sin resultados.</CommandEmpty>

      <CommandGroup heading="Secciones">
        <CommandItem
          v-for="s in sections"
          :key="s.name"
          :value="`${s.numero} ${s.titulo} ${s.subtitulo}`"
          @select="goSection(s.path)"
        >
          <component :is="iconMap[s.icon] ?? BookOpenText" class="mr-2 size-4 text-brand" />
          <span class="font-medium">{{ s.titulo }}</span>
          <span class="ml-2 text-muted-foreground">— {{ s.subtitulo }}</span>
        </CommandItem>
      </CommandGroup>

      <CommandSeparator />

      <CommandGroup heading="Casos de uso">
        <CommandItem
          v-for="uc in useCases"
          :key="uc.codigo"
          :value="`${uc.codigo} ${uc.nombre} ${uc.descripcion}`"
          @select="goUseCase(uc.codigo)"
        >
          <Hash class="mr-2 size-4 text-brand" />
          <span class="font-mono text-sm font-semibold">{{ uc.codigo }}</span>
          <span class="ml-3">{{ uc.nombre }}</span>
        </CommandItem>
      </CommandGroup>

      <CommandSeparator />

      <CommandGroup heading="Acciones">
        <CommandItem value="modo expositor presenter beamer" @select="togglePresenterAction">
          <Presentation class="mr-2 size-4 text-brand" />
          Activar/desactivar modo expositor
        </CommandItem>
        <CommandItem value="tema oscuro claro dark light" @select="toggleTheme">
          <Sun class="mr-2 size-4 text-brand" />
          Alternar tema claro / oscuro
        </CommandItem>
      </CommandGroup>
    </CommandList>
  </CommandDialog>
</template>
