package com.example.gestaller.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import com.example.gestaller.data.repository.VehicleRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddWorkOrderActivity extends AppCompatActivity {

    private Spinner spBrand, spModel;
    private LinearLayout servicesContainer;
    private VehicleRepository vehicleRepo;
    private ServiceTemplateRepository serviceRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workorder);

        spBrand = findViewById(R.id.spBrand);
        spModel = findViewById(R.id.spModel);
        servicesContainer = findViewById(R.id.servicesContainer);

        vehicleRepo = new VehicleRepository(getApplication());
        serviceRepo = new ServiceTemplateRepository(getApplication());

        // 🔹 Cargar marcas y modelos desde Room
        vehicleRepo.getAll().observe(this, vehicles -> {
            if (vehicles == null || vehicles.isEmpty()) return;

            // 🔹 Lista de marcas sin duplicados
            Set<String> uniqueBrands = new LinkedHashSet<>();
            for (Vehicle v : vehicles) {
                uniqueBrands.add(v.getBrand());
            }

            List<String> brands = new ArrayList<>(uniqueBrands);

            // 🔹 Adaptador de marcas
            ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    brands
            );
            spBrand.setAdapter(brandAdapter);

            // 🔹 Listener para filtrar modelos al seleccionar una marca
            spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedBrand = brands.get(position);
                    List<String> models = new ArrayList<>();

                    for (Vehicle v : vehicles) {
                        if (v.getBrand().equalsIgnoreCase(selectedBrand)) {
                            if (!models.contains(v.getModel())) {
                                models.add(v.getModel());
                            }
                        }
                    }

                    // 🔹 Adaptador de modelos filtrados
                    ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(
                            AddWorkOrderActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            models
                    );
                    spModel.setAdapter(modelAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    spModel.setAdapter(null);
                }
            });
        });

        // 🔹 Cargar servicios dinámicamente y ordenarlos alfabéticamente
        serviceRepo.getAllTemplates().observe(this, serviceTemplates -> {
            servicesContainer.removeAllViews();

            if (serviceTemplates == null || serviceTemplates.isEmpty()) return;

            // Ordenar por nombre
            Collections.sort(serviceTemplates, Comparator.comparing(
                    ServiceTemplate::getName,
                    String.CASE_INSENSITIVE_ORDER
            ));

            for (ServiceTemplate service : serviceTemplates) {
                CheckBox cb = new CheckBox(AddWorkOrderActivity.this);
                cb.setText(service.getName());
                cb.setTextColor(getResources().getColor(R.color.black));
                cb.setTextSize(15);
                cb.setPadding(25, 10, 25, 10);
                servicesContainer.addView(cb);
            }
        });
    }
}
