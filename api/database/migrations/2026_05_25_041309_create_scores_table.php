<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('scores', function (Blueprint $table) {
            $table->id();
            $table->string('player_name', 40);
            $table->unsignedInteger('score')->default(0);
            $table->string('result')->nullable();
            $table->timestamp('match_played_at')->nullable();
            $table->timestamps();

            // Ranking: mayores puntajes primero, desempate determinista por antigüedad.
            $table->index(['score', 'created_at']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('scores');
    }
};
