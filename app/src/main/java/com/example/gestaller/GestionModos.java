package com.example.gestaller;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Gestiona el modo claro/oscuro global de la aplicación.
 * Se ejecuta automáticamente al iniciar Gestaller.
 */
public class GestionModos extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Cargar preferencia guardada
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("DarkMode", false);

        // Aplicar el modo globalmente
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
