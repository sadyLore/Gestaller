package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WorkOrder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int vehicleId;
    private double totalPrice;
    private String notes;
    private long date;

    public WorkOrder(int vehicleId, double totalPrice, String notes, long date) {
        this.vehicleId = vehicleId;
        this.totalPrice = totalPrice;
        this.notes = notes;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}
