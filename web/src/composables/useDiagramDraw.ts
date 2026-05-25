import { nextTick, type Ref } from 'vue'
import { gsap } from '@/lib/gsap'
import { usePrefersReducedMotion } from './usePrefersReducedMotion'

interface DiagramDrawOptions {
  edgeDuration?: number
  edgeStagger?: number
  nodeDuration?: number
  nodeStagger?: number
}

export function useDiagramDraw(
  host: Ref<HTMLElement | null>,
  options: DiagramDrawOptions = {},
) {
  const {
    edgeDuration = 1.2,
    edgeStagger = 0.06,
    nodeDuration = 0.45,
    nodeStagger = 0.04,
  } = options

  const prefersReducedMotion = usePrefersReducedMotion()

  return async function drawDiagram() {
    await nextTick()
    const svg = host.value?.querySelector('svg')
    if (!svg) return
    if (prefersReducedMotion.value) return

    const edges = svg.querySelectorAll<SVGPathElement>(
      '.edgePath path, .flowchart-link, .edge-pattern-solid',
    )
    edges.forEach((path) => {
      const len = typeof path.getTotalLength === 'function' ? path.getTotalLength() : 200
      path.style.strokeDasharray = String(len)
      path.style.strokeDashoffset = String(len)
    })

    if (edges.length > 0) {
      gsap.to(edges, {
        strokeDashoffset: 0,
        duration: edgeDuration,
        stagger: edgeStagger,
        ease: 'power2.out',
        clearProps: 'strokeDasharray,strokeDashoffset',
      })
    }

    const nodes = svg.querySelectorAll<SVGGElement>('.node, .cluster')
    if (nodes.length > 0) {
      gsap.from(nodes, {
        opacity: 0,
        scale: 0.92,
        transformOrigin: 'center center',
        duration: nodeDuration,
        stagger: nodeStagger,
        ease: 'back.out(1.6)',
      })
    }
  }
}
