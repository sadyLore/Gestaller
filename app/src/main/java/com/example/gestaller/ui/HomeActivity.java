package com.example.gestaller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import com.example.gestaller.data.repository.VehicleRepository;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.example.gestaller.ui.adapter.WorkOrderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAddWork;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerWorkOrders;
    private WorkOrderAdapter workOrderAdapter;
    private WorkOrderRepository workOrderRepository;
    private EditText etSearch;
    private TextView tvNoResults;
    private TextView tvWorkOrdersTitle;
    private LiveData<List<WorkOrder>> workOrdersLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        fabAddWork = findViewById(R.id.fabAddWork);
        recyclerWorkOrders = findViewById(R.id.recyclerWorkOrders);
        etSearch = findViewById(R.id.etSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        tvWorkOrdersTitle = findViewById(R.id.tvWorkOrdersTitle);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        workOrderRepository = new WorkOrderRepository(getApplication());

        findViewById(R.id.btnMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        updateThemeIcon(); // Actualizar el ícono al iniciar

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
            } else if (id == R.id.nav_theme_toggle) {
                toggleTheme(); // Lógica para cambiar el tema
            }
            drawerLayout.closeDrawers();
            return true;
        });

        fabAddWork.setOnClickListener(v -> showAddWorkOrderDialog());

        setupRecyclerView();
        setupSearch();
    }

    private void setupRecyclerView() {
        recyclerWorkOrders.setLayoutManager(new LinearLayoutManager(this));
        workOrderAdapter = new WorkOrderAdapter(new ArrayList<>(), workOrderRepository, false);
        recyclerWorkOrders.setAdapter(workOrderAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadWorkOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadWorkOrders("");
    }

    private void loadWorkOrders(String query) {
        boolean isSearching = !query.trim().isEmpty();

        if (workOrdersLiveData != null) {
            workOrdersLiveData.removeObservers(this);
        }

        if (isSearching) {
            tvWorkOrdersTitle.setText("Resultados");
            workOrdersLiveData = workOrderRepository.searchWorkOrders(query);
        } else {
            tvWorkOrdersTitle.setText("Trabajos recientes");
            workOrdersLiveData = workOrderRepository.getAllWorkOrders();
        }

        workOrdersLiveData.observe(this, workOrders -> {
            boolean hasResults = workOrders != null && !workOrders.isEmpty();

            if (hasResults) {
                recyclerWorkOrders.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);

                if (!isSearching && workOrders.size() > 10) {
                    workOrderAdapter.updateData(workOrders.subList(0, 10));
                } else {
                    workOrderAdapter.updateData(workOrders);
                }
            } else {
                recyclerWorkOrders.setVisibility(View.GONE);
                tvNoResults.setVisibility(View.VISIBLE);
                workOrderAdapter.updateData(new ArrayList<>());
            }
        });
    }

    private void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        recreate(); // Reinicia la actividad para aplicar el nuevo tema
    }

    private void updateThemeIcon() {
        MenuItem themeItem = navigationView.getMenu().findItem(R.id.nav_theme_toggle);
        if (themeItem != null) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                themeItem.setIcon(R.drawable.ic_dark_mode); // Icono de luna
            } else {
                themeItem.setIcon(R.drawable.ic_light_mode); // Icono de sol
            }
        }
    }

    private void showAddWorkOrderDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.activity_add_workorder, null);
            builder.setView(view);
            AlertDialog dialog = builder.create();

            EditText etClientName = view.findViewById(R.id.etClientName);
            EditText etPlate = view.findViewById(R.id.etPlate);
            EditText etNotes = view.findViewById(R.id.etNotes);
            LinearLayout servicesContainer = view.findViewById(R.id.servicesContainer);
            Spinner spBrand = view.findViewById(R.id.spBrand);
            Spinner spModel = view.findViewById(R.id.spModel);
            Button btnSave = view.findViewById(R.id.btnSave);
            Button btnCancel = view.findViewById(R.id.btnCancel);

            WorkOrderRepository workRepo = new WorkOrderRepository(getApplication());
            ServiceTemplateRepository serviceRepo = new ServiceTemplateRepository(getApplication());
            VehicleRepository vehicleRepo = new VehicleRepository(getApplication());

            vehicleRepo.getAll().observe(this, vehicleList -> {
                if (vehicleList != null && !vehicleList.isEmpty()) {
                    List<String> brands = new ArrayList<>();
                    for (Vehicle vehicle : vehicleList) brands.add(vehicle.getBrand());

                    Set<String> uniqueBrands = new LinkedHashSet<>(brands);
                    brands.clear();
                    brands.addAll(uniqueBrands);

                    ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            brands
                    );
                    brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spBrand.setAdapter(brandAdapter);

                    spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedBrand = brands.get(position);
                            List<String> models = new ArrayList<>();

                            for (Vehicle vehicle : vehicleList) {
                                if (vehicle.getBrand().equals(selectedBrand)) {
                                    models.add(vehicle.getModel());
                                }
                            }

                            ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(
                                    HomeActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    models
                            );
                            modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spModel.setAdapter(modelAdapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            });

            serviceRepo.getAllTemplates().observe(this, services -> {
                servicesContainer.removeAllViews();
                for (ServiceTemplate s : services) {
                    CheckBox cb = new CheckBox(this);
                    cb.setText(s.getName());
                    cb.setTextColor(getResources().getColor(R.color.black));
                    servicesContainer.addView(cb);
                }
            });

            btnSave.setOnClickListener(view1 -> {
                List<String> selectedServices = new ArrayList<>();
                for (int i = 0; i < servicesContainer.getChildCount(); i++) {
                    CheckBox cb = (CheckBox) servicesContainer.getChildAt(i);
                    if (cb.isChecked()) selectedServices.add(cb.getText().toString());
                }

                String servicesText = String.join(", ", selectedServices);
                String notes = etNotes.getText().toString().trim();
                String clientName = etClientName.getText().toString().trim();
                String plateText = etPlate.getText().toString().trim();
                long date = System.currentTimeMillis();

                String selectedBrand = (String) spBrand.getSelectedItem();
                String selectedModel = (String) spModel.getSelectedItem();

                if (clientName.isEmpty() || plateText.isEmpty()) {
                    Toast.makeText(this, "⚠️ Completá todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedBrand == null || selectedModel == null) {
                    Toast.makeText(this, "⚠️ Seleccioná marca y modelo. Si no hay, agregalos desde el menú.", Toast.LENGTH_LONG).show();
                    return;
                }

                WorkOrder order = new WorkOrder();
                order.setClientName(clientName);
                order.setVehicleBrand(selectedBrand);
                order.setVehicleModel(selectedModel);
                order.setVehiclePlate(plateText);
                order.setServices(servicesText);
                order.setNotes(notes);
                order.setTotalPrice(0.0);
                order.setDate(date);

                workRepo.insert(order);
                Toast.makeText(this, "✅ Trabajo agregado correctamente", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            btnCancel.setOnClickListener(v -> dialog.dismiss());

            dialog.show();

        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir ventana: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
