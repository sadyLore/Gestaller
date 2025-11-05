package com.example.gestaller.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;

public class AddClientActivity extends AppCompatActivity {
    private EditText etName, etPhone, etAddress;
    private Button btnSave, btnCancel;
    private ImageView ivThemeToggle;
    private ClientRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        repository = new ClientRepository(this.getApplication());

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        ivThemeToggle = findViewById(R.id.ivThemeToggle);

        updateThemeIcon(); // Actualizar el ícono al iniciar

        // Botón para cambiar el tema
        ivThemeToggle.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            recreate(); // Reinicia la actividad para aplicar el nuevo tema
        });

        // Botón Cancelar: vuelve a la pantalla anterior
        btnCancel.setOnClickListener(v -> finish());

        // Botón Guardar: valida e inserta el cliente
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (!name.isEmpty()) {
                repository.insert(new Client(name, phone, address));
                finish(); // vuelve a la lista después de guardar
            } else {
                etName.setError("El nombre es obligatorio");
            }
        });
    }

    private void updateThemeIcon() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            ivThemeToggle.setImageResource(R.drawable.ic_dark_mode); // Icono de luna
        } else {
            ivThemeToggle.setImageResource(R.drawable.ic_light_mode); // Icono de sol
        }
    }
}
