package com.example.gestaller.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.dao.WorkOrderDao;
import com.example.gestaller.data.local.entity.WorkOrder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkOrderRepository {

    private final WorkOrderDao workOrderDao;
    private final ExecutorService executorService;

    public WorkOrderRepository(Application application) {
        TallerDatabase db = TallerDatabase.getInstance(application);
        workOrderDao = db.workOrderDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // 🔹 Obtener todas las órdenes (antiguo)
    public LiveData<List<WorkOrder>> getAllWorkOrders() {
        return workOrderDao.getAllWorkOrders();
    }

    // 🔹 Insertar una nueva orden
    public void insert(WorkOrder workOrder) {
        executorService.execute(() -> workOrderDao.insert(workOrder));
    }

    // 🔹 Actualizar una orden existente
    public void update(WorkOrder workOrder) {
        executorService.execute(() -> workOrderDao.update(workOrder));
    }

    // 🔹 Eliminar una orden
    public void delete(WorkOrder workOrder) {
        executorService.execute(() -> workOrderDao.delete(workOrder));
    }

    // 🔹 Obtener todas (nueva forma)
    public LiveData<List<WorkOrder>> getAll() {
        return workOrderDao.getAll();
    }

    // 🔹 Obtener una orden específica por ID (para detalle)
    public LiveData<WorkOrder> getWorkOrderById(int id) {
        return workOrderDao.getWorkOrderById(id);
    }
}
