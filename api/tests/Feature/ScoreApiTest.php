<?php

use App\Enums\MatchResult;
use App\Models\Score;
use Illuminate\Foundation\Testing\RefreshDatabase;

use function Pest\Laravel\getJson;
use function Pest\Laravel\postJson;

uses(RefreshDatabase::class);

it('devuelve el ranking ordenado por puntaje descendente', function () {
    Score::factory()->create(['player_name' => 'Bajo', 'score' => 5]);
    Score::factory()->create(['player_name' => 'Alto', 'score' => 99]);
    Score::factory()->create(['player_name' => 'Medio', 'score' => 50]);

    getJson('/api/scores')
        ->assertOk()
        ->assertJsonPath('data.0.player_name', 'Alto')
        ->assertJsonPath('data.1.player_name', 'Medio')
        ->assertJsonPath('data.2.player_name', 'Bajo');
});

it('respeta el parámetro limit del ranking', function () {
    Score::factory()->count(15)->create();

    getJson('/api/scores?limit=5')
        ->assertOk()
        ->assertJsonCount(5, 'data');
});

it('registra un puntaje válido enviado por el juego', function () {
    postJson('/api/scores', [
        'player_name' => 'NeonRider',
        'score' => 42,
        'result' => MatchResult::Win->value,
    ])
        ->assertCreated()
        ->assertJsonPath('data.player_name', 'NeonRider')
        ->assertJsonPath('data.score', 42)
        ->assertJsonPath('data.result', 'win');

    expect(Score::where('player_name', 'NeonRider')->exists())->toBeTrue();
});

it('rechaza un puntaje inválido', function () {
    postJson('/api/scores', ['player_name' => '', 'score' => -1])
        ->assertUnprocessable()
        ->assertJsonValidationErrors(['player_name', 'score']);
});
