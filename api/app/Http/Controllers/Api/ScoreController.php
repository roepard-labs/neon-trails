<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\StoreScoreRequest;
use App\Http\Resources\ScoreResource;
use App\Models\Score;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\AnonymousResourceCollection;
use Symfony\Component\HttpFoundation\Response;

class ScoreController extends Controller
{
    /**
     * Ranking del leaderboard (mayores puntajes primero).
     */
    public function index(Request $request): AnonymousResourceCollection
    {
        $limit = max(1, min($request->integer('limit', 10), 100));

        return ScoreResource::collection(
            Score::ranked()->limit($limit)->get()
        );
    }

    /**
     * Registra el puntaje de una partida (lo envía el juego al terminar).
     */
    public function store(StoreScoreRequest $request): JsonResponse
    {
        $score = Score::create($request->validated());

        return ScoreResource::make($score)
            ->response()
            ->setStatusCode(Response::HTTP_CREATED);
    }
}
