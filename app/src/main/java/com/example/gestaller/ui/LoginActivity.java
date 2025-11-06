package com.example.gestaller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.gestaller.R;
import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.entity.User;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private Button btnFingerprint;
    private TallerDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnFingerprint = findViewById(R.id.btnFingerprint);

        db = TallerDatabase.getDatabase(this);

        // Crear usuarios por defecto (solo una vez)
        new Thread(() -> {
            try {
                if (db.userDao().getAllUsers().isEmpty()) {
                    db.userDao().insert(new User("admin", "1234", "propietario"));
                    db.userDao().insert(new User("colab", "1234", "colaborador"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 🔹 LOGIN CON USUARIO Y CONTRASEÑA
        btnLogin.setOnClickListener(v -> loginUser());

        // 🔹 AUTENTICACIÓN POR HUELLA DIGITAL
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricManager biometricManager = BiometricManager.from(this);

        // Usar solo huella (sin PIN del sistema)
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG;

        if (biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
            BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            Toast.makeText(getApplicationContext(), "Huella verificada ✅", Toast.LENGTH_SHORT).show();

                            SharedPreferences prefs = getSharedPreferences("GestallerPrefs", MODE_PRIVATE);
                            String role = prefs.getString("userRole", "propietario");

                            if (role.equalsIgnoreCase("propietario")) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, EmployeeHomeActivity.class));
                            }
                            finish();
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                                    errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                                Toast.makeText(getApplicationContext(),
                                        "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Toast.makeText(getApplicationContext(), "Huella no reconocida ❌", Toast.LENGTH_SHORT).show();
                        }
                    });

            // ✅ Solo huella, con botón "Cancelar"
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Gestaller")
                    .setSubtitle("Toca el sensor de huellas dactilares")
                    .setDescription("Usá tu huella digital para ingresar a tu cuenta")
                    .setNegativeButtonText("Cancelar")
                    .setAllowedAuthenticators(authenticators)
                    .build();

            btnFingerprint.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
        } else {
            // Oculta el botón si el dispositivo no tiene lector de huella
            btnFingerprint.setVisibility(View.GONE);
        }
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User user = db.userDao().login(username, password);

            if (user != null) {
                SharedPreferences prefs = getSharedPreferences("GestallerPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userRole", user.getRole());
                editor.apply();
            }

            runOnUiThread(() -> {
                if (user != null) {
                    Toast.makeText(this, "Bienvenido " + user.getRole(), Toast.LENGTH_SHORT).show();

                    if (user.getRole().equalsIgnoreCase("propietario")) {
                        startActivity(new Intent(this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(this, EmployeeHomeActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Contraseña o nombre de usuario incorrectos.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
