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

    // CONSULTA CORREGIDA
    // Se usa 'work_orders' que es el nombre de la tabla definido en la entidad.
    @Query("SELECT * FROM work_orders ORDER BY date DESC")
    LiveData<List<WorkOrder>> getAllWorkOrders();

    // CONSULTA CORREGIDA
    // También se usa 'work_orders'.
    @Query("SELECT * FROM work_orders ORDER BY id DESC")
    LiveData<List<WorkOrder>> getAll();

    @Insert
    void insert(WorkOrder workOrder);

    @Update
    void update(WorkOrder workOrder);

    @Delete
    void delete(WorkOrder workOrder);
}
