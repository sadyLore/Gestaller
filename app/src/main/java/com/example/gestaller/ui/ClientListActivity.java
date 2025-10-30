package com.example.gestaller.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;
import com.example.gestaller.ui.adapter.ClientAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerClients;
    private ClientAdapter adapter;
    private ClientRepository clientRepo;
    private FloatingActionButton fabAddClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        recyclerClients = findViewById(R.id.recyclerClients);
        fabAddClient = findViewById(R.id.fabAddClient);
        recyclerClients.setLayoutManager(new LinearLayoutManager(this));

        clientRepo = new ClientRepository(getApplication());

        adapter = new ClientAdapter(new ArrayList<>(), clientRepo);
        recyclerClients.setAdapter(adapter);

        clientRepo.getAll().observe(this, clients -> {
            if (clients != null) {
                adapter.updateData(clients);
            }
        });

        fabAddClient.setOnClickListener(v -> showAddClientDialog());
    }

    private void showAddClientDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_client, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etAddress = view.findViewById(R.id.etAddress);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client(name, phone, address);
            clientRepo.insert(client);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
