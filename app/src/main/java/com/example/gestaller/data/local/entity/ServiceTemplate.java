package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class ServiceTemplate {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public String name;
    private String Description;
    private double defaultPrice;

    public ServiceTemplate(String name, String description, double defaultPrice) {
        this.name = name;
        Description = description;
        this.defaultPrice = defaultPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(double defaultPrice) { this.defaultPrice = defaultPrice; }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}

