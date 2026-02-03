package com.example.gestaller.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.TallerDatabase;
import com.example.gestaller.data.local.dao.ClientDao;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.ui.adapter.PhotoAdapter;
import com.example.gestaller.ui.viewmodel.SharedVoiceViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    private AutoCompleteTextView autoCliente;
    private EditText etClientPhone, etPlate, etNotes;
    private Spinner spBrand, spModel;
    private LinearLayout servicesContainer;
    private RecyclerView photosRecyclerView;
    private Button btnTomarFoto, btnCancel, btnSave;
    private FloatingActionButton fabVoice;

    private VehicleViewModel vehicleViewModel;
    private ServiceTemplateViewModel serviceTemplateViewModel;
    private WorkOrderViewModel workOrderViewModel;
    private SharedVoiceViewModel voiceVm;

    private FirebaseStorage storage;
    private Uri photoUri;
    private final List<String> photoUrls = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<Client> clientList = new ArrayList<>();
    private ClientDao clientDao;

    private ActivityResultLauncher<Intent> voiceRecognitionLauncher;

    private final ActivityResultLauncher<String> requestCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) tomarFoto();
                else Toast.makeText(this, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show();
            }
    );

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && photoUri != null) subirAFirebase(photoUri);
                else Toast.makeText(this, "Captura cancelada o fallida", Toast.LENGTH_SHORT).show();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workorder);

        // ViewModels
        vehicleViewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        serviceTemplateViewModel = new ViewModelProvider(this).get(ServiceTemplateViewModel.class);
        workOrderViewModel = new ViewModelProvider(this).get(WorkOrderViewModel.class);
        voiceVm = new ViewModelProvider(this).get(SharedVoiceViewModel.class);
        storage = FirebaseStorage.getInstance();

        // Views
        autoCliente = findViewById(R.id.autoCliente);
        etClientPhone = findViewById(R.id.etClientPhone);
        spBrand = findViewById(R.id.spBrand);
        spModel = findViewById(R.id.spModel);
        etPlate = findViewById(R.id.etPlate);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        servicesContainer = findViewById(R.id.servicesContainer);
        etNotes = findViewById(R.id.etNotes);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        fabVoice = findViewById(R.id.fabVoice);

        // Setup
        setupPhotoRecycler();
        setupObservers();
        setupClientAutoComplete();
        setupVoiceRecognition();

        // Listeners
        btnTomarFoto.setOnClickListener(v -> verificarPermisosYCapturarFoto());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> guardarOrdenDeTrabajo());
        fabVoice.setOnClickListener(v -> {
            voiceVm.changeState(SharedVoiceViewModel.VoiceState.LISTENING);
            startVoiceRecognition();
        });
    }

    private void setupVoiceRecognition() {
        voiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            String text = results.get(0);
                            Log.d("VoiceRecognition", "Texto reconocido: " + text);
                            Toast.makeText(this, "Escuché: " + text, Toast.LENGTH_SHORT).show();
                            voiceVm.setRecognizedText(text);
                        }
                    }
                }
        );

        voiceVm.getRecognizedText().observe(this, text -> {
            if (text == null || text.trim().isEmpty()) return;

            SharedVoiceViewModel.VoiceState state = voiceVm.getState().getValue();
            if (state != SharedVoiceViewModel.VoiceState.LISTENING) return;

            SharedVoiceViewModel.NavigationTarget target = parseInitialCommand(text);

            if (target != null) {
                voiceVm.setNavigationTarget(target);
                voiceVm.changeState(SharedVoiceViewModel.VoiceState.AWAITING_DATA);
            } else {
                voiceVm.changeState(SharedVoiceViewModel.VoiceState.IDLE);
                Toast.makeText(this, "Comando no reconocido", Toast.LENGTH_SHORT).show();
            }
            voiceVm.setRecognizedText(null);
        });

        voiceVm.getNavigationTarget().observe(this, target -> {
            if (target == null || target == SharedVoiceViewModel.NavigationTarget.NONE) return;

            Intent intent = null;
            switch (target) {
                case NEW_VEHICLE:
                    intent = new Intent(this, AddVehicleActivity.class);
                    intent.putExtra("VOICE_MODE", "AWAITING_DATA");
                    intent.putExtra("VOICE_TARGET", "VEHICLE");
                    break;
                case NEW_ORDER:
                    Toast.makeText(this, "Ya te encuentras en la pantalla de nueva orden.", Toast.LENGTH_SHORT).show();
                    break;
                case NEW_CLIENT:
                    intent = new Intent(this, AddClientActivity.class);
                    intent.putExtra("VOICE_MODE", "AWAITING_DATA");
                    intent.putExtra("VOICE_TARGET", "CLIENT");
                    break;
                case NEW_SERVICE:
                    Toast.makeText(this, "Funcionalidad de Nuevo Servicio próximamente.", Toast.LENGTH_SHORT).show();
                    break;
            }

            if (intent != null) {
                startActivity(intent);
            }
            voiceVm.navigationHandled();
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-419");
        try {
            voiceRecognitionLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    private SharedVoiceViewModel.NavigationTarget parseInitialCommand(String raw) {
        if (raw == null) return null;
        
        String text = raw.toLowerCase().trim();
        // Normalización básica de acentos
        text = text.replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ú", "u");
        // Quitar signos de puntuación
        text = text.replaceAll("[^a-z0-9 ]", "");

        if (text.contains("vehiculo") || text.contains("auto") || text.contains("carro")) {
            return SharedVoiceViewModel.NavigationTarget.NEW_VEHICLE;
        }
        if (text.contains("orden") || text.contains("trabajo")) {
            return SharedVoiceViewModel.NavigationTarget.NEW_ORDER;
        }
        if (text.contains("cliente") || text.contains("persona")) {
            return SharedVoiceViewModel.NavigationTarget.NEW_CLIENT;
        }
        if (text.contains("servicio")) {
            return SharedVoiceViewModel.NavigationTarget.NEW_SERVICE;
        }

        return null;
    }

    private void setupClientAutoComplete() {
        TallerDatabase db = TallerDatabase.getDatabase(this);
        clientDao = db.clientDao();

        clientDao.getAllClients().observe(this, clients -> {
            if (clients != null && !clients.isEmpty()) {
                clientList = clients;
                List<String> nombres = clients.stream().map(Client::getName).collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, nombres);
                autoCliente.setAdapter(adapter);

                autoCliente.setOnItemClickListener((parent, view, position, id) -> {
                    String seleccionado = (String) parent.getItemAtPosition(position);
                    for (Client c : clientList) {
                        if (c.getName().equals(seleccionado)) {
                            etClientPhone.setText(c.getPhone());
                            break;
                        }
                    }
                });
            }
        });
    }

    private void setupPhotoRecycler() {
        photoAdapter = new PhotoAdapter(this, photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);
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

        ArrayAdapter<String> modelAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModel.setAdapter(modelAdapter);
    }

    private void verificarPermisosYCapturarFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            tomarFoto();
        else
            requestCameraPermission.launch(Manifest.permission.CAMERA);
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

            if (intent.resolveActivity(getPackageManager()) != null) takePictureLauncher.launch(intent);
            else Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();

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
                    photoUrls.add(downloadUrl.toString());
                    photosRecyclerView.setVisibility(View.VISIBLE);
                    photoAdapter.notifyItemInserted(photoUrls.size() - 1);
                    Toast.makeText(this, "Foto subida ✅", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir ❌", Toast.LENGTH_SHORT).show();
            Log.e("AddWorkOrderActivity", "Error subida", e);
        });
    }

    private void guardarOrdenDeTrabajo() {
        if (autoCliente.getText().toString().isEmpty()) {
            Toast.makeText(this, "Selecciona un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        List<ServiceTemplate> serviciosSeleccionados = new ArrayList<>();
        for (int i = 0; i < servicesContainer.getChildCount(); i++) {
            View view = servicesContainer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    ServiceTemplate service = (ServiceTemplate) checkBox.getTag();
                    serviciosSeleccionados.add(service);
                }
            }
        }

        if (serviciosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarResumenServicios(serviciosSeleccionados);
    }

    private void mostrarResumenServicios(List<ServiceTemplate> serviciosSeleccionados) {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_services_summary, null);
        LinearLayout container = view.findViewById(R.id.containerServices);
        TextView tvTotal = view.findViewById(R.id.tvTotal);
        Button btnFacturar = view.findViewById(R.id.btnFacturar);

        double total = 0.0;

        for (ServiceTemplate s : serviciosSeleccionados) {
            TextView item = new TextView(this);
            item.setText(String.format("%s – Gs. %.0f", s.getName(), s.getDefaultPrice()));
            item.setTextSize(15);
            item.setPadding(0, 6, 0, 6);
            item.setTextColor(getResources().getColor(android.R.color.black));
            container.addView(item);
            total += s.getDefaultPrice();
        }

        tvTotal.setText(String.format("Total: Gs. %.0f", total));

        btnFacturar.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://ekuatia.set.gov.py/ekuatiai/"));
            startActivity(browserIntent);
        });

        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(
                        this,
                        R.style.ThemeOverlay_Gestaller_BottomSheetDialog
                );

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        bottomSheetDialog.setOnDismissListener(dialog -> {
            guardarOrdenEnBaseDeDatos(serviciosSeleccionados);
        });
    }

    private void guardarOrdenEnBaseDeDatos(List<ServiceTemplate> serviciosSeleccionados) {
        String servicesString = serviciosSeleccionados.stream()
                .map(ServiceTemplate::getName)
                .collect(Collectors.joining(", "));

        String photosString = String.join(",", photoUrls);

        WorkOrder newOrder = new WorkOrder();
        newOrder.setClientName(autoCliente.getText().toString());
        newOrder.setClientPhone(etClientPhone.getText().toString());
        newOrder.setVehicleBrand(spBrand.getSelectedItem() != null ? spBrand.getSelectedItem().toString() : "");
        newOrder.setVehicleModel(spModel.getSelectedItem() != null ? spModel.getSelectedItem().toString() : "");
        newOrder.setVehiclePlate(etPlate.getText().toString());
        newOrder.setServices(servicesString);
        newOrder.setNotes(etNotes.getText().toString());
        newOrder.setPhotoUrl(photosString);
        newOrder.setDate(new Date().getTime());

        workOrderViewModel.insert(newOrder);
        Toast.makeText(this, "Orden guardada ✅", Toast.LENGTH_SHORT).show();
        finish();
    }
}
