<?php

namespace Database\Factories;

use App\Enums\MatchResult;
use App\Models\Score;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Score>
 */
class ScoreFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'player_name' => fake()->userName(),
            'score' => fake()->numberBetween(0, 50),
            'result' => fake()->randomElement(MatchResult::cases()),
            'match_played_at' => fake()->dateTimeBetween('-1 month'),
        ];
    }
}
