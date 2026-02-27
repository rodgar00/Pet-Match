package com.rodgar00.petmatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

public class EditorPet extends Activity {

    ImageView closeMenu;
    NavigationView navigationView;

    ImageView imageView;
    TextView nombre, raza, Edad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pets);

        ImageView logoApp = findViewById(R.id.logoApp);

        DrawerLayout drawerLayout = findViewById(R.id.main);

        ImageView menuHamburguesa = findViewById(R.id.menuHamburguesa);
        closeMenu = findViewById(R.id.Close);
        navigationView = findViewById(R.id.navView);

        logoApp.setOnClickListener(v ->{
            Intent intent = new Intent(EditorPet.this, MainActivity.class);
            startActivity(intent);
        });

        imageView = findViewById(R.id.TvDogFoto);
        nombre = findViewById(R.id.tvDogNombre);
        raza = findViewById(R.id.tvDogRazas);
        Edad = findViewById(R.id.tvDogEda);


        Intent intent = getIntent();

        String nombreDog = intent.getStringExtra("nombre");
        String EdadDog = intent.getStringExtra("Edad");
        String RazaDog = intent.getStringExtra("Raza");
        String imagenDog = intent.getStringExtra("imagen");

        if (imagenDog != null && !imagenDog.isEmpty()) {
            Glide.with(this)
                    .load(imagenDog)
                    .into(imageView);
        }


        menuHamburguesa.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });

        closeMenu.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.END);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                startActivity(new Intent(EditorPet.this, Profile.class));
            } else if (id == R.id.nav_pets) {
                startActivity(new Intent(EditorPet.this, Pets.class));
            } else if (id == R.id.nav_refugio) {
                startActivity(new Intent(EditorPet.this, Refugio.class));
            } else if (id == R.id.nav_exit) {
                startActivity(new Intent(EditorPet.this, Usuarios.class));
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // 3. Mostrar los datos en la pantalla
        if (nombreDog != null) nombre.setText(nombreDog);
        if (EdadDog != null) Edad.setText(EdadDog);
        if (RazaDog != null) raza.setText(RazaDog);
        if (imagenDog != null && !imagenDog.isEmpty()) {
            Glide.with(this)
                    .load(imagenDog)
                    .into(imageView);
        }
    }
}
