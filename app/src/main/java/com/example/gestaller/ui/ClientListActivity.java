package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;
import com.example.gestaller.ui.adapter.ClientAdapter;
import java.util.ArrayList;
import java.util.List;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClientAdapter adapter;
    private ClientRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        recyclerView = findViewById(R.id.recyclerClients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ClientAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        repository = new ClientRepository(this.getApplication());

        // Cargar datos desde Room
        repository.getAll().observe(this, new Observer<List<Client>>() {
            @Override
            public void onChanged(List<Client> clients) {
                adapter.updateData(clients);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddClientActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        repository.getAll().observe(this, clients -> adapter.updateData(clients));
    }
}
