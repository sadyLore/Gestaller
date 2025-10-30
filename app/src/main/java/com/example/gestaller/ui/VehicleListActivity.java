package com.example.gestaller.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.VehicleRepository;
import com.example.gestaller.ui.adapter.VehicleAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class VehicleListActivity extends AppCompatActivity {

    private RecyclerView recyclerVehicles;
    private VehicleAdapter adapter;
    private VehicleRepository vehicleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        recyclerVehicles = findViewById(R.id.recyclerVehicles);
        recyclerVehicles.setLayoutManager(new LinearLayoutManager(this));

        vehicleRepository = new VehicleRepository(getApplication());
        adapter = new VehicleAdapter(new ArrayList<>(), vehicleRepository);
        recyclerVehicles.setAdapter(adapter);

        vehicleRepository.getAll().observe(this, vehicles -> {
            if (vehicles != null) {
                adapter.setVehicles(vehicles);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabAddVehicle);
        fab.setOnClickListener(v -> showAddVehicleDialog());
    }

    private void showAddVehicleDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_vehicle, null);
        builder.setView(view);

        EditText etBrand = view.findViewById(R.id.etBrand);
        EditText etModel = view.findViewById(R.id.etModel);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String brand = etBrand.getText().toString();
            String model = etModel.getText().toString();

            if (brand.isEmpty() || model.isEmpty()) {
                Toast.makeText(this, "La marca y el modelo son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Vehicle vehicle = new Vehicle(brand, model);
            vehicleRepository.insert(vehicle);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
