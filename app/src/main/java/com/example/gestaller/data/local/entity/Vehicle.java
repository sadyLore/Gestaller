package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehicles") // Es una buena práctica nombrar la tabla explícitamente
public class Vehicle {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String brand;
    private String model;

    // CONSTRUCTOR PRINCIPAL
    // Este es el único constructor que Room usará para leer objetos de la base de datos.
    // Inicializa TODOS los campos de la clase.
    public Vehicle(int id, String brand, String model) {
        this.id = id;
        this.brand = brand;
        this.model = model;
    }

    // CONSTRUCTOR SECUNDARIO (para crear vehículos NUEVOS)
    // Se usa cuando quieres insertar un vehículo nuevo y no tienes el 'id' todavía (Room lo generará).
    // Usamos @Ignore para decirle a Room que no intente usar este constructor para leer datos.
    @Ignore
    public Vehicle(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }


    // --- GETTERS y SETTERS (Estos ya estaban bien) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
