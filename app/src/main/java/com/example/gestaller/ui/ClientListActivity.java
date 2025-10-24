package com.example.gestaller.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.ui.adapter.ClientAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerClients;
    private ClientAdapter adapter;
    private List<Client> clientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        recyclerClients = findViewById(R.id.recyclerClients);
        recyclerClients.setLayoutManager(new LinearLayoutManager(this));

        // 🔹 Datos de ejemplo (hasta conectar Room)
        clientList = new ArrayList<>();
        clientList.add(new Client("Carlos Ojeda", "098155690", "Villa Aurelia"));
        clientList.add(new Client("Fiorella Miranda", "0972726832", "San Lorenzo, zona Sur"));
        clientList.add(new Client("Nelly Bogado", "0992800200", "Fernando de la Mora"));

        adapter = new ClientAdapter(clientList);
        recyclerClients.setAdapter(adapter);
    }
}
