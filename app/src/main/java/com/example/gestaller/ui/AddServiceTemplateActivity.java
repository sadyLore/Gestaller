package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.repository.ServiceTemplateRepository;

public class AddServiceTemplateActivity extends AppCompatActivity {

    private EditText etName, etDescription, etPrice;
    private Button btnSave, btnCancel;
    private ServiceTemplateRepository repository;
    private int serviceId = -1; // ← Para saber si estamos editando

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_template);

        repository = new ServiceTemplateRepository(getApplication());

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // 🔹 Cancelar → volver atrás
        btnCancel.setOnClickListener(v -> finish());

        // 🔹 Ver si venimos desde "Editar"
        if (getIntent() != null && getIntent().hasExtra("serviceId")) {
            serviceId = getIntent().getIntExtra("serviceId", -1);
            String name = getIntent().getStringExtra("name");
            String description = getIntent().getStringExtra("description");
            double price = getIntent().getDoubleExtra("price", 0);

            etName.setText(name);
            etDescription.setText(description);
            etPrice.setText(String.valueOf(price));
        }

        // 🔹 Guardar / actualizar
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("El nombre es obligatorio");
                return;
            }

            double price = priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr);

            ServiceTemplate service = new ServiceTemplate(name, description, price);

            if (serviceId != -1) {
                // 🔹 Editar servicio existente
                service.setId(serviceId);
                repository.update(service);
            } else {
                // 🔹 Nuevo servicio
                repository.insert(service);
            }

            finish();
        });
    }
}
