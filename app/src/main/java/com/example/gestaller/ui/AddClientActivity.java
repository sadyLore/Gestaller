package com.example.gestaller.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;
;

public class AddClientActivity extends AppCompatActivity {
    private EditText etName, etPhone, etAddress;
    private Button btnSave, btnCancel;
    private ClientRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        repository = new ClientRepository(this.getApplication());

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Botón Cancelar: vuelve a la pantalla anterior
        btnCancel.setOnClickListener(v -> finish());

        // Botón Guardar: valida e inserta el cliente
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (!name.isEmpty()) {
                repository.insert(new Client(name, phone, address));
                finish(); // vuelve a la lista después de guardar
            } else {
                etName.setError("El nombre es obligatorio");
            }
        });
    }
}
