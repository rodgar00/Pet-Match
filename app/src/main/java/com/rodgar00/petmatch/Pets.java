package com.rodgar00.petmatch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Pets extends Activity {
    ImageView closeMenu;
    NavigationView navigationView;
    private RecyclerView recyclerView;
    private PetAdapter adapter;
    private List<DogModel> petList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets); // Solo una vez

        // 1. Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPets);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PetAdapter(this, petList);
        recyclerView.setAdapter(adapter);

        // 2. Referencias a Vistas
        ImageView logoApp = findViewById(R.id.logoApp);
        DrawerLayout drawerLayout = findViewById(R.id.main);
        ImageView menuHamburguesa = findViewById(R.id.menuHamburguesa);
        closeMenu = findViewById(R.id.Close);
        navigationView = findViewById(R.id.navView);
        com.google.android.material.floatingactionbutton.FloatingActionButton btnAddPet = findViewById(R.id.btnAddPet);

        // 3. Listeners
        if (btnAddPet != null) {
            btnAddPet.setOnClickListener(v -> {
                Intent intent = new Intent(Pets.this, ProfileMascotaPersonal.class);
                startActivityForResult(intent, 100);
            });
        }

        logoApp.setOnClickListener(v -> startActivity(new Intent(Pets.this, MainActivity.class)));
        menuHamburguesa.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        closeMenu.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));

        configurarNavigation(drawerLayout);

        // 4. Cargar datos
        cargarMascotas();
    }

    private void configurarNavigation(DrawerLayout drawerLayout) {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) startActivity(new Intent(Pets.this, Profile.class));
            else if (id == R.id.nav_pets) cargarMascotas(); // Ya estamos aquí, solo refresca
            else if (id == R.id.nav_refugio) startActivity(new Intent(Pets.this, Refugio.class));
            else if (id == R.id.nav_exit) startActivity(new Intent(Pets.this, Usuarios.class));

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void cargarMascotas() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String correoLogueado = sharedPref.getString("email", null);

        // 2. Si no hay correo (es invitado), mostrar mensaje y no llamar a la API
        if (correoLogueado == null) {
            petList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Inicia sesión para ver tus mascotas", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Llamar a la API pasando el correo
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<List<DogModel>> call = api.getMascotasPersonales(correoLogueado);

        call.enqueue(new Callback<List<DogModel>>() {
            @Override
            public void onResponse(Call<List<DogModel>> call, Response<List<DogModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    petList.clear();
                    petList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<DogModel>> call, Throwable t) {
                Toast.makeText(Pets.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            cargarMascotas(); // Refresca tras añadir una nueva
        }
    }
}

