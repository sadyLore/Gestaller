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

public class ServiceTemplateListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private ServiceTemplateAdapter adapter;
    private ServiceTemplateRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_template_list);

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        repository = new ServiceTemplateRepository(getApplication());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔹 Observamos los servicios y si está vacío, insertamos los predeterminados una sola vez
        repository.getAllTemplates().observe(this, services -> {
            if (services.isEmpty()) {
                // Inserta los predeterminados solo una vez
                repository.insert(new ServiceTemplate("Cambio de aceite y filtro", "Reemplazo de aceite y filtro", 120000));
                repository.insert(new ServiceTemplate("Alineación y balanceo", "Ajuste de ángulos y balanceo de ruedas", 100000));
                repository.insert(new ServiceTemplate("Revisión y cambio de frenos", "Inspección y sustitución de pastillas o discos", 150000));
                repository.insert(new ServiceTemplate("Cambio de bujías y mantenimiento del sistema de encendido", "Cambio de bujías y revisión del encendido", 130000));
                repository.insert(new ServiceTemplate("Revisión del sistema eléctrico", "Chequeo de batería, alternador y fusibles", 110000));
                repository.insert(new ServiceTemplate("Mantenimiento del sistema de refrigeración", "Revisión de radiador, líquido y mangueras", 140000));
            }

            // 🔹 Actualiza el adaptador
            adapter = new ServiceTemplateAdapter(services, repository);
            recyclerView.setAdapter(adapter);
        });

        // 🔹 Botón flotante para agregar un nuevo servicio
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddServiceTemplateActivity.class)));
    }
}
