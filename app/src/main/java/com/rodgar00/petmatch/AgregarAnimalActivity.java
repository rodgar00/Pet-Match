package com.rodgar00.petmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarAnimalActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText nombreEdit, duenyoEdit, edadEdit, localizacionEdit,
            descripcionEdit, categoriaEdit, razaEdit;
    Button btnSeleccionarImagen, btnAgregarAnimal;
    ImageView imgPreview;
    Uri imageUri;
    ApiInterface api;

    // VARIABLE PARA SABER A QUÉ TABLA SUBIRLO
    private String tablaDestino = "adoptados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_animal);

        nombreEdit = findViewById(R.id.editNombre);
        duenyoEdit = findViewById(R.id.editDuenyo);
        edadEdit = findViewById(R.id.editEdad);
        localizacionEdit = findViewById(R.id.editLocalizacion);
        descripcionEdit = findViewById(R.id.editDescripcion);
        categoriaEdit = findViewById(R.id.editCategoria);
        razaEdit = findViewById(R.id.editRaza);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnAgregarAnimal = findViewById(R.id.btnAgregarAnimal);
        imgPreview = findViewById(R.id.imgPreview);

        api = ApiClient.getClient().create(ApiInterface.class);

        // RECUPERAMOS LA TABLA SELECCIONADA DESDE EL MAINACTIVITY
        if (getIntent().hasExtra("TABLA_DESTINO")) {
            tablaDestino = getIntent().getStringExtra("TABLA_DESTINO");
        }

        // Si por casualidad se intenta añadir desde favoritos, forzamos a adoptados
        if (tablaDestino != null && tablaDestino.equals("favoritos")) {
            tablaDestino = "adoptados";
        }

        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
        btnAgregarAnimal.setOnClickListener(v -> subirAnimal());
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
            imgPreview.setVisibility(ImageView.VISIBLE);
        }
    }

    private void subirAnimal() {
        if (nombreEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nombre = RequestBody.create(MediaType.parse("text/plain"), nombreEdit.getText().toString());
        RequestBody duenyo = RequestBody.create(MediaType.parse("text/plain"), duenyoEdit.getText().toString());
        RequestBody edad = RequestBody.create(MediaType.parse("text/plain"), edadEdit.getText().toString());
        RequestBody localizacion = RequestBody.create(MediaType.parse("text/plain"), localizacionEdit.getText().toString());
        RequestBody descripcion = RequestBody.create(MediaType.parse("text/plain"), descripcionEdit.getText().toString());
        RequestBody categoria = RequestBody.create(MediaType.parse("text/plain"), categoriaEdit.getText().toString());
        RequestBody raza = RequestBody.create(MediaType.parse("text/plain"), razaEdit.getText().toString());

        MultipartBody.Part imagenPart = null;
        if (imageUri != null) {
            if (Build.VERSION.SDK_INT >= 30) {
                if (!Environment.isExternalStorageManager()) {
                    Intent getpermission = new Intent();
                    getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(getpermission);
                }
            }

            File file = new File(FileUtils.getPath(this, imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            imagenPart = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);
        }

        Call<DogModel> call;

        // ELEGIMOS EL ENDPOINT CORRECTO DEPENDIENDO DE LA TABLA
        switch (tablaDestino) {
            case "encontrados":
                call = api.crearEncontrado(nombre, duenyo, edad, localizacion, descripcion, categoria, raza, imagenPart);
                break;
            case "perdidos":
                call = api.crearPerdido(nombre, duenyo, edad, localizacion, descripcion, categoria, raza, imagenPart);
                break;
            case "adoptados":
            default:
                call = api.crearAdoptado(nombre, duenyo, edad, localizacion, descripcion, categoria, raza, imagenPart);
                break;
        }

        call.enqueue(new Callback<DogModel>() {
            @Override
            public void onResponse(Call<DogModel> call, Response<DogModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AgregarAnimalActivity.this, "Añadido correctamente a " + tablaDestino, Toast.LENGTH_SHORT).show();
                    finish(); // cerrar actividad
                } else {
                    Toast.makeText(AgregarAnimalActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DogModel> call, Throwable t) {
                Toast.makeText(AgregarAnimalActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}