package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkOrderDetailActivity extends AppCompatActivity {

    // Declaraciones de vistas
    private ImageView imgWorkOrder;
    private TextView tvClientName, tvVehicleInfo, tvPhone, tvServices, tvNotes, tvPrice, tvDate; // Eliminé tvTotal para evitar duplicados

    private WorkOrderRepository repository;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_detail);

        // --- Inicializar vistas ---
        imgWorkOrder = findViewById(R.id.imgWorkOrder);
        tvClientName = findViewById(R.id.tvClientName);
        tvVehicleInfo = findViewById(R.id.tvVehicleInfo);
        tvPhone = findViewById(R.id.tvPhone);
        tvServices = findViewById(R.id.tvServices);
        tvNotes = findViewById(R.id.tvNotes);
        tvDate = findViewById(R.id.tvDate);

        // ▼▼▼ ¡AQUÍ ESTÁ LA LÍNEA AÑADIDA! ▼▼▼
        tvPrice = findViewById(R.id.tvPrice); // Ahora tvPrice está inicializado correctamente

        // Inicializar repositorio y obtener el ID de la orden
        repository = new WorkOrderRepository(getApplication());
        orderId = getIntent().getIntExtra("orderId", -1);

        // Observar los datos si el ID es válido
        if (orderId != -1) {
            // Es mucho más eficiente crear un método en el DAO para buscar por ID
            // Pero por ahora, mantenemos la lógica actual para no introducir más cambios.
            repository.getAll().observe(this, orders -> {
                for (WorkOrder order : orders) {
                    if (order.getId() == orderId) {
                        cargarDatos(order);
                        break; // Salimos del bucle una vez que encontramos la orden
                    }
                }
            });
        }
    }

    private void cargarDatos(WorkOrder order) {
        // Imagen (temporal: ícono)
        imgWorkOrder.setImageResource(R.drawable.ic_car_placeholder);

        // Datos del cliente y vehículo
        // Asumiendo que has modificado WorkOrder para tener estos getters
        // Si no los tienes, este código fallará.
        // tvClientName.setText(order.getClientName());
        // tvVehicleInfo.setText(order.getVehicleBrand() + " " + order.getVehicleModel() + " - " + order.getVehiclePlate());
        // tvPhone.setText(order.getClientPhone());

        // Datos de la orden
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

        // --- Asignar el precio y la fecha ---
        // Usamos String.format para dar formato al número con separadores de miles
        tvPrice.setText("Total: Gs. " + String.format(Locale.GERMAN, "%,.0f", order.getTotalPrice()));

        // Formatear y mostrar la fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(order.getDate())));
    }
}
