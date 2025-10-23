package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Vehicle {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int clientId;
    private String brand;
    private String model;
    private int year;
    private String color;
    private String plate;

    public Vehicle(int clientId, String brand, String model, int year, String color, String plate) {
        this.clientId = clientId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.plate = plate;
    }

    public Vehicle(String brand, String model, String year, String color, String plate) {

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
}

