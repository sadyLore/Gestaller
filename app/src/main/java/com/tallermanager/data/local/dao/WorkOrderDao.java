package com.tallermanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaller.data.local.entity.WorkOrder;

import java.util.List;

@Dao
public interface WorkOrderDao {

    @Query("SELECT * FROM WorkOrder ORDER BY date DESC")
    LiveData<List<WorkOrder>> getAllWorkOrders();

    @Query("SELECT * FROM WorkOrder WHERE vehicleId = :vehicleId")
    LiveData<List<WorkOrder>> getWorkOrdersByVehicle(int vehicleId);

    @Insert
    void insert(WorkOrder workOrder);

    @Update
    void update(WorkOrder workOrder);

    @Delete
    void delete(WorkOrder workOrder);
}

