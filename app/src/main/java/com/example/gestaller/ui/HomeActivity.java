package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestaller.R;

public class HomeActivity extends AppCompatActivity {

    Button btnClientes, btnVehiculos, btnTrabajos, btnServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnClientes = findViewById(R.id.btnClientes);
        btnVehiculos = findViewById(R.id.btnVehiculos);
        btnTrabajos = findViewById(R.id.btnTrabajos);
        btnServicios = findViewById(R.id.btnServicios);

        btnClientes.setOnClickListener(v -> startActivity(new Intent(this, ClientListActivity.class)));
        btnVehiculos.setOnClickListener(v -> startActivity(new Intent(this, VehicleListActivity.class)));
        btnTrabajos.setOnClickListener(v -> startActivity(new Intent(this, WorkOrderListActivity.class)));
        btnServicios.setOnClickListener(v -> startActivity(new Intent(this, ServiceTemplateListActivity.class)));
    }
}
