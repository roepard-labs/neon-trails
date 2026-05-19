# Contribuir a Neon Trails

¡Gracias por interesarte en contribuir! Este proyecto forma parte de un trabajo académico de **Técnicas de Programación**, por lo que tiene algunas particularidades.

## Reglas obligatorias del curso

Antes de contribuir, lee [**docs/rules/Proyecto Técnicas de Programación.pdf**](docs/rules/Proyecto%20Técnicas%20de%20Programación.pdf) y [**docs/rules/general.md**](docs/rules/general.md). El proyecto debe cumplir requisitos específicos de:

- Programación Orientada a Objetos
- Paquetes definidos
- Hilos y concurrencia
- Multimedia (sonidos, imágenes)
- Pantallas (menú, juego, ranking, game over)
- Persistencia

## Cómo contribuir

### Reportar bugs

1. Revisa que el bug no haya sido reportado ya en [Issues](https://github.com/jemgdevp/neon-trails/issues).
2. Usa la plantilla de **Bug Report** para crear un issue.
3. Incluye pasos para reproducir, comportamiento esperado vs. real, y capturas si aplica.

### Proponer features

1. Abre un issue con la plantilla de **Feature Request**.
2. Describe el problema que resuelve y cómo se alinea con los requisitos del curso.
3. Si la feature es grande, discútela antes de implementar.

### Pull Requests

1. Crea un branch desde `develop`: `git checkout -b feat/mi-cambio`.
2. Sigue las [convenciones del proyecto](AGENTS.md):
   - Comentarios en **español**
   - JavaDoc en APIs públicas
   - Cambios mínimos, sin refactors masivos
3. Asegúrate de que `make verify` pase (compila + tests + package).
4. Usa la plantilla de PR y completa el checklist.
5. Asigna un reviewer si aplica.

### Estilo de commits

Usa [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: descripción en español
fix: corrección de bug
docs: cambios en documentación
chore: tareas de mantenimiento
refactor: refactorización sin cambio funcional
test: añadir o modificar tests
```

## Configuración del entorno

```bash
# Requisitos: JDK 17 + Maven
git clone https://github.com/jemgdevp/neon-trails.git
cd neon-trails
make compile
make run
```

## Estructura del proyecto

| Capa | Ruta | Descripción |
|------|------|-------------|
| Entrada | `src/main/java/Main.java` | `main`, arranque en EDT |
| Lógica | `src/main/java/logic/` | Estado, entidades, reglas |
| Vista | `src/main/java/view/` | Ventana, panel, renderizado |
| Eventos | `src/main/java/events/` | Key Bindings (InputMap/ActionMap) |
| Pantallas | `src/main/java/view/screens/` | Welcome, NameInput, Game, GameOver |
| Tests | `src/test/java/` | JUnit 5 |
| Docs | `docs/` | Arquitectura, pruebas, reglas |

Consulta [AGENTS.md](AGENTS.md) para la guía completa de arquitectura y convenciones.
