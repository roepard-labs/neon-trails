<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { gsap } from '@/lib/gsap'
import { MapPin, ArrowRight, Play, Trophy } from 'lucide-vue-next'
import { projectInfo } from '@/data/project'
import { buttonVariants } from '@/components/ui/button'
import { usePrefersReducedMotion } from '@/composables/usePrefersReducedMotion'
import portadaPhoto from '@/assets/jemgdevp.jpeg'

const photo = ref<HTMLElement | null>(null)
const eyebrow = ref<HTMLElement | null>(null)
const title = ref<HTMLElement | null>(null)
const subtitle = ref<HTMLElement | null>(null)
const card = ref<HTMLElement | null>(null)
const accentLine = ref<HTMLElement | null>(null)
const cta = ref<HTMLElement | null>(null)

const prefersReducedMotion = usePrefersReducedMotion()

onMounted(() => {
  if (prefersReducedMotion.value) {
    const targets = [
      photo.value,
      eyebrow.value,
      accentLine.value,
      ...(title.value?.querySelectorAll('.split-word') ?? []),
      subtitle.value,
      ...(card.value?.querySelectorAll('[data-info]') ?? []),
      cta.value,
    ].filter(Boolean) as Element[]
    gsap.set(targets, { opacity: 1, y: 0, scale: 1, clearProps: 'all' })
    return
  }

  const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

  tl.from(photo.value, { opacity: 0, scale: 0.85, duration: 0.9 })
    .from(eyebrow.value, { opacity: 0, y: 12, duration: 0.5 }, '-=0.4')
    .from(
      accentLine.value,
      { scaleX: 0, transformOrigin: 'center center', duration: 0.6 },
      '-=0.3',
    )
    .from(
      title.value?.querySelectorAll('.split-word') ?? [],
      { opacity: 0, y: 28, duration: 0.7, stagger: 0.08 },
      '-=0.4',
    )
    .from(subtitle.value, { opacity: 0, y: 14, duration: 0.5 }, '-=0.3')
    .from(
      card.value?.querySelectorAll('[data-info]') ?? [],
      { opacity: 0, y: 20, duration: 0.5, stagger: 0.06 },
      '-=0.2',
    )
    .from(cta.value, { opacity: 0, y: 10, duration: 0.4 }, '-=0.2')

  // Pulse en la flecha del CTA
  gsap.to('[data-cta-arrow]', {
    x: 6,
    duration: 0.9,
    yoyo: true,
    repeat: -1,
    ease: 'sine.inOut',
  })
})

const tituloWords = projectInfo.titulo.split(' ')
</script>

<template>
  <section
    class="snap-slide relative isolate flex min-h-[70vh] flex-col items-center justify-center overflow-hidden text-center sm:min-h-[75vh] md:min-h-[80vh]"
  >
    <!-- Background grid + radial gradient -->
    <div
      class="absolute inset-0 -z-10 bg-grid animate-grid-drift opacity-50 [mask-image:radial-gradient(ellipse_at_top,_black_30%,_transparent_70%)]"
      aria-hidden="true"
    />

    <div class="relative mx-auto flex max-w-3xl flex-col items-center">
      <!-- Foto + halo difuminado -->
      <div ref="photo" class="relative mb-10 size-28 sm:size-32 md:size-40 lg:size-44">
        <!-- Halo difuminado rotando (misma foto, escalada y borrosa) -->
        <div class="pointer-events-none absolute inset-0 animate-orbit-slow" aria-hidden="true">
          <img
            :src="portadaPhoto"
            alt=""
            aria-hidden="true"
            loading="lazy"
            decoding="async"
            class="absolute inset-0 size-full scale-[1.7] rounded-full object-cover opacity-50 blur-3xl saturate-150"
          />
        </div>
        <!-- Foto principal -->
        <img
          :src="portadaPhoto"
          alt="Neon Trails"
          loading="eager"
          decoding="async"
          fetchpriority="high"
          class="relative size-full rounded-full object-cover shadow-2xl ring-4 ring-background ring-offset-2 ring-offset-brand/40"
        />
      </div>

      <!-- Eyebrow + accent line -->
      <div ref="eyebrow" class="flex items-center gap-3">
        <span
          ref="accentLine"
          class="relative inline-block h-px w-10 overflow-hidden bg-brand/30"
          aria-hidden="true"
        >
          <span class="absolute inset-0 animate-shimmer-brand" aria-hidden="true" />
        </span>
        <p class="font-mono text-xs uppercase tracking-[0.22em] text-brand">
          Proyecto Final · {{ projectInfo.asignatura }}
        </p>
        <span
          class="relative inline-block h-px w-10 overflow-hidden bg-brand/30"
          aria-hidden="true"
        >
          <span class="absolute inset-0 animate-shimmer-brand" aria-hidden="true" />
        </span>
      </div>

      <!-- Título -->
      <h1
        ref="title"
        class="mt-8 text-balance text-4xl font-bold leading-[1.05] tracking-tight sm:text-5xl md:text-6xl lg:text-7xl"
      >
        <span
          v-for="(word, i) in tituloWords"
          :key="i"
          class="split-word mr-3 inline-block"
        >{{ word }}</span>
      </h1>

      <!-- Subtitle -->
      <p
        ref="subtitle"
        class="mx-auto mt-8 max-w-2xl text-balance text-lg leading-relaxed text-muted-foreground md:text-xl"
      >
        {{ projectInfo.subtitulo }}
      </p>

      <!-- Info card -->
      <div
        ref="card"
        class="mt-14 grid w-full max-w-3xl gap-x-10 gap-y-6 rounded-xl border border-border bg-card/80 p-5 text-left text-sm backdrop-blur sm:grid-cols-2 sm:p-6 md:p-8"
      >
        <div data-info class="sm:col-span-2">
          <p class="font-mono text-xs uppercase tracking-[0.16em] text-muted-foreground">
            Integrantes
          </p>
          <ul class="mt-2 grid gap-1.5 sm:grid-cols-2">
            <li
              v-for="(integrante, i) in projectInfo.integrantes"
              :key="i"
              class="flex items-baseline gap-2"
            >
              <span class="text-lg font-medium">{{ integrante.nombre }}</span>
              <span
                v-if="integrante.identificacion"
                class="font-mono text-xs text-muted-foreground"
              >
                {{ integrante.identificacion }}
              </span>
            </li>
          </ul>
        </div>
        <div data-info>
          <p class="font-mono text-xs uppercase tracking-[0.16em] text-muted-foreground">
            Asignatura
          </p>
          <p class="mt-1.5 text-lg font-medium">{{ projectInfo.asignatura }}</p>
        </div>
        <div data-info>
          <p class="font-mono text-xs uppercase tracking-[0.16em] text-muted-foreground">
            Carrera
          </p>
          <p class="mt-1.5 text-lg font-medium">{{ projectInfo.carrera }}</p>
        </div>
        <div v-if="projectInfo.docente" data-info class="sm:col-span-2">
          <p class="font-mono text-xs uppercase tracking-[0.16em] text-muted-foreground">
            Docente
          </p>
          <p class="mt-1.5 text-lg font-medium">{{ projectInfo.docente }}</p>
        </div>
        <div data-info class="sm:col-span-2">
          <p class="font-mono text-xs uppercase tracking-[0.16em] text-muted-foreground">
            Universidad
          </p>
          <p class="mt-1.5 flex items-center gap-2 text-lg font-medium">
            <MapPin class="size-4 text-brand" />
            {{ projectInfo.universidad }}
            <span v-if="projectInfo.ciudad" class="font-normal text-muted-foreground">
              · {{ projectInfo.ciudad }}
            </span>
            <span class="ml-auto font-mono text-xs text-muted-foreground">
              {{ projectInfo.ano }}
            </span>
          </p>
        </div>
      </div>

      <div ref="cta" class="mt-12 flex items-center justify-center gap-2">
        <RouterLink
          to="/problema"
          class="group inline-flex min-h-12 items-center gap-3 rounded-lg bg-brand px-6 py-3.5 text-base font-medium text-brand-foreground shadow-sm transition-opacity hover:opacity-90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
        >
          Comenzar la exposición
          <ArrowRight data-cta-arrow class="size-5" />
        </RouterLink>
        <span class="text-sm text-muted-foreground">— 8 secciones</span>
      </div>

      <!-- Acciones rápidas: jugar y ranking -->
      <div class="mt-6 flex flex-wrap items-center justify-center gap-3">
        <a :href="'/game/'" :class="buttonVariants({ variant: 'outline' })">
          <Play class="size-4" />
          Jugar en el navegador
        </a>
        <RouterLink to="/leaderboard" :class="buttonVariants({ variant: 'ghost' })">
          <Trophy class="size-4" />
          Ver leaderboard
        </RouterLink>
      </div>
    </div>
  </section>
</template>
