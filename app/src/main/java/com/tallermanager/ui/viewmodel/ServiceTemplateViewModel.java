package com.tallermanager.ui.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.repository.ServiceTemplateRepository;

import java.util.List;

public class ServiceTemplateViewModel extends AndroidViewModel {
    private final ServiceTemplateRepository repository;
    private final LiveData<List<ServiceTemplate>> allTemplates;

    public ServiceTemplateViewModel(@NonNull Application application) {
        super(application);
        repository = new ServiceTemplateRepository(application);
        allTemplates = repository.getAllTemplates();
    }

    public LiveData<List<ServiceTemplate>> getAllTemplates() {
        return allTemplates;
    }

    public void insert(ServiceTemplate serviceTemplate) {
        repository.insert(serviceTemplate);
    }

    public void update(ServiceTemplate serviceTemplate) {
        repository.update(serviceTemplate);
    }

    public void delete(ServiceTemplate serviceTemplate) {
        repository.delete(serviceTemplate);
    }
}
