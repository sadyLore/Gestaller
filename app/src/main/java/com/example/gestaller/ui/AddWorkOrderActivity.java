package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

public class AddWorkOrderActivity extends AppCompatActivity {

    private EditText etVehicleId, etTotalPrice, etNotes;
    private Button btnSave, btnCancel;
    private WorkOrderRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workorder);

        repository = new WorkOrderRepository(getApplication());

        etVehicleId = findViewById(R.id.etVehicleId);
        etTotalPrice = findViewById(R.id.etTotalPrice);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String vehicleIdText = etVehicleId.getText().toString().trim();
            String totalText = etTotalPrice.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (vehicleIdText.isEmpty() || totalText.isEmpty()) return;

            int vehicleId = Integer.parseInt(vehicleIdText);
            double totalPrice = Double.parseDouble(totalText);
            long currentDate = System.currentTimeMillis();

            WorkOrder work = new WorkOrder(vehicleId, totalPrice, notes, currentDate);
            repository.insert(work);
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
