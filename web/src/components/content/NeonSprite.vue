<script setup lang="ts">
import { computed } from 'vue'

type Player = 'p1' | 'p2'
type SpriteState = 'idle' | 'walk' | 'throw' | 'bike'

interface Props {
  /** Jugador: P1 (cyan) o P2 (magenta). Define color de halo/glow y el sprite. */
  player?: Player
  /** Pose del humanoide. Ignorado si se pasa `src` directo. */
  state?: SpriteState
  /** Ruta directa a un sprite de `/sprites/*.svg` (motos, discos, arena…). */
  src?: string
  /** Lado del sprite en px (los SVG son cuadrados). */
  size?: number
  /** Halo radial detrás del sprite. */
  halo?: boolean
  /** `drop-shadow` de neón sobre el trazo del sprite. */
  glow?: boolean
  /** Texto alternativo accesible. */
  alt?: string
}

const props = withDefaults(defineProps<Props>(), {
  player: 'p1',
  state: 'idle',
  size: 96,
  halo: true,
  glow: true,
})

// Los sprites viven en /public/sprites para que sus animaciones SMIL y filtros
// SVG queden aislados dentro del <img> (no colisionan IDs entre instancias).
const resolvedSrc = computed(
  () => props.src ?? `/sprites/player-${props.player}-${props.state}.svg`,
)

const color = computed(() => (props.player === 'p2' ? '#ff3399' : '#00ffff'))

const haloStyle = computed(() =>
  props.halo
    ? {
        background: `radial-gradient(circle at 50% 55%, ${
          props.player === 'p2' ? 'rgba(255,51,153,0.20)' : 'rgba(0,255,255,0.20)'
        }, transparent 68%)`,
      }
    : {},
)

const imgStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  filter: props.glow
    ? `drop-shadow(0 0 7px ${color.value}) drop-shadow(0 0 2px ${color.value})`
    : 'none',
}))

const altText = computed(
  () => props.alt ?? `Jugador ${props.player === 'p2' ? '2' : '1'} (${props.state})`,
)
</script>

<template>
  <span class="relative inline-grid place-items-center" :style="haloStyle">
    <img
      :src="resolvedSrc"
      :alt="altText"
      :style="imgStyle"
      class="block select-none"
      loading="lazy"
      decoding="async"
      draggable="false"
    />
  </span>
</template>
