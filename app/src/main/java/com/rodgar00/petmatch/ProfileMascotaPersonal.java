package com.rodgar00.petmatch;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileMascotaPersonal extends Activity {
    private EditText editNombre, editEdad, editRaza;
    private Button btnSeleccionarImagen, btnAgregarAnimal;
    private ImageView imgPreview;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_animal_personal);

        // 1. Inicializar vistas
        editNombre = findViewById(R.id.editNombre);
        editEdad = findViewById(R.id.editEdad);
        editRaza = findViewById(R.id.editRaza);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnAgregarAnimal = findViewById(R.id.btnAgregarAnimal);
        imgPreview = findViewById(R.id.imgPreview);

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        btnAgregarAnimal.setOnClickListener(v -> guardarMascota());
    }

    private void abrirGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
            imgPreview.setVisibility(View.VISIBLE);
        }
    }

    private void guardarMascota() {
        String nombre = editNombre.getText().toString().trim();
        String edad = editEdad.getText().toString().trim();
        String raza = editRaza.getText().toString().trim();
        String uriString = (imageUri != null) ? imageUri.toString() : "";

        if (nombre.isEmpty() || edad.isEmpty() || raza.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("nuevo_nombre", nombre);
        resultIntent.putExtra("nuevo_edad", edad);
        resultIntent.putExtra("nuevo_raza", raza);
        resultIntent.putExtra("nuevo_imagen", uriString);

        setResult(RESULT_OK, resultIntent);
        SubirMascotaPersonal(nombre, edad, raza);
    }

    private void SubirMascotaPersonal(String nombre, String edad, String raza) {
        // 1. Recuperar el email guardado
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String correoLogueado = sharedPref.getString("email", "");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // 2. Crear los RequestBody de texto
        RequestBody rbNombre = RequestBody.create(MultipartBody.FORM, nombre);
        RequestBody rbEdad = RequestBody.create(MultipartBody.FORM, edad);
        RequestBody rbRaza = RequestBody.create(MultipartBody.FORM, raza);
        RequestBody rbEmail = RequestBody.create(MultipartBody.FORM, correoLogueado);

        // 3. Preparar la imagen (MultipartBody.Part)
        MultipartBody.Part imagePart = null; // Cambiamos el nombre para no repetir
        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] bytes = getBytes(inputStream);

                RequestBody requestFile = RequestBody.create(
                        MediaType.parse(getContentResolver().getType(imageUri)),
                        bytes
                );
                // "imagen" debe coincidir con el nombre en Django
                imagePart = MultipartBody.Part.createFormData("imagen", "mascota.jpg", requestFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Call<DogModel> call = apiInterface.crearMascotaPersonal(rbNombre, rbEdad, rbRaza, rbEmail, imagePart);

        call.enqueue(new Callback<DogModel>() {
            @Override
            public void onResponse(Call<DogModel> call, Response<DogModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileMascotaPersonal.this, "¡Mascota guardada!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ProfileMascotaPersonal.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DogModel> call, Throwable t) {
                Toast.makeText(ProfileMascotaPersonal.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public byte[] getBytes(InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
