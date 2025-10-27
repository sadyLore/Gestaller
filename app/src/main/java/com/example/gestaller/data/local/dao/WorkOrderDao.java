package com.example.gestaller.data.local.dao;

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

    // 🔹 Obtener todas las órdenes (por fecha descendente)
    @Query("SELECT * FROM work_orders ORDER BY date DESC")
    LiveData<List<WorkOrder>> getAllWorkOrders();

    // 🔹 Obtener todas las órdenes (por ID descendente)
    @Query("SELECT * FROM work_orders ORDER BY id DESC")
    LiveData<List<WorkOrder>> getAll();

    // 🔹 Obtener una orden específica por su ID
    @Query("SELECT * FROM work_orders WHERE id = :id LIMIT 1")
    LiveData<WorkOrder> getWorkOrderById(int id);

    // 🔹 Insertar nueva orden
    @Insert
    void insert(WorkOrder workOrder);

    // 🔹 Actualizar una orden existente
    @Update
    void update(WorkOrder workOrder);

    // 🔹 Eliminar una orden
    @Delete
    void delete(WorkOrder workOrder);
}
