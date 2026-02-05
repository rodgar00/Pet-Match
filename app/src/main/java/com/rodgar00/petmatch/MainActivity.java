package com.rodgar00.petmatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextInputEditText searchBar;
    RecyclerView recyclerView;
    DogRecycler adapter;
    ArrayList<DogModel> dogList = new ArrayList<>();
    ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.characterRecycler);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new DogRecycler(this, dogList);
        recyclerView.setAdapter(adapter);

        api = ApiClient.getClient().create(ApiInterface.class);

        buscarPerro("husky");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String breed = s.toString().trim().toLowerCase();

                if (breed.length() >= 3) {
                    buscarPerro(breed);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void buscarPerro(String breed) {

        int NUM_IMAGENES = 20;

        api.getDogsByBreed(breed, NUM_IMAGENES).enqueue(new Callback<DogResponse>() {
            @Override
            public void onResponse(Call<DogResponse> call, Response<DogResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    dogList.clear();

                    for (String url : response.body().getImageUrls()) {
                        dogList.add(new DogModel(breed, url));
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    dogList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<DogResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
