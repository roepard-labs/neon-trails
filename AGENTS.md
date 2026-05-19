# AGENTS.md — Neon Trails

Guía para asistentes de código (y humanos) que trabajan en este repositorio.

## Qué es el proyecto

- **Stack**: Java **Swing** (ventana 2D, `JFrame`/`JPanel`), sin motor externo.
- **Género**: arena local **dos jugadores** (inspiración *Tron*): movimiento, **discos**, **modo moto ~5 s**, colisiones con **bordes** del panel.
- **Web**: carpeta `web/` es un **stub** (HTML/CSS/JS + JSON mock); no sustituye al juego Java.

## Regla de oro (cátedra)

La entrega debe alinearse con [**docs/rules/Proyecto Técnicas de Programación.pdf**](docs/rules/Proyecto%20Técnicas%20de%20Programación.pdf) y con [**docs/rules/general.md**](docs/rules/general.md). Antes de diseñar o ampliar funcionalidad, releer esos requisitos (POO, paquetes, hilos, multimedia, pantallas, ranking, etc.).

**Importante:** el PDF menciona políticas académicas estrictas (p. ej. sobre uso de IA y sustentación). Quien entrega el curso es responsable de cumplirlas; este archivo no sustituye las normas del docente.

## Estructura de código (mantener)

| Paquete / área | Ruta | Rol |
|----------------|------|-----|
| Entrada | [`src/main/java/Main.java`](src/main/java/Main.java) | `main`; arranque en EDT con Swing. |
| Fachada UI | [`src/main/java/modules/GameGUI.java`](src/main/java/modules/GameGUI.java) | Punto para encadenar futuras pantallas (menú, instrucciones…). |
| Lógica | [`src/main/java/logic/`](src/main/java/logic/) | Estado, entidades, reglas, constantes. Sin dependencias de Swing si es posible. |
| Vista | [`src/main/java/view/`](src/main/java/view/) | Ventana, panel, bucle en **hilo** + repintado. |
| Eventos | [`src/main/java/events/`](src/main/java/events/) | Entrada: *Key Bindings* (`InputMap`/`ActionMap`), no `KeyListener` salvo justificación. |
| Recursos | [`src/main/resources/`](src/main/resources/) | Imágenes y audio empacados al classpath; cargar con `getClass().getResourceAsStream("/...")`. |
| Pantallas | [`src/main/java/view/screens/`](src/main/java/view/screens/) | Subclases de `BaseScreen` (Welcome, NameInput, Game, GameOver). Coordina `ScreenManager`. |
| Tests | [`src/test/java/`](src/test/java/) | JUnit 5 para la capa pura `logic/`. |
| Documentación | [`docs/`](docs/) | Arquitectura, pruebas, reglas. |
| Web stub | [`web/`](web/) | Mock de ranking / demo de UI estática. |

## Convenciones de implementación

- **Comentarios**: en **español**; **JavaDoc** en APIs públicas; usar `// NOTE:`, `// TODO:`, `// FIXME:` solo cuando aporten contexto no obvio.
- **Concurrencia**: la simulación avanza en el hilo del juego; el dibujo en el EDT. Mutación de estado compartido: **un solo lock coherente** (p. ej. objeto `stateLock` en el panel) o copias snapshot si crece la complejidad.
- **Controles**: mantener convivencia de dos teclados en la misma PC; preferir `WHEN_IN_FOCUSED_WINDOW` en el panel raíz.
- **Alcance de cambios**: cambios **mínimos** y centrados en la tarea; no refactor masivo “de paso”.

## Cómo compilar y ejecutar

Build system: **Maven** (Java 17). El `Makefile` envuelve los comandos comunes:

```bash
make compile   # mvn compile
make run       # mvn exec:java  (arranca el juego)
make test      # mvn test       (JUnit 5 en src/test/java)
make package   # mvn package    (jar en target/)
make verify    # mvn -B verify  (igual que CI)
make clean     # mvn clean
```

CI: [`.github/workflows/java.yml`](.github/workflows/java.yml) corre `mvn -B verify` con Temurin 17 en push y PR contra `develop`/`master`. Si se añade un build paso (shade, resources adicionales, etc.), actualizar este bloque, el README y el workflow.

## Archivos de plan / hitos

Si el usuario adjunta un plan en `.cursor/plans/` o similar, **no editar ese archivo** salvo que lo pida explícitamente. Los to-dos del plan se marcan en la herramienta de tareas del agente, no duplicando listas en el plan.

## Checklist rápido antes de dar por hecha una feature

1. ¿`make verify` pasa (compila + tests + package)?
2. ¿Los dos jugadores reciben entrada a la vez (con foco en la ventana)?
3. ¿Bordes, discos y moto se comportan como en la especificación de la tarea?
4. ¿El PDF exige algo nuevo (sonidos, pantallas, ordenamiento, persistencia) y queda **acotado** o documentado como siguiente paso?

## Referencias útiles

- Arquitectura base: [`docs/architecture.md`](docs/architecture.md)
- Pruebas manuales sugeridas: [`docs/PRUEBAS.md`](docs/PRUEBAS.md)
- Instrucciones de uso: [`README.md`](README.md)
