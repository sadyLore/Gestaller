package com.example.gestaller.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;
import com.example.gestaller.ui.adapter.ClientAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerClients;
    private ClientAdapter adapter;
    private ClientRepository clientRepo;
    private FloatingActionButton fabAddClient;
    private List<Client> clientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        recyclerClients = findViewById(R.id.recyclerClients);
        fabAddClient = findViewById(R.id.fabAddClient);
        recyclerClients.setLayoutManager(new LinearLayoutManager(this));

        clientRepo = new ClientRepository(getApplication());

        // 🔹 Cargar datos desde Room
        clientRepo.getAll().observe(this, clients -> {
            if (clients != null) {
                clientList = clients;
                adapter.updateData(clientList);
            }
        });

        // 🔹 Configurar adaptador
        adapter = new ClientAdapter(clientList, clientRepo);
        recyclerClients.setAdapter(adapter);

        // 🔹 Botón flotante para agregar nuevo cliente
        fabAddClient.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddClientActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Actualizar la lista cuando volvés del formulario
    @Override
    protected void onResume() {
        super.onResume();
        clientRepo.getAll().observe(this, clients -> {
            if (clients != null) {
                clientList = clients;
                adapter.updateData(clientList);
            }
        });
    }
}
