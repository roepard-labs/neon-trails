/**
 * Carga datos mock y rellena el DOM.
 * NOTE: En una fase posterior se puede sustituir por fetch a un backend o archivo generado por el juego.
 */

const FALLBACK_LEADERBOARD = {
  top3: [
    { name: "Offline_A", score: 120 },
    { name: "Offline_B", score: 95 },
    { name: "Offline_C", score: 72 },
  ],
};

async function loadJson(path) {
  try {
    const res = await fetch(path);
    if (!res.ok) {
      throw new Error(`No se pudo cargar ${path}`);
    }
    return res.json();
  } catch {
    // NOTE: Al abrir con file:// algunos navegadores bloquean fetch; se usa fallback.
    console.warn("Usando datos mock embebidos (fetch no disponible).");
    return FALLBACK_LEADERBOARD;
  }
}

function renderLeaderboard(entries) {
  const ol = document.getElementById("leaderboard");
  ol.innerHTML = "";
  for (const row of entries) {
    const li = document.createElement("li");
    li.textContent = `${row.name} — ${row.score} pts`;
    ol.appendChild(li);
  }
}

function mockMatchPulse() {
  const p1 = document.getElementById("p1-score");
  const p2 = document.getElementById("p2-score");
  let t = 0;
  setInterval(() => {
    t += 1;
    p1.textContent = String((t % 5) + 1);
    p2.textContent = String((t % 3) + 2);
  }, 1200);
}

async function main() {
  const data = await loadJson("./mock/leaderboard.json");
  renderLeaderboard(data.top3);
  mockMatchPulse();
}

main().catch((err) => {
  console.error(err);
  renderLeaderboard(FALLBACK_LEADERBOARD.top3);
});
