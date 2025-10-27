package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.VehicleRepository;

public class AddVehicleActivity extends AppCompatActivity {

    private EditText etBrand, etModel;
    private Button btnSave, btnCancel;
    private VehicleRepository repository;
    private int vehicleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        repository = new VehicleRepository(getApplication());

        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Si venimos de “Editar”, cargar datos
        if (getIntent() != null && getIntent().hasExtra("vehicleId")) {
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
                vehicle.setId(vehicleId);
                repository.update(vehicle);
            } else {
                repository.insert(vehicle);
            }

            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
