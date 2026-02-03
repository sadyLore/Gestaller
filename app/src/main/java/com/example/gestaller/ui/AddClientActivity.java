package com.example.gestaller.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;

import java.util.ArrayList;

public class AddClientActivity extends AppCompatActivity {
    private EditText etName, etPhone, etAddress;
    private Button btnSave, btnCancel;
    private ImageView ivThemeToggle;
    private ClientRepository repository;

    private ActivityResultLauncher<Intent> voiceRecognitionLauncher;

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

        setupVoiceRecognition();
        updateThemeIcon();

        // Comprobar si venimos por comando de voz
        if (getIntent() != null && "AWAITING_DATA".equals(getIntent().getStringExtra("VOICE_MODE"))) {
            getIntent().removeExtra("VOICE_MODE");
            startVoiceRecognition();
        }

        ivThemeToggle.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            recreate();
        });

        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (!name.isEmpty()) {
                repository.insert(new Client(name, phone, address));
                finish();
            } else {
                etName.setError("El nombre es obligatorio");
            }
        });
    }

    private void setupVoiceRecognition() {
        voiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            processClientData(results.get(0));
                        }
                    }
                }
        );
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-419");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dime el nombre y el teléfono del cliente");
        try {
            voiceRecognitionLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    private void processClientData(String text) {
        if (text == null || text.isEmpty()) return;
        
        // Lógica simple de extracción:
        // Teléfono: todo lo que sean números
        String phone = text.replaceAll("[^0-9]", "");
        // Nombre: el resto del texto
        String name = text.replaceAll("[0-9]", "").trim();

        if (!name.isEmpty()) {
            etName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
        }
        if (!phone.isEmpty()) {
            etPhone.setText(phone);
        }
    }

    private void updateThemeIcon() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            ivThemeToggle.setImageResource(R.drawable.ic_dark_mode);
        } else {
            ivThemeToggle.setImageResource(R.drawable.ic_light_mode);
        }
    }
}
