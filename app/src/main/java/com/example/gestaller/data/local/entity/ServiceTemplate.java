package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class ServiceTemplate {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private double defaultPrice;

    public ServiceTemplate(String name, double defaultPrice) {
        this.name = name;
        this.defaultPrice = defaultPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(double defaultPrice) { this.defaultPrice = defaultPrice; }
}

