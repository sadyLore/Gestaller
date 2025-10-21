package com.tallermanager.data.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaller.data.local.entity.Vehicle;

import java.util.List;

@Dao
public interface VehicleDao {

    @Query("SELECT * FROM Vehicle ORDER BY brand ASC")
    LiveData<List<Vehicle>> getAllVehicles();

    @Query("SELECT * FROM Vehicle WHERE clientId = :clientId")
    LiveData<List<Vehicle>> getVehiclesByClient(int clientId);

    @Query("SELECT * FROM Vehicle WHERE plate LIKE '%' || :plate || '%' LIMIT 1")
    Vehicle getVehicleByPlate(String plate);

    @Insert
    void insert(Vehicle vehicle);

    @Update
    void update(Vehicle vehicle);

    @Delete
    void delete(Vehicle vehicle);
}
