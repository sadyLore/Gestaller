package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.repository.VehicleRepository;
import com.example.gestaller.data.local.entity.Vehicle;

public class AddVehicleActivity extends AppCompatActivity {

    private EditText etBrand, etModel, etYear, etColor, etPlate;
    private Button btnSave, btnCancel;
    private VehicleRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        repository = new VehicleRepository(getApplication());

        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String brand = etBrand.getText().toString();
            String model = etModel.getText().toString();


            Vehicle vehicle = new Vehicle(brand, model);
            repository.insert(vehicle);
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
