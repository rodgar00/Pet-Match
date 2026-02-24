package com.rodgar00.petmatch;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.provider.MediaStore;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Profile extends Activity {
    ImageView closeMenu;

    NavigationView navigationView;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.acitivty_profile);

        DrawerLayout drawerLayout = findViewById(R.id.main);

        ImageView logoApp = findViewById(R.id.logoApp);

        logoApp.setOnClickListener(v -> {

            Intent intent = new Intent(Profile.this, MainActivity.class);

            startActivity(intent);

        });
        ImageView menuHamburguesa = findViewById(R.id.menuHamburguesa);

        closeMenu = findViewById(R.id.Close);

        navigationView = findViewById(R.id.navView);

        menuHamburguesa.setOnClickListener(v -> {

            drawerLayout.openDrawer(GravityCompat.END);

        });
        closeMenu.setOnClickListener(v -> {

            drawerLayout.closeDrawer(GravityCompat.END);

        });

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();



            if (id == R.id.nav_profile) {

                startActivity(new Intent(Profile.this, Profile.class));

            } else if (id == R.id.nav_pets) {

                startActivity(new Intent(Profile.this, Pets.class));

            } else if (id == R.id.nav_refugio) {

                startActivity(new Intent(Profile.this, Refugio.class));

            } else if (id == R.id.nav_exit) {

                startActivity(new Intent(Profile.this, Usuarios.class));

            }

            drawerLayout.closeDrawer(GravityCompat.END);

            return true;

        });

    }

}