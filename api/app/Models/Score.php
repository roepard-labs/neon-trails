<?php

namespace App\Models;

use App\Enums\MatchResult;
use Database\Factories\ScoreFactory;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Score extends Model
{
    /** @use HasFactory<ScoreFactory> */
    use HasFactory;

    /** @var list<string> */
    protected $fillable = [
        'player_name',
        'score',
        'result',
        'match_played_at',
    ];

    /**
     * Ranking del leaderboard: mayor puntaje primero, desempate por antigüedad.
     */
    public function scopeRanked(Builder $query): void
    {
        $query->orderByDesc('score')->orderBy('created_at');
    }

    /**
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'score' => 'integer',
            'result' => MatchResult::class,
            'match_played_at' => 'datetime',
        ];
    }
}
