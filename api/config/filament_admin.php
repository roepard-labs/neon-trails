<?php

return [

    /*
    |--------------------------------------------------------------------------
    | Usuario administrador del panel Filament
    |--------------------------------------------------------------------------
    |
    | Credenciales del administrador que AdminUserSeeder crea/actualiza al
    | desplegar. En producción se inyectan por variables de entorno desde
    | Dokploy; nunca se hardcodean credenciales reales aquí.
    |
    */

    'name' => env('FILAMENT_ADMIN_NAME', 'Neon Admin'),

    'email' => env('FILAMENT_ADMIN_EMAIL', 'admin@neon-trails.local'),

    'password' => env('FILAMENT_ADMIN_PASSWORD', 'password'),

];
