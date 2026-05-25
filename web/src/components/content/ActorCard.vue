<script setup lang="ts">
import {
  User,
  UserPlus,
  UserCheck,
  Radio,
  Users,
  type LucideIcon,
} from 'lucide-vue-next'
import { Card, CardContent, CardHeader } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import type { Actor } from '@/types/domain'

interface Props {
  actor: Actor
  highlightId?: string
}

const props = defineProps<Props>()

const iconMap: Record<string, LucideIcon> = {
  User,
  UserPlus,
  UserCheck,
  Radio,
  Users,
}

const Icon = iconMap[props.actor.icon] ?? Users
</script>

<template>
  <Card
    class="group relative overflow-hidden border-border transition-shadow hover:shadow-md"
    :class="{ 'ring-2 ring-brand': highlightId === actor.id }"
  >
    <span
      aria-hidden="true"
      class="absolute -top-10 -right-10 size-32 rounded-full bg-brand/8 transition-transform group-hover:scale-110"
    />

    <CardHeader class="relative">
      <div class="flex items-start gap-4">
        <span
          class="flex size-14 shrink-0 items-center justify-center rounded-xl bg-brand text-brand-foreground shadow-sm"
        >
          <Icon class="size-7" />
        </span>
        <div class="flex flex-col">
          <span class="font-heading text-xl font-semibold leading-tight">
            {{ actor.nombre }}
          </span>
          <Badge variant="secondary" class="mt-2 w-fit font-mono text-xs">
            {{ actor.id }}
          </Badge>
        </div>
      </div>
    </CardHeader>

    <CardContent class="relative space-y-4">
      <p class="text-base leading-relaxed text-muted-foreground">
        {{ actor.descripcion }}
      </p>
      <div>
        <p class="mb-2 font-mono text-xs uppercase tracking-[0.16em] text-muted-foreground">
          Responsabilidades
        </p>
        <ul class="space-y-2">
          <li
            v-for="(r, i) in actor.responsabilidades"
            :key="i"
            class="flex gap-2.5 text-base text-foreground"
          >
            <span
              aria-hidden="true"
              class="mt-2.5 inline-block size-1.5 shrink-0 rounded-full bg-brand"
            />
            <span>{{ r }}</span>
          </li>
        </ul>
      </div>
    </CardContent>
  </Card>
</template>
