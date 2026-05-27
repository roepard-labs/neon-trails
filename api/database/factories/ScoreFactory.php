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
            'player_name' => $this->faker->userName(),
            'score' => $this->faker->numberBetween(0, 50),
            'result' => $this->faker->randomElement(MatchResult::cases()),
            'match_played_at' => $this->faker->dateTimeBetween('-1 month'),
        ];
    }
}
