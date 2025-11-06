package com.example.gestaller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gestaller.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class EmployeeHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAddWork;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        fabAddWork = findViewById(R.id.fabAddWork);

        sharedPreferences = getSharedPreferences("GestallerPrefs", MODE_PRIVATE);

        findViewById(R.id.btnMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        View headerView = navigationView.getHeaderView(0);
        TextView tvUserRole = headerView.findViewById(R.id.tvUserRole);
        tvUserRole.setText("Usuario: colaborador");

        navigationView.getMenu().findItem(R.id.nav_vehiculos).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_servicios).setVisible(false);

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        fabAddWork.setOnClickListener(v -> startActivity(new Intent(this, AddWorkOrderActivity.class)));
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            startActivity(new Intent(this, ClientListActivity.class));
        } else if (id == R.id.nav_trabajos) {
            startActivity(new Intent(this, WorkOrderListActivity.class));
        } else if (id == R.id.nav_theme_toggle) {
            toggleTheme();
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        recreate();
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userRole");
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
