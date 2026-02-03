package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.VehicleRepository;

import java.util.ArrayList;

public class AddVehicleActivity extends AppCompatActivity {

    private EditText etBrand, etModel;
    private Button btnSave, btnCancel;
    private TextView tvTitle;
    private VehicleRepository repository;
    private int vehicleId = -1;

    private ActivityResultLauncher<Intent> voiceRecognitionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        repository = new VehicleRepository(getApplication());

        // Asignar vistas
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        tvTitle = findViewById(R.id.tvTitle);

        setupVoiceRecognition();

        // Comprobar si estamos en modo edición
        if (getIntent() != null && getIntent().hasExtra("vehicleId")) {
            // Modo Edición
            tvTitle.setText("Editar Vehículo");
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            etBrand.setText(getIntent().getStringExtra("brand"));
            etModel.setText(getIntent().getStringExtra("model"));
        }

        // Comprobar si venimos por comando de voz
        if (getIntent() != null && "AWAITING_DATA".equals(getIntent().getStringExtra("VOICE_MODE"))) {
            getIntent().removeExtra("VOICE_MODE");
            startVoiceRecognition();
        }

        btnSave.setOnClickListener(v -> {
            String brand = etBrand.getText().toString().trim();
            String model = etModel.getText().toString().trim();

            if (brand.isEmpty()) {
                etBrand.setError("La marca es obligatoria");
                return;
            }

            Vehicle vehicle = new Vehicle(brand, model);
            if (vehicleId != -1) {
                // Actualizar vehículo existente
                vehicle.setId(vehicleId);
                repository.update(vehicle);
            } else {
                // Insertar nuevo vehículo
                repository.insert(vehicle);
            }

            finish(); // Cerrar la actividad
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupVoiceRecognition() {
        voiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            processVehicleData(results.get(0));
                        }
                    }
                }
        );
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-419");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dime la marca y el modelo del vehículo");
        try {
            voiceRecognitionLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    private void processVehicleData(String text) {
        if (text == null || text.isEmpty()) return;
        
        // Lógica simple: separar por espacios
        String[] parts = text.split(" ", 2);
        if (parts.length > 0) {
            etBrand.setText(parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1));
        }
        if (parts.length > 1) {
            etModel.setText(parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1));
        }
    }
}
