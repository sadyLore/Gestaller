package com.example.gestaller.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.dao.ClientDao;
import com.example.gestaller.data.local.entity.Client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRepository {
    private final ClientDao clientDao;
    private final ExecutorService executorService;

    public ClientRepository(Application application) {
        TallerDatabase db = TallerDatabase.getInstance(application);
        clientDao = db.clientDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Client>> getAllClients() {
        return clientDao.getAllClients();
    }

    public void insert(Client client) {
        executorService.execute(() -> clientDao.insert(client));
    }

    public void update(Client client) {
        executorService.execute(() -> clientDao.update(client));
    }

    public void delete(Client client) {
        executorService.execute(() -> clientDao.delete(client));
    }
}

