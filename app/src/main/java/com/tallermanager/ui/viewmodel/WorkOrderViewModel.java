package com.tallermanager.ui.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

import java.util.List;

public class WorkOrderViewModel extends AndroidViewModel {
    private final WorkOrderRepository repository;
    private final LiveData<List<WorkOrder>> allWorkOrders;

    public WorkOrderViewModel(@NonNull Application application) {
        super(application);
        repository = new WorkOrderRepository(application);
        allWorkOrders = repository.getAllWorkOrders();
    }

    public LiveData<List<WorkOrder>> getAllWorkOrders() {
        return allWorkOrders;
    }

    public void insert(WorkOrder workOrder) {
        repository.insert(workOrder);
    }

    public void update(WorkOrder workOrder) {
        repository.update(workOrder);
    }

    public void delete(WorkOrder workOrder) {
        repository.delete(workOrder);
    }
}
