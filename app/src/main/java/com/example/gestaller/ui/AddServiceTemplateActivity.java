package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import com.example.gestaller.data.local.entity.ServiceTemplate;

public class AddServiceTemplateActivity extends AppCompatActivity {

    private EditText etName, etDescription, etPrice;
    private Button btnSave, btnCancel;
    private ServiceTemplateRepository repository;

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

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                etName.setError("Campo obligatorio");
                etPrice.setError("Campo obligatorio");
                return;
            }

            double price = Double.parseDouble(priceStr);

            ServiceTemplate service = new ServiceTemplate(name, description, price);
            repository.insert(service);
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
