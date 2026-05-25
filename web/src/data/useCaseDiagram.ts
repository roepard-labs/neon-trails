export const useCaseDiagramMermaid = `flowchart TB
  classDef actor fill:#fff,stroke:#3b58c4,stroke-width:2px,color:#0f172a,font-weight:bold
  classDef caso fill:#eef2ff,stroke:#3b58c4,stroke-width:1.5px,color:#0f172a
  classDef sec fill:#f8fafc,stroke:#94a3b8,stroke-width:1.5px,color:#334155
  classDef inc fill:#fff7ed,stroke:#c2410c,stroke-width:1.5px,stroke-dasharray:4 3,color:#7c2d12

  J(["🎮 Jugador"]):::actor
  A(["🛡️ Administrador"]):::actor
  L(["🗄️ Servicio leaderboard"]):::actor

  subgraph SYS["🏟️ Sistema Neon Trails"]
    direction TB
    UC1(("CU-PART-01<br/>Iniciar partida<br/>(P)")):::caso
    UC2(("CU-MOV-02<br/>Conducir moto<br/>(P)")):::caso
    UC3(("CU-DISC-03<br/>Disparar disco<br/>(P)")):::caso
    UC4(("CU-MOTO-04<br/>Modo moto 5 s<br/>(P)")):::caso
    UC5(("CU-SCORE-05<br/>Registrar puntaje<br/>(S)")):::sec
    UC6(("CU-RANK-06<br/>Consultar ranking<br/>(S)")):::sec
    UC7(("CU-ADMIN-07<br/>Administrar puntajes<br/>(S)")):::sec
    UC8(("CU-CICLO-08<br/>Jugar de nuevo<br/>(S)")):::sec
    UCc(("Detectar<br/>colisión")):::inc
    UCa(("Auth<br/>Filament")):::inc
  end

  J --- UC1
  J --- UC2
  J --- UC3
  J --- UC4
  J --- UC5
  J --- UC6
  J --- UC8
  A --- UC7
  L --- UC5
  L --- UC6
  L --- UC7

  UC2 -. "«include»" .-> UCc
  UC3 -. "«include»" .-> UCc
  UC4 -. "«extend»" .-> UC2
  UC7 -. "«include»" .-> UCa
  UC5 -. "evento" .-> UC6

  linkStyle 0,1,2,3,4,5,6,7,8,9,10 stroke:#3b58c4,stroke-width:1.5px
  linkStyle 11,12,14 stroke:#c2410c,stroke-width:1.2px
  linkStyle 13 stroke:#64748b,stroke-width:1px
`

export const useCaseDiagramResumen = {
  actores: [
    {
      actor: 'Jugador',
      casos: [
        'CU-PART-01 Iniciar partida',
        'CU-MOV-02 Conducir la moto',
        'CU-DISC-03 Disparar disco',
        'CU-MOTO-04 Modo moto 5 s',
        'CU-SCORE-05 Registrar puntaje',
        'CU-RANK-06 Consultar ranking',
        'CU-CICLO-08 Jugar de nuevo',
      ],
    },
    {
      actor: 'Administrador / Servicio leaderboard',
      casos: [
        'CU-ADMIN-07 Administrar puntajes',
        'CU-SCORE-05 Registrar puntaje',
        'CU-RANK-06 Consultar ranking',
      ],
    },
  ],
  relaciones: [
    {
      tipo: '«include»',
      desde: 'CU-MOV-02 Conducir la moto',
      hacia: 'Detectar colisión',
      descripcion:
        'Cada movimiento dentro de GameState.tick verifica colisiones con bordes, rivales y discos antes de confirmar la posición.',
    },
    {
      tipo: '«include»',
      desde: 'CU-DISC-03 Disparar disco',
      hacia: 'Detectar colisión',
      descripcion:
        'El recorrido del disco evalúa rebotes en los bordes e impactos contra el rival en la fase de resolución del tick.',
    },
    {
      tipo: '«extend»',
      desde: 'CU-MOTO-04 Modo moto 5 s',
      hacia: 'CU-MOV-02 Conducir la moto',
      descripcion:
        'El modo moto extiende la conducción: durante 5 s aumenta la velocidad ×1.75 sin cambiar el mecanismo de movimiento.',
    },
    {
      tipo: '«include»',
      desde: 'CU-ADMIN-07 Administrar puntajes',
      hacia: 'Auth Filament',
      descripcion:
        'El acceso al CRUD del leaderboard exige autenticarse en el panel Filament (canAccessPanel).',
    },
    {
      tipo: 'evento',
      desde: 'CU-SCORE-05 Registrar puntaje',
      hacia: 'CU-RANK-06 Consultar ranking',
      descripcion:
        'Cada POST /api/scores agrega una entrada que se refleja al instante en la consulta del ranking (GET /api/scores).',
    },
  ],
}
