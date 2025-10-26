package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.repository.VehicleRepository;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import java.util.ArrayList;
import java.util.List;

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

        // 🔹 Cargar marcas desde los vehículos guardados
        vehicleRepo.getAllVehicles().observe(this, vehicles -> {
            List<String> brands = new ArrayList<>();
            List<String> models = new ArrayList<>();

            for (Vehicle v : vehicles) {
                if (!brands.contains(v.getBrand())) brands.add(v.getBrand());
                if (!models.contains(v.getModel())) models.add(v.getModel());
            }

            ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
            brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spBrand.setAdapter(brandAdapter);

            ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
            modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spModel.setAdapter(modelAdapter);
        });

        // 🔹 Cargar servicios dinámicamente
        serviceRepo.getAllTemplates().observe(this, new Observer<List<ServiceTemplate>>() {
            @Override
            public void onChanged(List<ServiceTemplate> serviceTemplates) {
                servicesContainer.removeAllViews();
                for (ServiceTemplate service : serviceTemplates) {
                    CheckBox cb = new CheckBox(AddWorkOrderActivity.this);
                    cb.setText(service.getName());
                    cb.setTextColor(getResources().getColor(R.color.black));
                    cb.setPadding(12, 8, 12, 8);
                    servicesContainer.addView(cb);
                }
            }
        });
    }
}
