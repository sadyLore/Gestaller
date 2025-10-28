package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkOrderDetailActivity extends AppCompatActivity {

    private ImageView imgWorkOrder;
    private TextView tvClientName, tvVehicleInfo, tvPhone, tvServices, tvNotes, tvDate, tvTotal;
    private WorkOrderRepository repository;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_detail);

        // Inicializar vistas
        imgWorkOrder = findViewById(R.id.imgWorkOrder);
        tvClientName = findViewById(R.id.tvClientName);
        tvVehicleInfo = findViewById(R.id.tvVehicleInfo);
        tvPhone = findViewById(R.id.tvPhone);
        tvServices = findViewById(R.id.tvServices);
        tvNotes = findViewById(R.id.tvNotes);
        tvDate = findViewById(R.id.tvDate);

        repository = new WorkOrderRepository(getApplication());

        orderId = getIntent().getIntExtra("orderId", -1);

        if (orderId != -1) {
            repository.getAll().observe(this, orders -> {
                for (WorkOrder order : orders) {
                    if (order.getId() == orderId) {
                        cargarDatos(order);
                        break;
                    }
                }
            });
        }
    }

    private void cargarDatos(WorkOrder order) {
        // Imagen (temporal: ícono)
        imgWorkOrder.setImageResource(R.drawable.ic_car_placeholder);

        // Datos
        tvClientName.setText(order.getClientName());
        tvVehicleInfo.setText(order.getVehicleBrand() + " " + order.getVehicleModel() + " - " + order.getVehiclePlate());
        tvPhone.setText(order.getClientPhone());

        if (order.getServices() != null && !order.getServices().isEmpty()) {
            tvServices.setText(order.getServices());
        } else {
            tvServices.setText("Sin servicios registrados");
        }

        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            tvNotes.setText(order.getNotes());
        } else {
            tvNotes.setText("Sin notas");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(order.getDate())));

        tvTotal.setText("Total: Gs. " + String.format("%,.0f", order.getTotalPrice()));
    }
}
