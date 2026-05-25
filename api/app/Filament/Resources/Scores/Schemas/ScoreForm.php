<?php

namespace App\Filament\Resources\Scores\Schemas;

use App\Enums\MatchResult;
use Filament\Forms\Components\DateTimePicker;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\TextInput;
use Filament\Schemas\Schema;

class ScoreForm
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->components([
                TextInput::make('player_name')
                    ->required(),
                TextInput::make('score')
                    ->required()
                    ->numeric()
                    ->default(0),
                Select::make('result')
                    ->options(MatchResult::class),
                DateTimePicker::make('match_played_at'),
            ]);
    }
}
