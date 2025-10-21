package com.example.gestaller.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaller.data.local.entity.Client;

import java.util.List;

@Dao
public interface ClientDao {

    @Query("SELECT * FROM client ORDER BY name ASC")
    LiveData<List<Client>> getAllClients();

    @Insert
    void insert(Client client);

    @Update
    void update(Client client);

    @Delete
    void delete(Client client);
}
