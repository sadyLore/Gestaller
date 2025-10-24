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

        repository.getAllTemplates().observe(this, services -> {
            adapter = new ServiceTemplateAdapter(services, repository);
            recyclerView.setAdapter(adapter);
        });


        // 🔹 Botón flotante para agregar un nuevo servicio
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddServiceTemplateActivity.class)));
    }
}
