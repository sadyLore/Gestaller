package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.entity.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TallerDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        db = TallerDatabase.getInstance(this);

        // Crear usuarios por defecto (solo una vez)
        new Thread(() -> {
            if (db.userDao().login("admin", "1234") == null) {
                db.userDao().insert(new User("admin", "1234", "propietario"));
                db.userDao().insert(new User("colab", "1234", "colaborador"));
            }
        }).start();

        btnLogin.setOnClickListener(v -> loginUser());
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
