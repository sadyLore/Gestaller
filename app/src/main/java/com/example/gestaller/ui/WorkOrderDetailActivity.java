package com.example.gestaller.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkOrderDetailActivity extends AppCompatActivity {

    private ImageView imgWorkOrder;
    private TextView tvClientName, tvVehicleInfo, tvPhone, tvServices, tvNotes, tvDate, tvPriceLabel;
    private Button btnClose;
    private WorkOrderRepository repository;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_detail);

        // 🔹 Inicializar vistas
        imgWorkOrder = findViewById(R.id.imgWorkOrder);
        tvClientName = findViewById(R.id.tvClientName);
        tvVehicleInfo = findViewById(R.id.tvVehicleInfo);
        tvPhone = findViewById(R.id.tvPhone);
        tvServices = findViewById(R.id.tvServices);
        tvNotes = findViewById(R.id.tvNotes);
        tvDate = findViewById(R.id.tvDate);
        btnClose = findViewById(R.id.btnClose);

        // 🔹 Configurar botón para cerrar
        btnClose.setOnClickListener(v -> finish());

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
        // 📸 Imagen
        if (order.getPhotoUrl() != null && !order.getPhotoUrl().isEmpty()) {
            imgWorkOrder.setVisibility(View.VISIBLE);
            Glide.with(this).load(order.getPhotoUrl()).into(imgWorkOrder);
        } else {
            imgWorkOrder.setVisibility(View.GONE);
        }

        // 🧾 Cliente
        tvClientName.setText(order.getClientName() != null ? order.getClientName() : "Cliente desconocido");

        // 🚗 Vehículo
        String vehicleText = "";
        if (order.getVehicleBrand() != null) vehicleText += order.getVehicleBrand() + " ";
        if (order.getVehicleModel() != null) vehicleText += order.getVehicleModel();
        if (order.getVehiclePlate() != null && !order.getVehiclePlate().isEmpty())
            vehicleText += " - " + order.getVehiclePlate();
        tvVehicleInfo.setText(vehicleText.trim().isEmpty() ? "Vehículo no registrado" : vehicleText);

        // ☎️ Teléfono
        tvPhone.setText(order.getClientPhone() != null && !order.getClientPhone().isEmpty()
                ? order.getClientPhone()
                : "Sin teléfono");

        // 🔧 Servicios
        tvServices.setText(order.getServices() != null && !order.getServices().isEmpty()
                ? order.getServices()
                : "Sin servicios registrados");

        // 📝 Notas
        tvNotes.setText(order.getNotes() != null && !order.getNotes().isEmpty()
                ? order.getNotes()
                : "Sin notas agregadas");

        // 📅 Fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        tvDate.setText("Fecha: " + sdf.format(new Date(order.getDate())));
    }
}
