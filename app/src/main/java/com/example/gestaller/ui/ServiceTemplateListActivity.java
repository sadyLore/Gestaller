package com.example.gestaller.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import com.example.gestaller.ui.adapter.ServiceTemplateAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ServiceTemplateListActivity extends AppCompatActivity {

    private RecyclerView recyclerServices;
    private ServiceTemplateAdapter adapter;
    private ServiceTemplateRepository serviceRepo;
    private FloatingActionButton fabAddService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_template_list);

        recyclerServices = findViewById(R.id.recyclerServices);
        fabAddService = findViewById(R.id.fabAddService);
        recyclerServices.setLayoutManager(new LinearLayoutManager(this));

        serviceRepo = new ServiceTemplateRepository(getApplication());

        adapter = new ServiceTemplateAdapter(new ArrayList<>(), serviceRepo);
        recyclerServices.setAdapter(adapter);

        serviceRepo.getAllTemplates().observe(this, services -> {
            if (services != null) {
                adapter.updateData(services);
            }
        });

        fabAddService.setOnClickListener(v -> showAddServiceDialog());
    }

    private void showAddServiceDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_service, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etDescription = view.findViewById(R.id.etDescription);
        EditText etPrice = view.findViewById(R.id.etPrice);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String description = etDescription.getText().toString();
            String priceStr = etPrice.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "El nombre y el precio son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            ServiceTemplate service = new ServiceTemplate(name, description, price);
            serviceRepo.insert(service);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
