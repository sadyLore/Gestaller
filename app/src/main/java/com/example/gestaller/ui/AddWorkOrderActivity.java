package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddWorkOrderActivity extends AppCompatActivity {

    private Spinner spBrand, spModel;
    private LinearLayout servicesContainer;
    private EditText etClientName, etPlate, etNotes;
    private Button btnSave, btnCancel;
    private WorkOrderRepository repository;

    private int workOrderId = -1; // si viene para editar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workorder);

        // Inicializar repositorio
        repository = new WorkOrderRepository(getApplication());

        // Vistas
        spBrand = findViewById(R.id.spBrand);
        spModel = findViewById(R.id.spModel);
        etClientName = findViewById(R.id.etClientName);
        etPlate = findViewById(R.id.etPlate);
        etNotes = findViewById(R.id.etNotes);
        servicesContainer = findViewById(R.id.servicesContainer);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // 🔹 Cargar servicios simulados (luego vendrán de BD)
        List<String> exampleServices = Arrays.asList("Cambio de aceite", "Frenos", "Alineación", "Revisión general");
        for (String s : exampleServices) {
            CheckBox cb = new CheckBox(this);
            cb.setText(s);
            cb.setTextColor(getResources().getColor(R.color.black));
            servicesContainer.addView(cb);
        }

        // 🔹 Verificar si venimos para editar
        if (getIntent() != null && getIntent().hasExtra("workOrderId")) {
            workOrderId = getIntent().getIntExtra("workOrderId", -1);
            cargarDatosExistentes();
        }

        // 🔹 Botón Guardar
        btnSave.setOnClickListener(v -> guardarTrabajo());

        // 🔹 Botón Cancelar
        btnCancel.setOnClickListener(v -> finish());
    }

    private void cargarDatosExistentes() {
        repository.getAllWorkOrders().observe(this, workOrders -> {
            for (WorkOrder order : workOrders) {
                if (order.getId() == workOrderId) {
                    etClientName.setText(order.getClientName());
                    etPlate.setText(order.getVehiclePlate());
                    etNotes.setText(order.getNotes());

                    // Seleccionar servicios marcados
                    String[] selected = order.getServices() != null ? order.getServices().split(", ") : new String[]{};
                    for (int i = 0; i < servicesContainer.getChildCount(); i++) {
                        CheckBox cb = (CheckBox) servicesContainer.getChildAt(i);
                        for (String s : selected) {
                            if (cb.getText().toString().equals(s)) {
                                cb.setChecked(true);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        });
    }

    private void guardarTrabajo() {
        String clientName = etClientName.getText().toString().trim();
        String plate = etPlate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Obtener servicios seleccionados
        StringBuilder servicesBuilder = new StringBuilder();
        for (int i = 0; i < servicesContainer.getChildCount(); i++) {
            CheckBox cb = (CheckBox) servicesContainer.getChildAt(i);
            if (cb.isChecked()) {
                if (servicesBuilder.length() > 0) servicesBuilder.append(", ");
                servicesBuilder.append(cb.getText().toString());
            }
        }

        WorkOrder order = new WorkOrder();
        order.setClientName(clientName);
        order.setVehiclePlate(plate);
        order.setNotes(notes);
        order.setServices(servicesBuilder.toString());
        order.setTotalPrice(0.0);
        order.setDate(new Date().getTime());

        if (workOrderId != -1) {
            order.setId(workOrderId);
            repository.update(order);
            Toast.makeText(this, "Trabajo actualizado correctamente", Toast.LENGTH_SHORT).show();
        } else {
            repository.insert(order);
            Toast.makeText(this, "Trabajo agregado correctamente", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
