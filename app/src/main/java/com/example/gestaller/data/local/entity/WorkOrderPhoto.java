
package com.example.gestaller.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WorkOrderPhoto {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int workOrderId;
    private String photoUri;

    public WorkOrderPhoto(int workOrderId, String photoUri) {
        this.workOrderId = workOrderId;
        this.photoUri = photoUri;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(int workOrderId) { this.workOrderId = workOrderId; }
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
}
