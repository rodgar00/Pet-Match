package com.rodgar00.petmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class Register extends AppCompatActivity {

    private TextInputLayout registerTILEmail, registerTILPassword, registerTILPasswordConfirm;
    private Button registerButton;
    private TextView registerTVLogin, loginTVInvitado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind views según tu XML
        registerTILEmail = findViewById(R.id.RegisterTILEmail);
        registerTILPassword = findViewById(R.id.RegisterTILPassword);
        registerTILPasswordConfirm = findViewById(R.id.RegisterTILPasswordConfirm);
        registerButton = findViewById(R.id.RegisterButton);
        registerTVLogin = findViewById(R.id.RegisterTVLogin);
        loginTVInvitado = findViewById(R.id.LoginTVInvitado);

        // Botón registrar
        registerButton.setOnClickListener(v -> {
            String email = registerTILEmail.getEditText().getText().toString().trim();
            String password1 = registerTILPassword.getEditText().getText().toString().trim();
            String password2 = registerTILPasswordConfirm.getEditText().getText().toString().trim();

            if (email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                Toast.makeText(Register.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password1.equals(password2)) {
                Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamada al backend
            registerUser(email, password1, password2);
        });

        // Botón login
        registerTVLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
        });

        // Botón invitado
        loginTVInvitado.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        });
    }

    private void registerUser(String email, String password1, String password2) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8000/api/registro/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("username", email);
                body.put("first_name", "Usuario");
                body.put("last_name", "Prueba");
                body.put("password1", password1);
                body.put("password2", password2);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode == 200 || responseCode == 201) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();

                if (responseCode == 200 || responseCode == 201) {
                    runOnUiThread(() -> {
                        Toast.makeText(Register.this, "Usuario Registrado Correctamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    });
                } else {
                    String errorMessage = "Error desconocido";

                    try {
                        JSONObject errorJson = new JSONObject(result.toString());
                        Iterator<String> keys = errorJson.keys();

                        if (keys.hasNext()) {
                            String key = keys.next();
                            Object value = errorJson.get(key);

                            if (value instanceof JSONArray) {
                                errorMessage = ((JSONArray) value).getString(0);
                            } else {
                                errorMessage = value.toString();
                            }
                        }

                    } catch (Exception e) {
                        errorMessage = result.toString();
                    }
                    String finalErrorMessage = errorMessage;
                    runOnUiThread(() ->
                            Toast.makeText(Register.this, finalErrorMessage, Toast.LENGTH_LONG).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Register.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
