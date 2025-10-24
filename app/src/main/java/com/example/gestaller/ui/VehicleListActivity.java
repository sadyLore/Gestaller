package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestaller.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VehicleListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ✅ 1. Cargar el layout correcto
        setContentView(R.layout.activity_vehicle_list);

        // ✅ 2. Referenciar el botón correcto
        FloatingActionButton fab = findViewById(R.id.fabAddVehicle);

        // ✅ 3. Evitar NullPointer y abrir la pantalla de agregar vehículo
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddVehicleActivity.class);
                startActivity(intent);
            });
        }
    }
}
