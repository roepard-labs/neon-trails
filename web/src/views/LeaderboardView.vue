<script setup lang="ts">
import { onMounted } from 'vue'
import { Play, Download, RefreshCw, Trophy } from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import { Button, buttonVariants } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useSectionAnimation } from '@/composables/useSectionAnimation'
import { useLeaderboard } from '@/composables/useLeaderboard'

useSectionAnimation()

const { entries, loading, error, load } = useLeaderboard()

const GAME_URL = '/game/'
const JAR_URL = 'https://github.com/roepard-labs/neon-trails/releases'

const fechaFmt = new Intl.DateTimeFormat('es-CO', { dateStyle: 'medium' })
function formatFecha(iso: string | null): string {
  if (!iso) return '—'
  const d = new Date(iso)
  return Number.isNaN(d.getTime()) ? '—' : fechaFmt.format(d)
}

const resultLabel: Record<string, string> = {
  win: 'Victoria',
  loss: 'Derrota',
  draw: 'Empate',
}

onMounted(() => load(20))
</script>

<template>
  <section class="snap-slide">
    <PageHeader
      :numero="8"
      titulo="Leaderboard"
      subtitulo="Ranking en vivo del juego. Cada partida envía su puntaje al backend (POST /api/scores); aquí se consume GET /api/scores en el mismo origen."
    />

    <!-- Acciones -->
    <div data-anim class="mb-8 flex flex-wrap gap-3">
      <a :href="GAME_URL" :class="buttonVariants()">
        <Play class="size-4" />
        Jugar en el navegador
      </a>
      <a
        :href="JAR_URL"
        target="_blank"
        rel="noreferrer"
        :class="buttonVariants({ variant: 'outline' })"
      >
        <Download class="size-4" />
        Descargar .jar
      </a>
      <Button variant="ghost" :disabled="loading" @click="load(20)">
        <RefreshCw class="size-4" :class="{ 'animate-spin': loading }" />
        Recargar
      </Button>
    </div>

    <!-- Tabla del ranking -->
    <div data-anim class="overflow-hidden rounded-xl border border-border bg-card">
      <div class="flex items-center gap-2 border-b border-border px-5 py-3">
        <Trophy class="size-5 text-brand" />
        <h2 class="font-heading text-lg font-bold">Ranking Top 20</h2>
        <span class="ml-auto rounded border border-border bg-muted/40 px-2 py-0.5 font-mono text-[11px] text-muted-foreground">
          GET /api/scores
        </span>
      </div>

      <!-- Error -->
      <div v-if="error" class="px-5 py-12 text-center">
        <p class="text-sm text-destructive">No se pudo cargar el ranking ({{ error }}).</p>
        <Button class="mt-4" variant="outline" size="sm" @click="load(20)">Reintentar</Button>
      </div>

      <!-- Cargando -->
      <div v-else-if="loading" class="space-y-2 p-5">
        <div v-for="i in 6" :key="i" class="h-9 animate-pulse rounded bg-muted" />
      </div>

      <!-- Vacío -->
      <div v-else-if="entries.length === 0" class="px-5 py-14 text-center text-muted-foreground">
        Aún no hay puntajes. ¡Juega una partida para abrir el marcador!
      </div>

      <!-- Datos -->
      <Table v-else>
        <TableHeader>
          <TableRow>
            <TableHead class="w-12">#</TableHead>
            <TableHead>Jugador</TableHead>
            <TableHead class="text-right">Puntaje</TableHead>
            <TableHead>Resultado</TableHead>
            <TableHead class="text-right">Fecha</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow v-for="(e, i) in entries" :key="e.id">
            <TableCell class="font-mono text-muted-foreground">{{ i + 1 }}</TableCell>
            <TableCell class="font-medium">{{ e.player_name }}</TableCell>
            <TableCell class="text-right font-mono font-bold text-brand">{{ e.score }}</TableCell>
            <TableCell>
              <Badge v-if="e.result" variant="outline">
                {{ resultLabel[e.result] ?? e.result }}
              </Badge>
              <span v-else class="text-muted-foreground">—</span>
            </TableCell>
            <TableCell class="text-right font-mono text-xs text-muted-foreground">
              {{ formatFecha(e.created_at) }}
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
  </section>
</template>
