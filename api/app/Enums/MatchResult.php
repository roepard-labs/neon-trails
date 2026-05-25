<?php

namespace App\Enums;

/**
 * Resultado de una partida desde la perspectiva de un jugador del leaderboard.
 */
enum MatchResult: string
{
    case Win = 'win';
    case Loss = 'loss';
    case Draw = 'draw';
}
