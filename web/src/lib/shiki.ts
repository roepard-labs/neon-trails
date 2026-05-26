import type { HighlighterCore } from 'shiki/core'
import { createHighlighterCore } from 'shiki/core'
import { createOnigurumaEngine } from 'shiki/engine/oniguruma'

/**
 * Lenguajes que aparecen en la presentación — uno por cada capa del monolito
 * (juego Java, backend PHP/Laravel, frontend Vue/TS, infra Docker/nginx/SQL +
 * herramientas toml/make/yaml). Solo se usa para `normalizeLang`.
 */
export const SHIKI_LANGS = [
  'java',
  'php',
  'typescript',
  'vue',
  'bash',
  'nginx',
  'docker',
  'sql',
  'ini',
  'yaml',
  'json',
  'toml',
  'make',
  'xml',
] as const

export type ShikiLang = (typeof SHIKI_LANGS)[number]

/** Tema oscuro tipo neón: navy profundo con acentos cyan/magenta (combina con la arena). */
export const SHIKI_THEME = 'tokyo-night'

/**
 * Alias amistosos → lenguaje real de Shiki. Permite que las plantillas escriban
 * `lang="ts"` o `lang="dockerfile"` sin preocuparse del ID canónico.
 */
const LANG_ALIASES: Record<string, ShikiLang> = {
  ts: 'typescript',
  tsx: 'typescript',
  js: 'typescript',
  sh: 'bash',
  shell: 'bash',
  dockerfile: 'docker',
  compose: 'yaml',
  yml: 'yaml',
  conf: 'nginx',
  env: 'ini',
  makefile: 'make',
}

export function normalizeLang(lang: string): ShikiLang {
  const lower = lang.toLowerCase()
  if ((SHIKI_LANGS as readonly string[]).includes(lower)) return lower as ShikiLang
  return LANG_ALIASES[lower] ?? 'bash'
}

// Singleton perezoso con la API de "núcleo" de Shiki: en lugar de importar el
// bundle completo (que arrastra ~200 gramáticas y emite un chunk por cada una),
// se cargan SOLO los 13 lenguajes que usamos. Cada `import('shiki/langs/X.mjs')`
// trae además sus lenguajes embebidos (vue → html/css/ts/js; php → html/sql…),
// así que el resaltado sigue siendo completo con un bundle mucho menor.
let highlighterPromise: Promise<HighlighterCore> | null = null

export function getHighlighter(): Promise<HighlighterCore> {
  if (!highlighterPromise) {
    highlighterPromise = createHighlighterCore({
      themes: [import('shiki/themes/tokyo-night.mjs')],
      langs: [
        import('shiki/langs/java.mjs'),
        import('shiki/langs/php.mjs'),
        import('shiki/langs/typescript.mjs'),
        import('shiki/langs/vue.mjs'),
        import('shiki/langs/bash.mjs'),
        import('shiki/langs/nginx.mjs'),
        import('shiki/langs/docker.mjs'),
        import('shiki/langs/sql.mjs'),
        import('shiki/langs/ini.mjs'),
        import('shiki/langs/yaml.mjs'),
        import('shiki/langs/json.mjs'),
        import('shiki/langs/toml.mjs'),
        import('shiki/langs/make.mjs'),
        import('shiki/langs/xml.mjs'),
      ],
      engine: createOnigurumaEngine(import('shiki/wasm')),
    })
  }
  return highlighterPromise
}

/**
 * Resalta `code` y devuelve el HTML `<pre class="shiki">…</pre>` listo para
 * `v-html`. Shiki escapa el contenido internamente.
 */
export async function highlight(code: string, lang: string): Promise<string> {
  const hl = await getHighlighter()
  return hl.codeToHtml(code, {
    lang: normalizeLang(lang),
    theme: SHIKI_THEME,
  })
}
