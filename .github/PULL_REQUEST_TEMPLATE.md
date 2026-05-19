## Descripción

<!-- Describe los cambios de forma clara y concisa. ¿Qué problema resuelven? -->

## Tipo de cambio

<!-- Marca con [x] las que apliquen. -->

- [ ] Bug fix (corrección de error)
- [ ] Nueva feature
- [ ] Refactor (sin cambio funcional)
- [ ] Documentación
- [ ] Mejora de rendimiento
- [ ] CI / Build

## Checklist

<!-- Asegúrate de cumplir todo antes de marcar el PR como listo. -->

- [ ] Mi código sigue las [convenciones del proyecto](AGENTS.md) (comentarios en español, JavaDoc en APIs públicas)
- [ ] Los cambios son **mínimos** — no incluyen refactors masivos "de paso"
- [ ] `make verify` pasa localmente (`mvn -B verify`)
- [ ] Agregué o actualicé tests en `src/test/java/` si apllica
- [ ] Los dos jugadores reciben entrada simultánea (sin romper `WHEN_IN_FOCUSED_WINDOW`)
- [ ] Documenté en `docs/` si el cambio lo amerita
- [ ] Actualicé `CHANGELOG.md` con los cambios relevantes

## Issues relacionados

<!-- Ej: Closes #42, Related to #15 -->

## Cómo probar

<!-- Instrucciones claras de cómo verificar que los cambios funcionan. -->

1. `make run`
2. '...'
3. Verificar que '...'

## Capturas / Evidencia

<!-- Si el cambio es visual, adjunta capturas antes/después. -->
