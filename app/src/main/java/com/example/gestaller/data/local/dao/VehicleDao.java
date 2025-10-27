package com.example.gestaller.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaller.data.local.entity.Vehicle;

import java.util.List;

@Dao
public interface VehicleDao {

    /**
     * Obtiene todos los vehículos de la base de datos, ordenados por marca.
     * @return Un LiveData con la lista de todos los vehículos.
     */
    @Query("SELECT * FROM vehicles ORDER BY brand ASC") // Cambiado a 'vehicles' y ordenado por 'brand'
    LiveData<List<Vehicle>> getAll(); // Nombre más corto y estándar

    /**
     * Inserta un nuevo vehículo en la base de datos.
     * Si el vehículo ya existe, será reemplazado.
     * @param vehicle El vehículo a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Buena práctica para evitar errores de conflictos
    void insert(Vehicle vehicle);

    /**
     * Actualiza un vehículo existente en la base de datos.
     * @param vehicle El vehículo con los datos actualizados.
     */
    @Update
    void update(Vehicle vehicle);

    /**
     * Elimina un vehículo de la base de datos.
     * @param vehicle El vehículo a eliminar.
     */
    @Delete
    void delete(Vehicle vehicle);
}
