<?php

use App\Http\Controllers\Api\ScoreController;
use Illuminate\Support\Facades\Route;

/*
 * Leaderboard de Neon Trails.
 * GET  /api/scores  → ranking público (consumido por la landing Vue).
 * POST /api/scores  → registro de puntaje (lo envía el juego Java al terminar la partida).
 */
Route::get('scores', [ScoreController::class, 'index']);
Route::post('scores', [ScoreController::class, 'store'])->middleware('throttle:30,1');
