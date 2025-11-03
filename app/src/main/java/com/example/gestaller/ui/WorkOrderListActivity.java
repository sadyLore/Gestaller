package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.example.gestaller.ui.adapter.WorkOrderAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WorkOrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private WorkOrderAdapter adapter;
    private WorkOrderRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_list);

        ImageButton menuButton = findViewById(R.id.btnMenu);
        if (menuButton != null) {
            menuButton.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        repository = new WorkOrderRepository(getApplication());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔹 Cargar lista de trabajos
        repository.getAll().observe(this, workOrders -> {
            adapter = new WorkOrderAdapter(workOrders, repository, true);
            recyclerView.setAdapter(adapter);
        });

        // 🔹 Botón flotante para agregar nuevo trabajo
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddWorkOrderActivity.class))
        );
    }

    public void showEditWorkOrderDialog(WorkOrder order) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_workorder, null);
        builder.setView(view);

        // Encontrar todas las vistas del diálogo
        EditText etClientName = view.findViewById(R.id.etClientName);
        EditText etClientPhone = view.findViewById(R.id.etClientPhone);
        Spinner spBrand = view.findViewById(R.id.spBrand);
        Spinner spModel = view.findViewById(R.id.spModel);
        EditText etPlate = view.findViewById(R.id.etPlate);
        EditText etNotes = view.findViewById(R.id.etNotes);
        Button btnSave = view.findViewById(R.id.btnSaveEdit);
        Button btnCancel = view.findViewById(R.id.btnCancelEdit);

        // Rellenar los campos con los datos existentes de la orden
        etClientName.setText(order.getClientName());
        etClientPhone.setText(order.getClientPhone());
        etPlate.setText(order.getVehiclePlate());
        etNotes.setText(order.getNotes());
        // Nota: La lógica para seleccionar items en los Spinners es más compleja y se omite aquí.

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String clientName = etClientName.getText().toString();
            String clientPhone = etClientPhone.getText().toString();
            String plate = etPlate.getText().toString();
            String notes = etNotes.getText().toString();

            if (clientName.isEmpty()) {
                Toast.makeText(this, "El nombre del cliente es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar el objeto WorkOrder y guardarlo en la base de datos
            order.setClientName(clientName);
            order.setClientPhone(clientPhone);
            order.setVehiclePlate(plate);
            order.setNotes(notes);
            // Nota: La lógica para obtener valores de los Spinners se omite aquí.

            repository.update(order);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
