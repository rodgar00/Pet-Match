package com.rodgar00.petmatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Refugio extends Activity {

    ImageView closeMenu;
    NavigationView navigationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refugio);

        DrawerLayout drawerLayout = findViewById(R.id.main);

        ImageView logoApp = findViewById(R.id.logoApp);

        logoApp.setOnClickListener(v -> {
            Intent intent = new Intent(Refugio.this, MainActivity.class);
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
                startActivity(new Intent(Refugio.this, Profile.class));
            } else if (id == R.id.nav_pets) {
                startActivity(new Intent(Refugio.this, Pets.class));
            } else if (id == R.id.nav_refugio) {
                startActivity(new Intent(Refugio.this, Refugio.class));
            } else if (id == R.id.nav_exit) {
                startActivity(new Intent(Refugio.this, Usuarios.class));
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });


        CardView cardRefugio = findViewById(R.id.cardrefugio1);
        final LinearLayout layoutDetalles = findViewById(R.id.layoutDetalles);

        cardRefugio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutDetalles.getVisibility() == View.GONE) {
                    layoutDetalles.setVisibility(View.VISIBLE);
                } else {
                    layoutDetalles.setVisibility(View.GONE);
                }
            }
        });
        CardView cardRefugio2 = findViewById(R.id.cardrefugio2);
        final LinearLayout layoutDetalles2 = findViewById(R.id.layoutDetalles2);

        cardRefugio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutDetalles2.getVisibility() == View.GONE) {
                    layoutDetalles2.setVisibility(View.VISIBLE);
                } else {
                    layoutDetalles2.setVisibility(View.GONE);
                }
            }
        });
        CardView cardRefugio3 = findViewById(R.id.cardrefugio3);
        final LinearLayout layoutDetalles3 = findViewById(R.id.layoutDetalles3);

        cardRefugio3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutDetalles3.getVisibility() == View.GONE) {
                    layoutDetalles3.setVisibility(View.VISIBLE);
                } else {
                    layoutDetalles3.setVisibility(View.GONE);
                }
            }
        });
        CardView cardRefugio4 = findViewById(R.id.cardrefugio4);
        final LinearLayout layoutDetalles4 = findViewById(R.id.layoutDetalles4);

        cardRefugio4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutDetalles4.getVisibility() == View.GONE)  {
                    layoutDetalles4.setVisibility(View.VISIBLE);
                } else {
                    layoutDetalles4.setVisibility(View.GONE);
                }
            }
        });

    }
}