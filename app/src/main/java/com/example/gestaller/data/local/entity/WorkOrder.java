package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index; // Importación necesaria para el índice
import androidx.room.PrimaryKey;

//  SE HA AÑADIDO LA PROPIEDAD 'indices' PARA LA MEJORA DE RENDIMIENTO
@Entity(
        tableName = "work_orders", // Buena práctica: nombrar la tabla explícitamente
        foreignKeys = {
                @ForeignKey(
                        entity = Vehicle.class,
                        parentColumns = "id",
                        childColumns = "vehicleId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index(value = "vehicleId")} // <-- ESTA ES LA LÍNEA QUE SOLUCIONA LA ADVERTENCIA
)
public class WorkOrder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int vehicleId;
    private String services;
    private double totalPrice;
    private String notes;
    private long date;

    // Tu constructor está bien para crear nuevas órdenes de trabajo
    public WorkOrder(int vehicleId,  String services, double totalPrice, String notes, long date) {
        this.vehicleId = vehicleId;
        this.services = services;
        this.totalPrice = totalPrice;
        this.notes = notes;
        this.date = date;
    }

    // --- GETTERS Y SETTERS (Están correctos) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public String getServices() { return services; }
    // Este setter parece que no lo necesitas si ya tienes el constructor, pero no hace daño
    // public void setServices(String services) { this.services = services; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}
