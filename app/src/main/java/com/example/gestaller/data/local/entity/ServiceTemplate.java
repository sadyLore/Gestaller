package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ServiceTemplate {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private double defaultPrice;

    // ✅ Constructor vacío requerido por Room
    public ServiceTemplate() {}

    // ✅ Constructor principal con parámetros correctos
    public ServiceTemplate(String name, String description, double defaultPrice) {
        this.name = name;
        this.description = description;
        this.defaultPrice = defaultPrice;
    }

    // ✅ Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(double defaultPrice) { this.defaultPrice = defaultPrice; }
}
