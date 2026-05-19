# Modelo de Gobernanza

## Visión general

Neon Trails es un proyecto académico mantenido por [jemgdevp](https://github.com/jemgdevp) como parte del curso **Técnicas de Programación**. El objetivo es construir un juego funcional que cumpla los requisitos establecidos en [docs/rules/](docs/rules/).

## Roles

| Rol | Responsable | Funciones |
|-----|-------------|-----------|
| **Mantenedor** | [jemgdevp](https://github.com/jemgdevp) | Revisión de PRs, merges a `develop` y `master`, dirección técnica, cumplimiento de requisitos del curso |
| **Colaboradores** | Contribuidores externos | Reportar bugs, proponer features, enviar PRs |

## Proceso de toma de decisiones

1. **Issues y features**: cualquier persona puede abrir un issue con una propuesta o bug.
2. **Discusión**: las propuestas se discuten en el issue. El mantenedor puede etiquetar como `accepted`, `declined` o `needs-discussion`.
3. **Implementación**: PRs desde branches con nombre `feat/`, `fix/`, `docs/`, etc.
4. **Revisión**: el mantenedor revisa el PR contra los requisitos del curso, el checklist de [AGENTS.md](AGENTS.md) y el pipeline de CI.
5. **Merge**: solo el mantenedor mergea a `develop` o `master`.

## Ramas

| Rama | Propósito |
|------|-----------|
| `master` | Código estable, listo para entrega |
| `develop` | Rama de integración, desarrollo activo |
| `feat/*`, `fix/*` | Ramas de feature/bugfix |

## Código de Conducta

Este proyecto sigue el [Código de Conducta](CODE_OF_CONDUCT.md). Se espera que todas las personas participantes lo respeten.

## Cambios a este documento

Modificaciones a este modelo de gobernanza deben proponerse mediante un issue y ser aprobadas por el mantenedor.
