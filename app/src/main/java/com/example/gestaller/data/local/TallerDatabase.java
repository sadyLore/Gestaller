package com.example.gestaller.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gestaller.data.local.dao.ClientDao;
import com.example.gestaller.data.local.dao.VehicleDao;
import com.example.gestaller.data.local.dao.WorkOrderDao;
import com.example.gestaller.data.local.dao.ServiceTemplateDao;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.local.entity.User;
import com.example.gestaller.data.local.entity.WorkOrderPhoto;
import com.example.gestaller.data.local.entity.WorkOrderService;

@Database(
        entities = {
                User.class,
                Client.class,
                Vehicle.class,
                WorkOrder.class,
                WorkOrderService.class,
                WorkOrderPhoto.class,
                ServiceTemplate.class
        },
        version = 1,
        exportSchema = false
)
public abstract class TallerDatabase extends RoomDatabase {

    // --- DAOs ---
    public abstract ClientDao clientDao();
    public abstract VehicleDao vehicleDao();
    public abstract WorkOrderDao workOrderDao();
    public abstract ServiceTemplateDao serviceTemplateDao();

    // --- Singleton ---
    private static volatile TallerDatabase INSTANCE;

    public static TallerDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TallerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TallerDatabase.class,
                                    "taller_manager.db"
                            )
                            .fallbackToDestructiveMigration() // si cambias versión, borra y recrea
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
