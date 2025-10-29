package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gestaller.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAddWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        fabAddWork = findViewById(R.id.fabAddWork);

        // 🔹 Menú lateral
        findViewById(R.id.btnMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_clientes) {
                startActivity(new Intent(this, ClientListActivity.class));
            } else if (id == R.id.nav_vehiculos) {
                startActivity(new Intent(this, VehicleListActivity.class));
            } else if (id == R.id.nav_servicios) {
                startActivity(new Intent(this, ServiceTemplateListActivity.class));
            } else if (id == R.id.nav_trabajos) {
                startActivity(new Intent(this, WorkOrderListActivity.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // 🔹 Botón flotante para agregar trabajo (CORREGIDO)
        // Ahora inicia la actividad completa en lugar del diálogo
        fabAddWork.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddWorkOrderActivity.class);
            startActivity(intent);
        });
    }
}
