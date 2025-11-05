package com.example.gestaller.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gestaller.data.local.dao.ClientDao;
import com.example.gestaller.data.local.dao.ServiceTemplateDao;
import com.example.gestaller.data.local.dao.VehicleDao;
import com.example.gestaller.data.local.dao.WorkOrderDao;
import com.example.gestaller.data.local.dao.UserDao; // ✅ Agregá esto
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.local.entity.User; // ✅ Agregá esto

@Database(
        entities = {
                Client.class,
                Vehicle.class,
                WorkOrder.class,
                ServiceTemplate.class,
                User.class // ✅ Agregá esto
        },
        version = 1,
        exportSchema = false
)
public abstract class TallerDatabase extends RoomDatabase {

    public abstract ClientDao clientDao();
    public abstract VehicleDao vehicleDao();
    public abstract WorkOrderDao workOrderDao();
    public abstract ServiceTemplateDao serviceTemplateDao();
    public abstract UserDao userDao(); // ✅ Agregá este método

    private static volatile TallerDatabase INSTANCE;

    public static TallerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TallerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TallerDatabase.class,
                                    "taller_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

