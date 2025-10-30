package com.example.gestaller.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tallermanager.ui.viewmodel.ServiceTemplateViewModel;
import com.tallermanager.ui.viewmodel.VehicleViewModel;
import com.tallermanager.ui.viewmodel.WorkOrderViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AddWorkOrderActivity extends AppCompatActivity {

    private EditText etClientName, etClientPhone, etPlate, etNotes;
    private Spinner spBrand, spModel;
    private LinearLayout servicesContainer;
    private ImageView imgPreview;
    private Button btnTomarFoto, btnCancel, btnSave;

    private VehicleViewModel vehicleViewModel;
    private ServiceTemplateViewModel serviceTemplateViewModel;
    private WorkOrderViewModel workOrderViewModel;

    private FirebaseStorage storage;
    private Uri photoUri;
    private String photoUrl = "";

    private List<Vehicle> vehicleList = new ArrayList<>();

    private final ActivityResultLauncher<String[]> requestPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean cameraGranted = Boolean.TRUE.equals(permissions.get(Manifest.permission.CAMERA));
                boolean storageGranted;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    storageGranted = Boolean.TRUE.equals(permissions.get(Manifest.permission.READ_MEDIA_IMAGES));
                } else {
                    storageGranted = Boolean.TRUE.equals(permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE))
                            && Boolean.TRUE.equals(permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
                }

                if (cameraGranted && storageGranted) {
                    tomarFoto();
                } else {
                    Toast.makeText(this, "Se requieren permisos de cámara y almacenamiento", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && photoUri != null) {
                    subirAFirebase(photoUri);
                } else {
                    Toast.makeText(this, "Captura cancelada o fallida", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workorder);

        vehicleViewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        serviceTemplateViewModel = new ViewModelProvider(this).get(ServiceTemplateViewModel.class);
        workOrderViewModel = new ViewModelProvider(this).get(WorkOrderViewModel.class);

        storage = FirebaseStorage.getInstance();

        etClientName = findViewById(R.id.etClientName);
        etClientPhone = findViewById(R.id.etClientPhone);
        spBrand = findViewById(R.id.spBrand);
        spModel = findViewById(R.id.spModel);
        etPlate = findViewById(R.id.etPlate);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        imgPreview = findViewById(R.id.imgPreview);
        servicesContainer = findViewById(R.id.servicesContainer);
        etNotes = findViewById(R.id.etNotes);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        btnTomarFoto.setOnClickListener(v -> verificarPermisosYCapturarFoto());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> guardarOrdenDeTrabajo());

        setupObservers();
    }

    private void setupObservers() {
        vehicleViewModel.getAll().observe(this, vehicles -> {
            if (vehicles != null && !vehicles.isEmpty()) {
                this.vehicleList = vehicles;
                List<String> brands = vehicles.stream()
                        .map(Vehicle::getBrand)
                        .distinct()
                        .collect(Collectors.toList());

                ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
                brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spBrand.setAdapter(brandAdapter);

                spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedBrand = (String) parent.getItemAtPosition(position);
                        updateModelSpinner(selectedBrand);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        spModel.setAdapter(null);
                    }
                });
            }
        });

        serviceTemplateViewModel.getAllTemplates().observe(this, serviceTemplates -> {
            if (serviceTemplates != null) {
                servicesContainer.removeAllViews();
                for (ServiceTemplate service : serviceTemplates) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(service.getName());
                    checkBox.setTag(service);
                    servicesContainer.addView(checkBox);
                }
            }
        });
    }

    private void updateModelSpinner(String brand) {
        List<String> models = vehicleList.stream()
                .filter(v -> v.getBrand().equals(brand))
                .map(Vehicle::getModel)
                .distinct()
                .collect(Collectors.toList());

        ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModel.setAdapter(modelAdapter);
    }

    private void verificarPermisosYCapturarFoto() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        String[] permissionsToRequest;
        boolean storagePermissionsGranted;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissionsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
            permissionsToRequest = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            boolean readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            storagePermissionsGranted = readPermission && writePermission;
            permissionsToRequest = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        if (!cameraPermission || !storagePermissionsGranted) {
            requestPermissions.launch(permissionsToRequest);
        } else {
            tomarFoto();
        }
    }

    private void tomarFoto() {
        try {
            File photoFile = new File(getExternalCacheDir(), "foto_" + System.currentTimeMillis() + ".jpg");
            photoUri = FileProvider.getUriForFile(this, "com.example.gestaller.provider", photoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            List<ResolveInfo> cameraApps = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo app : cameraApps) {
                grantUriPermission(app.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            if (intent.resolveActivity(getPackageManager()) != null) {
                takePictureLauncher.launch(intent);
            } else {
                Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir cámara: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("AddWorkOrderActivity", "Error cámara", e);
        }
    }

    private void subirAFirebase(Uri uri) {
        String fileName = "gestaller/fotos/" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = storage.getReference().child(fileName);
        UploadTask uploadTask = storageRef.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    photoUrl = downloadUrl.toString();
                    Glide.with(this).load(photoUrl).into(imgPreview);
                    imgPreview.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Foto subida ✅", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir ❌", Toast.LENGTH_SHORT).show();
            Log.e("AddWorkOrderActivity", "Error subida", e);
        });
    }

    private void guardarOrdenDeTrabajo() {
        if (etClientName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedServices = new ArrayList<>();
        for (int i = 0; i < servicesContainer.getChildCount(); i++) {
            View view = servicesContainer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    selectedServices.add(checkBox.getText().toString());
                }
            }
        }

        String servicesString = String.join(", ", selectedServices);

        WorkOrder newOrder = new WorkOrder();
        newOrder.setClientName(etClientName.getText().toString());
        newOrder.setClientPhone(etClientPhone.getText().toString());
        newOrder.setVehicleBrand(spBrand.getSelectedItem() != null ? spBrand.getSelectedItem().toString() : "");
        newOrder.setVehicleModel(spModel.getSelectedItem() != null ? spModel.getSelectedItem().toString() : "");
        newOrder.setVehiclePlate(etPlate.getText().toString());
        newOrder.setServices(servicesString);
        newOrder.setNotes(etNotes.getText().toString());
        newOrder.setPhotoUrl(photoUrl);
        newOrder.setDate(new Date().getTime());

        workOrderViewModel.insert(newOrder);
        Toast.makeText(this, "Orden guardada ✅", Toast.LENGTH_SHORT).show();
        finish();
    }
}
