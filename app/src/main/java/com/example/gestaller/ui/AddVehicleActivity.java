package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.VehicleRepository;

public class AddVehicleActivity extends AppCompatActivity {

    private EditText etBrand, etModel;
    private Button btnSave, btnCancel;
    private TextView tvTitle;
    private VehicleRepository repository;
    private int vehicleId = -1;

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

        // Comprobar si estamos en modo edición
        if (getIntent() != null && getIntent().hasExtra("vehicleId")) {
            // Modo Edición
            tvTitle.setText("Editar Vehículo");
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            etBrand.setText(getIntent().getStringExtra("brand"));
            etModel.setText(getIntent().getStringExtra("model"));
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
}
