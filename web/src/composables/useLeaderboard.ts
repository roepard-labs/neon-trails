import { ref } from 'vue'

/** Forma de cada puntaje devuelto por GET /api/scores (ver ScoreResource en Laravel). */
export interface LeaderboardEntry {
  id: number
  player_name: string
  score: number
  result: string | null
  played_at: string | null
  created_at: string | null
}

// Mismo origen por defecto (monolito). En dev, el proxy de Vite reenvía /api al backend.
const API_BASE = import.meta.env.VITE_API_URL ?? ''

/**
 * Composable que consulta el ranking del leaderboard.
 * Tolerante a fallos: ante error de red o HTTP, expone `error` y deja la lista vacía.
 */
export function useLeaderboard() {
  const entries = ref<LeaderboardEntry[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function load(limit = 20) {
    loading.value = true
    error.value = null
    try {
      const res = await fetch(`${API_BASE}/api/scores?limit=${limit}`, {
        headers: { Accept: 'application/json' },
      })
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const json = await res.json()
      // La API envuelve la colección en `data`; admitimos también un array plano.
      entries.value = Array.isArray(json) ? json : (json.data ?? [])
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'No se pudo cargar el ranking.'
      entries.value = []
    } finally {
      loading.value = false
    }
  }

  return { entries, loading, error, load }
}
