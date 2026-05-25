/**
 * TaskManager exposición — tema claro/oscuro y navegación por teclado
 */
(function () {
  const THEME_KEY = "tm-expo-theme";
  const slides = [
    "index.html",
    "arquitectura.html",
    "casos-uso.html",
    "mockups.html",
    "descripciones.html",
  ];

  function getCurrentIndex() {
    const name = window.location.pathname.split("/").pop() || "index.html";
    const i = slides.indexOf(name);
    return i >= 0 ? i : 0;
  }

  function applyTheme(theme) {
    const root = document.documentElement;
    if (theme === "dark") {
      root.classList.add("theme-dark");
    } else {
      root.classList.remove("theme-dark");
    }
    localStorage.setItem(THEME_KEY, theme);
    const btn = document.querySelector("[data-theme-toggle]");
    if (btn) {
      btn.setAttribute("aria-label", theme === "dark" ? "Tema claro" : "Tema oscuro");
      btn.textContent = theme === "dark" ? "☀" : "☾";
    }
    document.dispatchEvent(new CustomEvent("tm-theme-change", { detail: { theme } }));
  }

  function initTheme() {
    const saved = localStorage.getItem(THEME_KEY);
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    applyTheme(saved === "dark" || saved === "light" ? saved : prefersDark ? "dark" : "light");
  }

  function toggleTheme() {
    const next = document.documentElement.classList.contains("theme-dark")
      ? "light"
      : "dark";
    applyTheme(next);
  }

  function goSlide(delta) {
    const i = getCurrentIndex();
    const next = Math.min(Math.max(i + delta, 0), slides.length - 1);
    if (next !== i) {
      window.location.href = slides[next];
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
    initTheme();
    document.querySelectorAll("[data-theme-toggle]").forEach(function (el) {
      el.addEventListener("click", toggleTheme);
    });
  });

  document.addEventListener("keydown", function (e) {
    if (e.target && (e.target.tagName === "INPUT" || e.target.tagName === "TEXTAREA")) {
      return;
    }
    if (e.key === "ArrowRight" || e.key === "PageDown") {
      e.preventDefault();
      goSlide(1);
    } else if (e.key === "ArrowLeft" || e.key === "PageUp") {
      e.preventDefault();
      goSlide(-1);
    }
  });
})();
