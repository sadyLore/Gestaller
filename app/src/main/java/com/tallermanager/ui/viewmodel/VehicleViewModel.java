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
        allVehicles = repository.getAllVehicles();
    }

    public LiveData<List<Vehicle>> getAllVehicles() {
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

