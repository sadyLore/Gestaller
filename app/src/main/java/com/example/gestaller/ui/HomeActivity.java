package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestaller.R;

public class HomeActivity extends AppCompatActivity {

    private Button btnClientes, btnVehiculos, btnTrabajos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnClientes = findViewById(R.id.btnClientes);
        btnVehiculos = findViewById(R.id.btnVehiculos);
        btnTrabajos = findViewById(R.id.btnTrabajos);

        btnClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ClientListActivity.class));
            }
        });

        btnVehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, VehicleListActivity.class));
            }
        });

        btnTrabajos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WorkOrderListActivity.class));
            }
        });
    }
}
