<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class AdminUserSeeder extends Seeder
{
    /**
     * Crea (o actualiza) el usuario administrador del panel Filament.
     * Idempotente: se puede ejecutar en cada despliegue sin duplicar usuarios.
     */
    public function run(): void
    {
        User::updateOrCreate(
            ['email' => config('filament_admin.email')],
            [
                'name' => config('filament_admin.name'),
                'password' => Hash::make(config('filament_admin.password')),
            ],
        );
    }
}
