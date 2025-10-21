package com.example.gestaller.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.dao.ServiceTemplateDao;
import com.example.gestaller.data.local.entity.ServiceTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceTemplateRepository {
    private final ServiceTemplateDao serviceTemplateDao;
    private final ExecutorService executorService;

    public ServiceTemplateRepository(Application application) {
        TallerDatabase db = TallerDatabase.getInstance(application);
        serviceTemplateDao = db.serviceTemplateDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ServiceTemplate>> getAllTemplates() {
        return serviceTemplateDao.getAllTemplates();
    }

    public void insert(ServiceTemplate serviceTemplate) {
        executorService.execute(() -> serviceTemplateDao.insert(serviceTemplate));
    }

    public void update(ServiceTemplate serviceTemplate) {
        executorService.execute(() -> serviceTemplateDao.update(serviceTemplate));
    }

    public void delete(ServiceTemplate serviceTemplate) {
        executorService.execute(() -> serviceTemplateDao.delete(serviceTemplate));
    }
}

