package com.example.gestaller.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaller.data.local.entity.ServiceTemplate;

import java.util.List;

@Dao
public interface ServiceTemplateDao {

    @Query("SELECT * FROM servicetemplate ORDER BY name ASC")
    LiveData<List<ServiceTemplate>> getAllTemplates();

    @Insert
    void insert(ServiceTemplate serviceTemplate);

    @Update
    void update(ServiceTemplate serviceTemplate);

    @Delete
    void delete(ServiceTemplate serviceTemplate);
}
