package com.example.gestaller.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.repository.ClientRepository;
import com.example.gestaller.ui.adapter.ClientAdapter;
import com.example.gestaller.data.local.entity.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerClients;
    private ClientAdapter adapter;
    private ClientRepository clientRepo;
    private List<Client> clientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        recyclerClients = findViewById(R.id.recyclerClients);
        recyclerClients.setLayoutManager(new LinearLayoutManager(this));

        clientRepo = new ClientRepository(getApplication());

        // 🔹 Observar los clientes guardados en Room
        clientRepo.getAll().observe(this, clients -> {
            if (clients != null) {
                clientList = clients;
                adapter.updateData(clientList);
            }
        });

        // 🔹 Inicializar el adaptador
        adapter = new ClientAdapter(clientList, clientRepo);
        recyclerClients.setAdapter(adapter);
    }
}
