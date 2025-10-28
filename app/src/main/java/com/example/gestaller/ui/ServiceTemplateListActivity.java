package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import com.example.gestaller.ui.adapter.ServiceTemplateAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ServiceTemplateListActivity extends AppCompatActivity {

    private RecyclerView recyclerServices;
    private ServiceTemplateAdapter adapter;
    private ServiceTemplateRepository serviceRepo;
    private FloatingActionButton fabAddService;
    private List<ServiceTemplate> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_template_list);

        recyclerServices = findViewById(R.id.recyclerServices);
        fabAddService = findViewById(R.id.fabAddService);
        recyclerServices.setLayoutManager(new LinearLayoutManager(this));

        serviceRepo = new ServiceTemplateRepository(getApplication());

        // 🔹 Cargar servicios desde Room
        serviceRepo.getAllTemplates().observe(this, services -> {
            if (services != null) {
                serviceList = services;
                adapter.updateData(serviceList);
            }
        });

        // 🔹 Configurar adaptador
        adapter = new ServiceTemplateAdapter(serviceList, serviceRepo);
        recyclerServices.setAdapter(adapter);

        // 🔹 Botón flotante para agregar nuevo servicio
        fabAddService.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddServiceTemplateActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Actualizar la lista cuando se vuelve del formulario
    @Override
    protected void onResume() {
        super.onResume();
        serviceRepo.getAllTemplates().observe(this, services -> {
            if (services != null) {
                serviceList = services;
                adapter.updateData(serviceList);
            }
        });
    }
}
