import globals from "globals";
import tseslint from "typescript-eslint";
import pluginVue from "eslint-plugin-vue";
import css from "@eslint/css";
import { defineConfig } from "eslint/config";

export default defineConfig([
  { ignores: ["node_modules/**", "dist/**", ".claude/**", ".agents/**", "docs/**", "src/style.css"] },
  { files: ["**/*.{js,mjs,cjs,ts,mts,cts,vue}"], languageOptions: { globals: globals.browser } },
  tseslint.configs.recommended,
  pluginVue.configs["flat/essential"],
  { files: ["**/*.vue"], languageOptions: { parserOptions: { parser: tseslint.parser } } },
  {
    files: ["src/components/ui/**/*.vue"],
    rules: { "vue/multi-word-component-names": "off" },
  },
  { files: ["**/*.css"], plugins: { css }, language: "css/css" },
]);
