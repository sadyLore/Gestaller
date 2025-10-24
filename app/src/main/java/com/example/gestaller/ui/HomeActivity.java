package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gestaller.R;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton btnMenu;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenu = findViewById(R.id.btnMenu);
        navigationView = findViewById(R.id.navigationView);

        // 🔹 Abrir el Drawer al presionar el ícono hamburguesa
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // 🔹 Acciones de los ítems del Drawer
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
    }
}
