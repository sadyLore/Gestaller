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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dime el nombre, el teléfono y dirección del cliente");
        try {
            voiceRecognitionLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    private void processClientData(String text) {
        if (text == null || text.isEmpty()) return;
        android.util.Log.d("VoiceParsing", "Original: " + text);

        String name = "";
        String phone = "";
        String address = "";

        // 1. Encontrar el Teléfono (primer bloque de 6 a 15 dígitos, acepta +, espacios o guiones)
        java.util.regex.Pattern phonePattern = java.util.regex.Pattern.compile("(\\+?[0-9][0-9\\s-]{4,13}[0-9])");
        java.util.regex.Matcher matcher = phonePattern.matcher(text);

        int phoneStart = -1;
        int phoneEnd = -1;

        if (matcher.find()) {
            phone = matcher.group(0).trim();
            phoneStart = matcher.start();
            phoneEnd = matcher.end();
        }

        // 2. Determinar posición de la palabra "dirección"
        String lowerText = text.toLowerCase();
        int dirIndex = lowerText.indexOf("dirección");
        if (dirIndex == -1) dirIndex = lowerText.indexOf("direccion");

        // 3. Extraer Nombre (texto al inicio antes de teléfono o dirección)
        int nameEnd = text.length();
        if (phoneStart != -1) nameEnd = Math.min(nameEnd, phoneStart);
        if (dirIndex != -1) nameEnd = Math.min(nameEnd, dirIndex);

        name = text.substring(0, nameEnd).trim();
        // Quitar palabras clave del nombre
        String[] keywords = {"telefono", "teléfono", "celular", "movil", "móvil", "direccion", "dirección"};
        for (String kw : keywords) {
            name = name.replaceAll("(?i)" + kw, "").trim();
        }

        // 4. Extraer Dirección
        if (dirIndex != -1) {
            // Caso A: Palabra clave "dirección" detectada
            address = text.substring(dirIndex + 9).trim();
        } else if (phoneEnd != -1 && phoneEnd < text.length()) {
            // Caso B: Fallback (lo que queda después del teléfono si tiene letras)
            String fallback = text.substring(phoneEnd).trim();
            if (fallback.matches(".*[a-zA-ZáéíóúÁÉÍÓÚ].*")) {
                address = fallback;
            }
        }

        android.util.Log.d("VoiceParsing", "Parsed -> Name: [" + name + "] Phone: [" + phone + "] Address: [" + address + "]");

        // 5. Rellenar campos reales con null-checks
        if (etName != null && !name.isEmpty()) {
            String capitalized = name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
            etName.setText(capitalized);
        }
        if (etPhone != null && !phone.isEmpty()) {
            etPhone.setText(phone.replaceAll("[\\s-]", ""));
        }
        if (etAddress != null && !address.isEmpty()) {
            etAddress.setText(address);
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
