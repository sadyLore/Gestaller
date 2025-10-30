package com.example.gestaller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAddWork;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        fabAddWork = findViewById(R.id.fabAddWork);
        prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);

        findViewById(R.id.btnMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        boolean darkMode = prefs.getBoolean("DarkMode", false);
        updateDarkModeMenuItem(darkMode);

        fabAddWork.setOnClickListener(v -> showAddWorkOrderDialog());
    }

    private void updateDarkModeMenuItem(boolean darkMode) {
        if (navigationView != null && navigationView.getMenu() != null) {
            MenuItem darkModeItem = navigationView.getMenu().findItem(R.id.nav_dark_mode);
            if (darkModeItem != null) {
                darkModeItem.setTitle(darkMode ? "Modo claro" : "Modo oscuro");
                darkModeItem.setIcon(darkMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode);
            }
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            startActivity(new Intent(this, ClientListActivity.class));
        } else if (id == R.id.nav_vehiculos) {
            startActivity(new Intent(this, VehicleListActivity.class));
        } else if (id == R.id.nav_servicios) {
            startActivity(new Intent(this, ServiceTemplateListActivity.class));
        } else if (id == R.id.nav_trabajos) {
            startActivity(new Intent(this, WorkOrderListActivity.class));
        } else if (id == R.id.nav_dark_mode) {
            toggleDarkMode();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void toggleDarkMode() {
        boolean darkMode = prefs.getBoolean("DarkMode", false);
        boolean newMode = !darkMode;

        prefs.edit().putBoolean("DarkMode", newMode).apply();

        AppCompatDelegate.setDefaultNightMode(
                newMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        updateDarkModeMenuItem(newMode);
        recreate();
    }

    private void showAddWorkOrderDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_work_order, null);
        builder.setView(view);

        EditText etClientName = view.findViewById(R.id.etClientName);
        EditText etPlate = view.findViewById(R.id.etPlate);
        EditText etNotes = view.findViewById(R.id.etNotes);
        LinearLayout servicesContainer = view.findViewById(R.id.servicesContainer);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        ServiceTemplateRepository serviceRepo = new ServiceTemplateRepository(getApplication());
        serviceRepo.getAllTemplates().observe(this, services -> {
            servicesContainer.removeAllViews();
            for (ServiceTemplate s : services) {
                CheckBox cb = new CheckBox(this);
                cb.setText(s.getName());
                cb.setTextColor(getResources().getColor(R.color.white)); // TODO: use theme color
                servicesContainer.addView(cb);
            }
        });

        btnSave.setOnClickListener(v -> {
            String clientName = etClientName.getText().toString();
            String plate = etPlate.getText().toString();
            String notes = etNotes.getText().toString();

            List<String> selectedServices = new ArrayList<>();
            for (int i = 0; i < servicesContainer.getChildCount(); i++) {
                CheckBox cb = (CheckBox) servicesContainer.getChildAt(i);
                if (cb.isChecked()) {
                    selectedServices.add(cb.getText().toString());
                }
            }

            if (clientName.isEmpty() || plate.isEmpty()) {
                Toast.makeText(this, "El nombre y la matrícula son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            WorkOrder workOrder = new WorkOrder();
            workOrder.setClientName(clientName);
            workOrder.setVehiclePlate(plate);
            workOrder.setNotes(notes);
            workOrder.setServices(String.join(", ", selectedServices));

            WorkOrderRepository workOrderRepo = new WorkOrderRepository(getApplication());
            workOrderRepo.insert(workOrder);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
