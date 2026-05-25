interface MotionVariant {
  initial: Record<string, number>
  visibleOnce: {
    opacity: number
    x?: number
    y?: number
    scale?: number
    transition: { delay: number; duration: number; ease: string }
  }
}

export function useMockupMotion() {
  const enterUp = (delay = 0): MotionVariant => ({
    initial: { opacity: 0, y: 18 },
    visibleOnce: { opacity: 1, y: 0, transition: { delay, duration: 380, ease: 'easeOut' } },
  })

  const enterFromRight = (delay = 0): MotionVariant => ({
    initial: { opacity: 0, x: 18 },
    visibleOnce: { opacity: 1, x: 0, transition: { delay, duration: 380, ease: 'easeOut' } },
  })

  const enterFade = (delay = 0): MotionVariant => ({
    initial: { opacity: 0 },
    visibleOnce: { opacity: 1, transition: { delay, duration: 320, ease: 'easeOut' } },
  })

  const enterScale = (delay = 0): MotionVariant => ({
    initial: { opacity: 0, scale: 0.95 },
    visibleOnce: { opacity: 1, scale: 1, transition: { delay, duration: 380, ease: 'easeOut' } },
  })

  return { enterUp, enterFromRight, enterFade, enterScale }
}
