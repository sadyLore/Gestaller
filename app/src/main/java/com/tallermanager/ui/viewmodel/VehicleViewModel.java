package com.tallermanager.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.VehicleRepository;

import java.util.List;

public class VehicleViewModel extends AndroidViewModel {
    private final VehicleRepository repository;
    private final LiveData<List<Vehicle>> allVehicles;

    public VehicleViewModel(@NonNull Application application) {
        super(application);
        repository = new VehicleRepository(application);
        // 1. CORRECCIÓN EN EL CONSTRUCTOR
        allVehicles = repository.getAll(); // Usamos el nuevo nombre 'getAll()'
    }

    // 2. CORRECCIÓN DEL MÉTODO PÚBLICO (por consistencia)
    public LiveData<List<Vehicle>> getAll() { // Renombramos el método para que coincida
        return allVehicles;
    }

    public void insert(Vehicle vehicle) {
        repository.insert(vehicle);
    }

    public void update(Vehicle vehicle) {
        repository.update(vehicle);
    }

    public void delete(Vehicle vehicle) {
        repository.delete(vehicle);
    }
}
