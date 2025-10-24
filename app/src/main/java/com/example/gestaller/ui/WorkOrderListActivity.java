package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.example.gestaller.ui.adapter.WorkOrderAdapter;
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
}
