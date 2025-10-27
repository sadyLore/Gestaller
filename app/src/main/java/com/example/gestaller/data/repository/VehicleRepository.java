package com.example.gestaller.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.dao.VehicleDao;
import com.example.gestaller.data.local.entity.Vehicle;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VehicleRepository {
    private final VehicleDao vehicleDao;
    private final ExecutorService executorService;

    public VehicleRepository(Application application) {
        TallerDatabase db = TallerDatabase.getInstance(application);
        vehicleDao = db.vehicleDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // MÉTODO CORREGIDO
    public LiveData<List<Vehicle>> getAll() {
        return vehicleDao.getAll();
    }

    public void insert(Vehicle vehicle) {
        executorService.execute(() -> vehicleDao.insert(vehicle));
    }

    public void update(Vehicle vehicle) {
        executorService.execute(() -> vehicleDao.update(vehicle));
    }

    public void delete(Vehicle vehicle) {
        executorService.execute(() -> vehicleDao.delete(vehicle));
    }
}
