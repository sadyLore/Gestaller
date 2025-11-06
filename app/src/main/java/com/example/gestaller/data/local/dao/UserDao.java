package com.example.gestaller.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gestaller.data.local.entity.User;

import java.util.List; // 👈 necesario para devolver una lista

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Query("SELECT * FROM User WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    // 🔹 Nuevo método para verificar si ya existen usuarios
    @Query("SELECT * FROM User")
    List<User> getAllUsers();
}
