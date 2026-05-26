<script setup lang="ts">
import { onMounted } from 'vue'
import {
  Gamepad2,
  ShieldCheck,
  Plug,
  Download,
  Github,
  ExternalLink,
  RefreshCw,
  Trophy,
  type LucideIcon,
} from 'lucide-vue-next'
import PageHeader from '@/components/layout/PageHeader.vue'
import { Button } from '@/components/ui/button'
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

const REPO_URL = 'https://github.com/roepard-labs/neon-trails'

// Enlaces a todas las superficies del despliegue. Las rutas internas son
// relativas: resuelven contra el dominio que sirve la SPA (mismo origen).
interface EndpointLink {
  label: string
  desc: string
  href: string
  icon: LucideIcon
  acento?: boolean
}

const endpoints: EndpointLink[] = [
  {
    label: 'Jugar en el navegador',
    desc: 'El juego Swing vía noVNC',
    href: '/game/vnc.html?autoconnect=true&resize=remote',
    icon: Gamepad2,
    acento: true,
  },
  {
    label: 'Panel de administración',
    desc: 'Filament · CRUD del leaderboard (/admin)',
    href: '/admin',
    icon: ShieldCheck,
  },
  {
    label: 'API · ranking (JSON)',
    desc: 'GET /api/scores?limit=20',
    href: '/api/scores?limit=20',
    icon: Plug,
  },
  {
    label: 'Descargar .jar',
    desc: 'Ejecutable del juego (releases)',
    href: `${REPO_URL}/releases`,
    icon: Download,
  },
  {
    label: 'Repositorio',
    desc: 'Código fuente en GitHub',
    href: REPO_URL,
    icon: Github,
  },
]

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
      :numero="13"
      titulo="Leaderboard"
      subtitulo="Ranking en vivo del juego y acceso directo a todas las superficies del despliegue (juego, panel admin y API), servidas en el mismo dominio."
    />

    <!-- Endpoints en vivo -->
    <div data-anim class="mb-8">
      <h2 class="mb-3 flex items-center gap-2 font-mono text-xs uppercase tracking-[0.18em] text-muted-foreground">
        <ExternalLink class="size-3.5" />
        Endpoints en vivo
      </h2>
      <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <a
          v-for="e in endpoints"
          :key="e.href"
          :href="e.href"
          target="_blank"
          rel="noopener"
          class="group flex items-center gap-3 rounded-xl border bg-card p-4 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          :class="e.acento ? 'border-brand/50 hover:border-brand hover:bg-brand/5' : 'border-border hover:border-brand/60 hover:bg-accent/40'"
        >
          <span
            class="flex size-10 shrink-0 items-center justify-center rounded-lg"
            :class="e.acento ? 'bg-brand text-brand-foreground' : 'bg-brand/15 text-brand'"
          >
            <component :is="e.icon" class="size-5" />
          </span>
          <span class="min-w-0 flex-1">
            <span class="flex items-center gap-1.5 font-medium">
              {{ e.label }}
              <ExternalLink class="size-3 text-muted-foreground opacity-0 transition-opacity group-hover:opacity-100" />
            </span>
            <span class="block truncate text-sm text-muted-foreground">{{ e.desc }}</span>
          </span>
        </a>
      </div>
    </div>

    <!-- Tabla del ranking -->
    <div data-anim class="overflow-hidden rounded-xl border border-border bg-card">
      <div class="flex items-center gap-2 border-b border-border px-5 py-3">
        <Trophy class="size-5 text-brand" />
        <h2 class="font-heading text-lg font-bold">Ranking Top 20</h2>
        <span class="ml-auto flex items-center gap-3">
          <span class="hidden rounded border border-border bg-muted/40 px-2 py-0.5 font-mono text-[11px] text-muted-foreground sm:inline">
            GET /api/scores
          </span>
          <Button variant="ghost" size="sm" :disabled="loading" @click="load(20)">
            <RefreshCw class="size-4" :class="{ 'animate-spin': loading }" />
            Recargar
          </Button>
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
