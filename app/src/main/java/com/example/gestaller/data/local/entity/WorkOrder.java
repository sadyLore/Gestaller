package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "work_orders")
public class WorkOrder {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String clientName;
    private String clientPhone;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;
    private String services;
    private String notes;
    private long date;
    private String photoUrl; // ✅ NUEVO CAMPO para guardar la imagen

    public WorkOrder() {
        this.date = System.currentTimeMillis();
    }

    public WorkOrder(String clientName, String clientPhone, String vehicleBrand,
                     String vehicleModel, String vehiclePlate, String services,
                     String notes, long date, String photoUrl) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.vehiclePlate = vehiclePlate;
        this.services = services;
        this.notes = notes;
        this.date = date;
        this.photoUrl = photoUrl;
    }

    // 🔹 Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getVehicleBrand() { return vehicleBrand; }
    public void setVehicleBrand(String vehicleBrand) { this.vehicleBrand = vehicleBrand; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }

    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
