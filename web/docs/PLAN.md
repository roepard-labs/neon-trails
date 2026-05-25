# Plan de implementación — Proyecto Final Teoría de Sistemas

**Planteamiento elegido:** #8 — Sistema para empresa de logística (mensajería)
**Alcance:** Análisis de sistemas completo + mockups interactivos en HTML/CSS
**Stack:** Vue 3.5 + Vite 7 + Pinia + Vue Router 5 + Tailwind v4 + shadcn-vue 2.7 + GSAP + VueUse Motion

---

## 1. Contexto

El PDF (`docs/0. Proyecto Final Teoría de Sistemas.pdf`) pide entregar un **trabajo de análisis** (no implementación funcional): requerimientos, diagrama UML de casos de uso, descripciones de 2 casos principales + 2 secundarios, diagramas de actividad y mockups. El entregable final es un PDF con portada para sustentar oralmente (50% de la nota).

La base de Vue ya está armada — la aprovechamos para convertir todos los artefactos en un sitio web interactivo, navegable, animado y exportable a PDF mediante "Imprimir → Guardar como PDF" del navegador. Ese PDF resultante es lo que se entrega; el sitio se usa además durante la sustentación oral.

## 2. Contenido del análisis (planteamiento #8)

### Empresa de logística — mensajería
**Problema:** retrasos frecuentes, pérdida de paquetes, falta de trazabilidad, facturación manual.

**Actores:** Cliente, Repartidor, Supervisor logístico, Administrador.

**Casos de uso seleccionados** (según el PDF: 2 principales + 2 secundarios):
- **Principales:**
  1. **CU-01 Registrar pedido** (Cliente) — incluye CU-Calcular tarifa, extiende a CU-Cotizar
  2. **CU-02 Realizar entrega** (Repartidor) — incluye CU-Registrar evidencia
- **Secundarios:**
  3. **CU-03 Asignar ruta automáticamente** (Supervisor logístico) — incluye CU-Optimizar ruta
  4. **CU-04 Generar reporte de desempeño** (Administrador)

**Diagramas de actividad** se elaboran para los 2 casos principales (CU-01 y CU-02).

## 3. Arquitectura del sitio

### Rutas (Vue Router)

| Ruta | Vista | Propósito |
|------|-------|-----------|
| `/` | `PortadaView` | Portada con datos del estudiante (nombre, ID, asignatura, carrera, universidad) |
| `/problema` | `ProblemaView` | Planteamiento del problema, contexto y actores |
| `/requerimientos` | `RequerimientosView` | 4 categorías: negocio, usuario, sistema, F/NF |
| `/casos-de-uso` | `CasosUsoView` | Diagrama UML + tabla de actores y casos |
| `/descripciones` | `DescripcionesView` | 4 descripciones detalladas (2 principales + 2 secundarios) |
| `/actividad` | `ActividadView` | Diagramas de actividad de los 2 CU principales |
| `/mockups` | `MockupsView` | Mockups interactivos (con tabs/sub-rutas por actor) |

### Shell de la app
- Reemplazar `App.vue` por un layout con **sidebar fija** a la izquierda + área de contenido a la derecha
- Sidebar muestra: título del proyecto, lista de secciones (1 a 7), toggle de modo claro/oscuro, botón "Exportar PDF" que dispara `window.print()`
- Indicador de "Sección X de 7" arriba del contenido
- Transiciones suaves entre rutas con `<RouterView v-slot> + <Transition>`

### Datos (TypeScript, módulos en `src/data/`)
- `project.ts` — info del estudiante y proyecto (constantes)
- `actors.ts` — los 4 actores con descripción y responsabilidades
- `requirements.ts` — todos los requerimientos numerados (RN, RU, RS, RF, RNF)
- `useCases.ts` — 4 descripciones completas con todos los campos del template del PDF
- `useCaseDiagram.ts` — definición del diagrama (actores + casos + relaciones include/extend)
- `activityDiagrams.ts` — strings Mermaid de los diagramas de actividad

### Tipos
- `src/types/domain.ts` — `Actor`, `Requirement`, `UseCase`, `UseCaseRelation`, `ActivityDiagram`

### Componentes nuevos

**Layout:**
- `components/layout/AppShell.vue` — sidebar + main
- `components/layout/SidebarNav.vue` — navegación entre secciones (con estado activo)
- `components/layout/PageHeader.vue` — encabezado reutilizable (número de sección + título + subtítulo)
- `components/layout/PrintExportButton.vue` — botón que llama `window.print()`
- `components/layout/ThemeToggle.vue` — toggle claro/oscuro (VueUse `useColorMode`)

**Contenido analítico:**
- `components/content/ActorCard.vue` — tarjeta de actor (ícono Lucide + nombre + descripción)
- `components/content/RequirementBlock.vue` — bloque numerado de un requerimiento (RF01, etc.)
- `components/content/RequirementCategory.vue` — categoría con lista de RequirementBlock
- `components/content/UseCaseDescriptionCard.vue` — tarjeta con todos los campos de un CU
- `components/content/MermaidDiagram.vue` — wrapper que renderiza un string Mermaid a SVG

**Diagramas:** todos vía Mermaid (`MermaidDiagram.vue` es el único wrapper)
- Casos de uso: `flowchart LR` con subgrafo de frontera + estilos diferenciados (actores con shape `[ ]`, casos de uso con shape `(( ))`, líneas `-.->` para include/extend)
- Actividad: `flowchart TD` con `((` para nodos inicial/final, `{ }` para decisiones, `[ ]` para actividades

**Mockups:**
- `components/mockups/MockupFrame.vue` — chrome de navegador/celular alrededor de un mockup
- `components/mockups/MockupNav.vue` — pestañas para alternar entre actores
- `components/mockups/cliente/ClienteRastrearPedido.vue`
- `components/mockups/cliente/ClienteCrearPedido.vue`
- `components/mockups/repartidor/RepartidorRutaDelDia.vue`
- `components/mockups/repartidor/RepartidorConfirmarEntrega.vue`
- `components/mockups/supervisor/SupervisorAsignarRutas.vue`
- `components/mockups/supervisor/SupervisorMonitoreo.vue`
- `components/mockups/administrador/AdministradorReportes.vue`
- `components/mockups/administrador/AdministradorUsuarios.vue`

**shadcn-vue components a agregar** (vía CLI):
`card`, `separator`, `badge`, `tabs`, `scroll-area`, `sheet` (sidebar móvil), `table`, `input`, `label`, `switch` (theme toggle), `tooltip`. El de `sidebar` lo construimos manual porque el shadcn `sidebar` agrega bastante complejidad y no la necesitamos.

### Composables
- `composables/useScrollReveal.ts` — wrapper de GSAP ScrollTrigger para revelar elementos al entrar a viewport
- `composables/useSectionAnimation.ts` — animación de entrada al montar cada vista (timeline con stagger)

### Librerías a agregar
- **`mermaid`** (~150KB gzipped) — para diagramas de actividad. Lo registramos en un wrapper Vue (`MermaidDiagram.vue`) con `onMounted` + `mermaid.run()`.
- (Opcional) **`@vueuse/core` ya está** — usamos `useColorMode` para modo oscuro.

No agregamos librerías de mapas / gráficos pesados porque los mockups simulan sin necesidad de funcionalidad real (un `<div>` con grid de paquetes representa "monitoreo en tiempo real").

## 4. Diseño visual

- **Tipografía:** JetBrains Mono ya configurada (para acento "técnico/sistema"); body se puede dejar en sans-serif del sistema o también JetBrains
- **Color base:** zinc (ya configurado por shadcn-vue)
- **Acento de marca:** un color secundario por definir (sugerencia: azul logístico / naranja entrega — definirlo como `--color-brand` en `style.css`)
- **Animaciones:**
  - Entrada de cada vista: fade + slide up con stagger (GSAP timeline, ~600ms)
  - Scroll reveal en bloques largos (requerimientos, descripciones de CU)
  - Diagrama UML "se dibuja" al entrar (stroke-dashoffset animado con GSAP)
  - Mockups: micro-interacciones (hover en cards, focus en inputs)
- **Print stylesheet** en `style.css`:
  - `@media print` oculta sidebar y controles
  - Cada `<section>` con `page-break-after: always`
  - Colores forzados (sin OKLCH oscuro) para tinta
  - Anima → estado final (sin transitions)

## 5. Implementación en fases

### Fase 1 — Cimientos (shell + rutas + datos)
1. Crear `src/types/domain.ts` con todos los tipos
2. Crear todos los archivos en `src/data/` con la información del planteamiento
3. Crear `src/router/index.ts` con las 7 rutas
4. Crear `AppShell.vue` + `SidebarNav.vue` + `PageHeader.vue`
5. Reemplazar `App.vue` para usar `AppShell` con `<RouterView />`
6. Vistas vacías (placeholders con `PageHeader`) para las 7 rutas
7. Agregar shadcn-vue components (`card`, `separator`, `badge`, `tabs`, `scroll-area`, `table`, `tooltip`, `switch`)
8. Toggle de tema con VueUse `useColorMode`

**Verificación:** `pnpm dev` muestra sidebar + 7 rutas navegables.

### Fase 2 — Portada
- `PortadaView` con datos del estudiante, animación de entrada GSAP
- Estilo limpio académico: logo/escudo (si lo hay), título grande, info en bloque inferior

### Fase 3 — Problema
- Descripción del problema (texto narrativo)
- Sección de actores (4 `ActorCard` en grid)
- 3 columnas: "Actualmente", "Problemas", "Se requiere" como cards

### Fase 4 — Requerimientos
- 4 secciones (Negocio, Usuario, Sistema, Funcionales/No funcionales)
- Cada requerimiento con código (RN-01, RU-01, RS-01, RF-01, RNF-01), nombre y descripción
- Presentación: tabs o accordion, según el espacio
- Stagger animation al entrar

### Fase 5 — Diagrama de Casos de Uso
- Mermaid `flowchart LR` aproximando el diagrama de casos de uso:
  - Subgrafo con título "Sistema de Mensajería" como frontera del sistema
  - Actores fuera del subgrafo, casos de uso (óvalos) dentro
  - Flechas sólidas para asociación actor↔caso
  - Flechas punteadas con etiqueta `<<include>>` / `<<extend>>` entre casos
- CSS overrides para que los nodos de actor se vean redondeados y los casos elípticos (Mermaid no tiene shape "actor" nativo — usamos `(( ))` para casos y `[ ]` para actores con styling diferenciado)
- Tabla resumen debajo: actores ↔ casos de uso (con shadcn `Table`)
- Animación de entrada del diagrama: fade in + scale ligero (GSAP, ~400ms)

### Fase 6 — Descripciones de Casos de Uso
- 4 `UseCaseDescriptionCard` (CU-01, CU-02, CU-03, CU-04)
- Cada card muestra los 9 campos del template del PDF:
  Nombre · Actor(es) · Tipo · Precondiciones · Descripción · Flujo normal · Flujos alternos · Excepciones · Postcondiciones
- Layout en columna a 1 o 2 columnas según viewport

### Fase 7 — Diagramas de Actividad
- 2 diagramas (uno por CU principal) renderizados con Mermaid `flowchart TD`
- Cada diagrama incluye: nodo inicial (•), actividades (rectángulos redondeados), decisiones (diamantes), flujos alternativos y nodo final (◉)
- Título + descripción del flujo arriba de cada diagrama

### Fase 8 — Mockups
- `MockupsView` con `<Tabs>` shadcn-vue para alternar entre 4 actores
- Cada actor muestra 2 mockups (8 mockups en total, alineados con los 4 CU + dashboards)
- Cada mockup renderizado dentro de `MockupFrame` (frame de navegador o celular según el contexto)
- Los mockups son **HTML estático real** (no imágenes): sidebar simulada, tablas con datos ficticios, formularios deshabilitados pero con estilo real
- Microinteracciones: hover en filas, focus en inputs, tabs internos

### Fase 9 — Print + Export
- Agregar bloque `@media print` en `style.css`
- Vista única "imprimible" (`/imprimir` o todo en uno) que renderiza todas las secciones en cascada — opcional pero útil
- Botón "Exportar PDF" en sidebar que llama `window.print()`
- Verificar visualmente en print preview que todo se ve bien

### Fase 10 — Pulido
- Revisar tipografía y espaciados
- Ajustar animaciones GSAP (timing, ease)
- Verificar responsive (desktop + tablet — móvil opcional)
- Verificar accesibilidad básica (semántica, contraste, focus visible)
- `pnpm lint` y `pnpm type-check` sin errores
- `pnpm build` sin warnings

## 6. Archivos críticos a crear/modificar

**Crear:**
- `src/types/domain.ts`
- `src/data/project.ts`, `actors.ts`, `requirements.ts`, `useCases.ts`, `useCaseDiagram.ts`, `activityDiagrams.ts`
- `src/views/PortadaView.vue`, `ProblemaView.vue`, `RequerimientosView.vue`, `CasosUsoView.vue`, `DescripcionesView.vue`, `ActividadView.vue`, `MockupsView.vue`
- `src/components/layout/AppShell.vue`, `SidebarNav.vue`, `PageHeader.vue`, `PrintExportButton.vue`, `ThemeToggle.vue`
- `src/components/content/ActorCard.vue`, `RequirementBlock.vue`, `RequirementCategory.vue`, `UseCaseDescriptionCard.vue`, `MermaidDiagram.vue`
- `src/components/mockups/MockupFrame.vue` + 8 mockups
- `src/composables/useScrollReveal.ts`, `useSectionAnimation.ts`

**Modificar:**
- `src/App.vue` — usar `AppShell` envolviendo `<RouterView />`
- `src/router/index.ts` — agregar las 7 rutas
- `src/style.css` — agregar `@media print` + posibles variables de marca
- `src/views/HomeView.vue` — eliminar (reemplazado por PortadaView) o renombrar
- `package.json` — agregar `mermaid` como dependencia

**Eliminar/reemplazar:**
- `src/stores/counter.ts` — eliminar (era placeholder del scaffold)

## 7. Decisiones tomadas

- **Idioma del contenido:** español (toda la UI y todo el contenido analítico)
- **Modo oscuro:** se incluye toggle (las variables CSS `.dark` ya están definidas)
- **Mermaid para ambos diagramas** (actividad + casos de uso aproximado con `flowchart LR` + subgrafo de frontera + estilos)
- **Mockups son HTML interactivo, no imágenes** (más impresionante para sustentación y permite cambios rápidos)
- **Una sola página por sección** (sin scroll infinito) — facilita print-to-PDF
- **No backend, no persistencia, no funcionalidad real** — es análisis + mockups según lo elegido
- **Color de marca:** azul logístico — agregar `--brand` en `style.css` con un valor oklch equivalente a `#1d4ed8` (aprox `oklch(0.488 0.217 264)`). Variante dark con un azul más claro.
- **Portada:** valores placeholder en `src/data/project.ts` (`{{NOMBRE_ESTUDIANTE}}`, `{{IDENTIFICACION}}`, `{{CARRERA}}`, `{{UNIVERSIDAD}}`) que el usuario reemplaza después.

## 9. Verificación final

Cuando termine la implementación:
- `pnpm dev` → navegar por todas las secciones y confirmar contenido + animaciones
- `pnpm type-check` y `pnpm lint` sin errores
- `pnpm build` sin warnings, dist/ generado
- Abrir print preview (Ctrl+P en Chrome/Firefox) → confirmar que el PDF resultante es limpio, con cada sección en su página
- Probar modo oscuro y volver a claro
- Probar responsive en viewport tablet (~1024px) y desktop (~1440px)
