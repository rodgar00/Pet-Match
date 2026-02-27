package com.rodgar00.petmatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    private TextInputLayout loginTILEmail, loginTILPassword;
    private Button loginButton;
    private TextView loginTVInvitado, loginTVRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTILEmail = findViewById(R.id.LoginTILuserName);
        loginTILPassword = findViewById(R.id.LoginTILpassword);
        loginButton = findViewById(R.id.LoginButton);
        loginTVInvitado = findViewById(R.id.LoginTVInvitado);
        loginTVRegister = findViewById(R.id.LoginTVRegister);

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        loginButton.setOnClickListener(v -> {
            String email = loginTILEmail.getEditText().getText().toString().trim();
            String password = loginTILPassword.getEditText().getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Email y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password, editor);
        });

        loginTVRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
        });

        loginTVInvitado.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        });
    }

    private void loginUser(String email, String password, SharedPreferences.Editor editor) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8000/api/login/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();

                JSONObject response = new JSONObject(result.toString());

                if (response.getBoolean("success")) {
                    JSONObject data = response.getJSONObject("data");
                    String token = data.getString("token");
                    String refreshToken = data.getString("refreshToken");

                    editor.putString("accessToken", token);
                    editor.putString("refreshToken", refreshToken);
                    editor.putString("email", email);
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    });

                } else {
                    runOnUiThread(() -> {
                        try {
                            String error = response.getJSONArray("errors").getString(0);
                            Toast.makeText(Login.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(Login.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Login.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
