package com.tallermanager.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;

import java.util.List;

public class ClientViewModel extends AndroidViewModel {
    private final ClientRepository repository;
    private final LiveData<List<Client>> allClients;

    public ClientViewModel(@NonNull Application application) {
        super(application);
        repository = new ClientRepository(application);
        allClients = repository.getAllClients();
    }

    public LiveData<List<Client>> getAllClients() {
        return allClients;
    }

    public void insert(Client client) {
        repository.insert(client);
    }

    public void update(Client client) {
        repository.update(client);
    }

    public void delete(Client client) {
        repository.delete(client);
    }
}
