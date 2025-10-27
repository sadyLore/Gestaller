package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.repository.VehicleRepository;
import com.example.gestaller.ui.adapter.VehicleAdapter;
import com.example.gestaller.data.local.entity.Vehicle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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

        // 🔹 Observar los vehículos en la base de datos
        vehicleRepository.getAll().observe(this, vehicles -> {
            if (vehicles != null) {
                adapter.setVehicles(vehicles);
            }
        });

        // 🔹 Botón para agregar vehículo
        FloatingActionButton fab = findViewById(R.id.fabAddVehicle);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVehicleActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔹 Refrescar lista al volver de AddVehicleActivity
        vehicleRepository.getAll().observe(this, vehicles -> {
            if (vehicles != null) {
                adapter.setVehicles(vehicles);
            }
        });
    }
}
