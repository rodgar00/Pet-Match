package com.rodgar00.petmatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextInputEditText searchBar;
    RecyclerView recyclerView;
    DogRecycler adapter;

    ArrayList<DogModel> dogList = new ArrayList<>();
    ArrayList<DogModel> listaCompleta = new ArrayList<>();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuHamburguesa, closeMenu;
    ApiInterface api;

    Button btn1, btn2, btn3, btn4;

    // VARIABLE PARA RECORDAR QUÉ TABLA ESTÁ PULSADA
    private String tablaSeleccionada = "adoptados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.characterRecycler);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navView);
        menuHamburguesa = findViewById(R.id.menuHamburguesa);

        closeMenu = navigationView.getHeaderView(0).findViewById(R.id.Close);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new DogRecycler(this, dogList);
        recyclerView.setAdapter(adapter);

        api = ApiClient.getClient().create(ApiInterface.class);

        btn1.setOnClickListener(v -> seleccionarBoton(btn1, "adoptados"));
        btn2.setOnClickListener(v -> seleccionarBoton(btn2, "encontrados"));
        btn3.setOnClickListener(v -> seleccionarBoton(btn3, "perdidos"));
        btn4.setOnClickListener(v -> seleccionarBoton(btn4, "favoritos"));

        // Seleccionamos la primera por defecto al abrir la app
        seleccionarBoton(btn1, "adoptados");

        FloatingActionButton buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarAnimalActivity.class);
            // PASAMOS LA TABLA ACTUAL A LA OTRA PANTALLA
            intent.putExtra("TABLA_DESTINO", tablaSeleccionada);
            startActivity(intent);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString().toLowerCase();
                dogList.clear();

                if (texto.isEmpty()) {
                    dogList.addAll(listaCompleta);
                } else {
                    for (DogModel dog : listaCompleta) {
                        if (dog.getRaza().toLowerCase().contains(texto) || (dog.getCategoria().toLowerCase().contains(texto))) {
                            dogList.add(dog);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        menuHamburguesa.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        closeMenu.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, Profile.class));
            } else if (id == R.id.nav_pets) {
                startActivity(new Intent(MainActivity.this, Pets.class));
            } else if (id == R.id.nav_refugio) {
                startActivity(new Intent(MainActivity.this, Refugio.class));
            } else if (id == R.id.nav_exit) {
                startActivity(new Intent(MainActivity.this, Usuarios.class));
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void seleccionarBoton(Button boton, String tabla) {
        desmarcarTodos();
        boton.setSelected(true);
        tablaSeleccionada = tabla; // ACTUALIZAMOS LA VARIABLE AQUÍ
        buscarAnimales(tabla);
    }

    private void desmarcarTodos() {
        btn1.setSelected(false);
        btn2.setSelected(false);
        btn3.setSelected(false);
        btn4.setSelected(false);
    }

    private void buscarAnimales(String tabla) {
        adapter.setTablaActual(tabla);
        Call<List<DogModel>> call;

        switch (tabla) {
            case "adoptados":
                call = api.getAdoptados();
                break;
            case "encontrados":
                call = api.getEncontrados();
                break;
            case "perdidos":
                call = api.getPerdidos();
                break;
            case "favoritos":
                call = api.getFavoritos();
                break;
            default:
                return;
        }

        call.enqueue(new Callback<List<DogModel>>() {
            @Override
            public void onResponse(Call<List<DogModel>> call, Response<List<DogModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCompleta.clear();
                    listaCompleta.addAll(response.body());

                    dogList.clear();
                    dogList.addAll(listaCompleta);

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Error en la respuesta: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DogModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}