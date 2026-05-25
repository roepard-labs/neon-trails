<?php

namespace App\Http\Requests;

use App\Enums\MatchResult;
use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreScoreRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        return [
            'player_name' => ['required', 'string', 'max:40'],
            'score' => ['required', 'integer', 'min:0', 'max:100000'],
            'result' => ['nullable', Rule::enum(MatchResult::class)],
            'match_played_at' => ['nullable', 'date'],
        ];
    }
}
