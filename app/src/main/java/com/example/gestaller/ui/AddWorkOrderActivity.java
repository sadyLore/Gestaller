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
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
import java.util.List;

public class AddWorkOrderActivity extends AppCompatActivity {

    // Campos del layout
    private EditText etClientName, etPlate, etNotes;
    private Spinner spBrand, spModel;
    private LinearLayout servicesContainer;
    private ImageView imgPreview;
    private Button btnTomarFoto, btnCancel, btnSave;

    // Para cámara y Firebase
    private FirebaseStorage storage;
    private Uri photoUri;
    private String photoUrl = "";

    // Repositorio
    private WorkOrderRepository repository;

    // Launcher para permisos (CORREGIDO)
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
                    Log.d("AddWorkOrderActivity", "Permisos concedidos: Cámara y Almacenamiento");
                } else {
                    Toast.makeText(this, "Se requieren permisos de cámara y almacenamiento", Toast.LENGTH_SHORT).show();
                    Log.w("AddWorkOrderActivity", "Permisos denegados");
                }
            }
    );

    // Launcher para la cámara
    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && photoUri != null) {
                    subirAFirebase(photoUri);
                    Log.d("AddWorkOrderActivity", "Foto capturada exitosamente con URI: " + photoUri);
                } else {
                    Toast.makeText(this, "Captura cancelada o fallida", Toast.LENGTH_SHORT).show();
                    Log.w("AddWorkOrderActivity", "Captura fallida");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workorder);


        repository = new WorkOrderRepository(getApplication());
        storage = FirebaseStorage.getInstance();

        etClientName = findViewById(R.id.etClientName);
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
    }

    // Lógica de permisos (CORREGIDA)
    private void verificarPermisosYCapturarFoto() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        String[] permissionsToRequest;
        boolean storagePermissionsGranted;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 y superior: Solo se necesita READ_MEDIA_IMAGES
            storagePermissionsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
            permissionsToRequest = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            // Android 12 y inferior: Se necesitan ambos, READ y WRITE
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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            List<ResolveInfo> cameraApps = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo app : cameraApps) {
                grantUriPermission(app.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            if (intent.resolveActivity(getPackageManager()) != null) {
                takePictureLauncher.launch(intent);
                Toast.makeText(this, "Abriendo cámara...", Toast.LENGTH_SHORT).show();
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

        WorkOrder newOrder = new WorkOrder();
        newOrder.setId(0); // Placeholder; Room lo generará auto
        newOrder.setTotalPrice(0); // Placeholder; calcula de servicesContainer si tienes lógica
        newOrder.setDate(new Date().getTime());

        // Services: Placeholder (agrega lógica para recopilar de servicesContainer, ej: lista de strings)
        newOrder.setServices("Servicios: Pendientes de implementar");

        // Notes: Concatena todos los datos del form (workaround hasta agregar campos a WorkOrder)
        String notes = "Cliente: " + etClientName.getText().toString() +
                "\nMarca: " + (spBrand.getSelectedItem() != null ? spBrand.getSelectedItem().toString() : "") +
                "\nModelo: " + (spModel.getSelectedItem() != null ? spModel.getSelectedItem().toString() : "") +
                "\nChapa: " + etPlate.getText().toString() +
                "\nNotas adicionales: " + etNotes.getText().toString() +
                "\nFoto URL: " + photoUrl;
        newOrder.setNotes(notes);

        repository.insert(newOrder); // Asumiendo que insert existe y es asíncrono
        Toast.makeText(this, "Orden guardada ✅", Toast.LENGTH_SHORT).show();
        finish(); // Cierra y vuelve a la lista (se actualizará via observe)
    }
}