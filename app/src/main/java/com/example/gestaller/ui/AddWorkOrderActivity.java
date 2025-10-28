package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

import java.util.Date;

public class AddWorkOrderActivity extends AppCompatActivity {

    private Spinner spBrand, spModel;
    private LinearLayout servicesContainer;
    private EditText etNotes, etTotalPrice;
    private Button btnSave, btnCancel;
    private WorkOrderRepository repository;

    private int workOrderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workorder);

        repository = new WorkOrderRepository(getApplication());

        spBrand = findViewById(R.id.spBrand);
        spModel = findViewById(R.id.spModel);
        etNotes = findViewById(R.id.etNotes);
        servicesContainer = findViewById(R.id.servicesContainer);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Si viene de editar
        if (getIntent() != null && getIntent().hasExtra("workOrderId")) {
            workOrderId = getIntent().getIntExtra("workOrderId", -1);
            etNotes.setText(getIntent().getStringExtra("notes"));
        }

        btnSave.setOnClickListener(v -> {
            String notes = etNotes.getText().toString();
            double totalPrice = 0.0; // Podés agregar un campo de precio si lo necesitás

            WorkOrder order = new WorkOrder();
            order.setNotes(notes);
            order.setTotalPrice(totalPrice);
            order.setDate(new Date().getTime());

            if (workOrderId != -1) {
                order.setId(workOrderId);
                repository.update(order);
            } else {
                repository.insert(order);
            }
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
