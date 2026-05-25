/**
 * Inicializa Mermaid según tema al cargar la página.
 */
(function () {
  function getMermaidTheme() {
    return document.documentElement.classList.contains("theme-dark") ? "dark" : "neutral";
  }

  document.addEventListener("DOMContentLoaded", function () {
    if (typeof mermaid === "undefined") {
      return;
    }
    mermaid.initialize({
      startOnLoad: false,
      theme: getMermaidTheme(),
      securityLevel: "strict",
      fontFamily: "Inter, system-ui, sans-serif",
    });
    mermaid.run({ querySelector: ".mermaid" }).catch(function () {
      /* estático: sin telemetría */
    });
  });
})();
