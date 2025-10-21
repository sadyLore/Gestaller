package com.example.gestaller.data.local.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WorkOrderService {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int workOrderId;
    private String serviceName;
    private double price;

    public WorkOrderService(int workOrderId, String serviceName, double price) {
        this.workOrderId = workOrderId;
        this.serviceName = serviceName;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(int workOrderId) { this.workOrderId = workOrderId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
