<script setup lang="ts" generic="T">
import { ref } from 'vue'
import {
  FlexRender,
  getCoreRowModel,
  getSortedRowModel,
  useVueTable,
  type ColumnDef,
  type SortingState,
  type Updater,
} from '@tanstack/vue-table'
import { ArrowUpDown } from 'lucide-vue-next'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

// Tabla genérica con orden por columna: encapsula la cabecera headless de
// @tanstack/vue-table sobre los componentes shadcn (Herramientas la usa para
// las versiones del toolchain y los targets de make).
const props = defineProps<{ columns: ColumnDef<T>[]; data: T[] }>()

const sorting = ref<SortingState>([])

const table = useVueTable({
  get data() {
    return props.data
  },
  get columns() {
    return props.columns
  },
  state: {
    get sorting() {
      return sorting.value
    },
  },
  onSortingChange: (updater: Updater<SortingState>) => {
    sorting.value = typeof updater === 'function' ? updater(sorting.value) : updater
  },
  getCoreRowModel: getCoreRowModel(),
  getSortedRowModel: getSortedRowModel(),
})
</script>

<template>
  <div class="overflow-hidden rounded-xl border border-border bg-card">
    <Table>
      <TableHeader>
        <TableRow
          v-for="hg in table.getHeaderGroups()"
          :key="hg.id"
          class="border-border hover:bg-transparent"
        >
          <TableHead v-for="header in hg.headers" :key="header.id" class="text-foreground">
            <button
              v-if="header.column.getCanSort()"
              type="button"
              class="inline-flex items-center gap-1.5 font-semibold transition-colors hover:text-brand focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              @click="header.column.getToggleSortingHandler()?.($event)"
            >
              <FlexRender :render="header.column.columnDef.header" :props="header.getContext()" />
              <ArrowUpDown class="size-3.5 opacity-60" />
            </button>
            <FlexRender
              v-else-if="!header.isPlaceholder"
              :render="header.column.columnDef.header"
              :props="header.getContext()"
            />
          </TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        <TableRow
          v-for="row in table.getRowModel().rows"
          :key="row.id"
          class="border-border transition-colors hover:bg-brand/5"
        >
          <TableCell v-for="cell in row.getVisibleCells()" :key="cell.id" class="align-top">
            <FlexRender
              v-if="cell.column.columnDef.cell"
              :render="cell.column.columnDef.cell"
              :props="cell.getContext()"
            />
            <template v-else>{{ cell.getValue() }}</template>
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </div>
</template>
