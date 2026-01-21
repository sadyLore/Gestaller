package com.example.gestaller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.example.gestaller.ui.adapter.WorkOrderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

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

        sharedPreferences = getSharedPreferences("GestallerPrefs", MODE_PRIVATE);
        workOrderRepository = new WorkOrderRepository(getApplication());

        findViewById(R.id.btnMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        updateThemeIcon();

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
                toggleTheme();
            } else if (id == R.id.nav_logout) {
                logoutUser();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        fabAddWork.setOnClickListener(v -> startActivity(new Intent(this, AddWorkOrderActivity.class)));

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
        recreate();
    }

    private void updateThemeIcon() {
        MenuItem themeItem = navigationView.getMenu().findItem(R.id.nav_theme_toggle);
        if (themeItem != null) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                themeItem.setIcon(R.drawable.ic_dark_mode);
            } else {
                themeItem.setIcon(R.drawable.ic_light_mode);
            }
        }
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userRole");
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
