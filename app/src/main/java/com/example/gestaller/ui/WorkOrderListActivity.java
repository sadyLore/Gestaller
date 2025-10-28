package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.example.gestaller.ui.adapter.WorkOrderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkOrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private WorkOrderAdapter adapter;
    private WorkOrderRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_list);

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        repository = new WorkOrderRepository(getApplication());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // observar los datos
        repository.getAll().observe(this, workOrders -> {
            adapter = new WorkOrderAdapter(workOrders, repository);
            recyclerView.setAdapter(adapter);
        });

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddWorkOrderActivity.class)));
    }

    public static class WorkOrderDetailActivity extends AppCompatActivity {

        private ImageView imgWorkOrder;
        private TextView tvTitle, tvPrice, tvDate;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_workorder_detail);


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


            // Datos básicos
            tvTitle.setText("Trabajo #" + order.getId());
            tvPrice.setText("Total: Gs. " + String.format("%,.0f", order.getTotalPrice()));

            Date date = new Date(order.getDate());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvDate.setText("Fecha: " + sdf.format(date));
        }
    }
}
