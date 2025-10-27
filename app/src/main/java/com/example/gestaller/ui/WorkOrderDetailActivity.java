package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkOrderDetailActivity extends AppCompatActivity {

    private ImageView imgWorkOrder;
    private TextView tvTitle, tvPrice, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_detail);

        imgWorkOrder = findViewById(R.id.imgWorkOrder);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvDate = findViewById(R.id.tvDate);

        // 🔹 Simulación de datos (en la versión final, vendrán por intent o Room)
        WorkOrder sample = new WorkOrder();
        sample.setId(1);
        sample.setTotalPrice(250000);
        sample.setDate(System.currentTimeMillis());

        cargarDatos(sample);
    }

    private void cargarDatos(@NonNull WorkOrder order) {
        // Imagen temporal (Firebase más adelante)
        imgWorkOrder.setImageResource(R.drawable.ic_car_placeholder);

        // Datos básicos
        tvTitle.setText("Trabajo #" + order.getId());
        tvPrice.setText("Total: Gs. " + String.format("%,.0f", order.getTotalPrice()));

        Date date = new Date(order.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText("Fecha: " + sdf.format(date));
    }
}
