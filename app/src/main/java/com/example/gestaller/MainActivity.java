package com.example.gestaller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.dao.ClientDao;
import com.example.gestaller.data.local.entity.Client;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // o activity_home, según tu flujo real

        // 🔹 Inicializamos Room en un hilo aparte para no bloquear la UI
        new Thread(() -> {
            try {
                // Inicializa la base de datos
                TallerDatabase db = TallerDatabase.getInstance(this);

                // Obtiene el DAO
                ClientDao clientDao = db.clientDao();

                // Inserta un cliente de prueba (esto fuerza la creación de las tablas)
                Client client = new Client(
                        "Cliente de prueba",
                        "0999123456",
                        "Asunción"
                );

                clientDao.insert(client);

                System.out.println("✅ Cliente insertado y base de datos creada correctamente.");

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("⚠️ Error al inicializar la base de datos: " + e.getMessage());
            }
        }).start();
    }
}
