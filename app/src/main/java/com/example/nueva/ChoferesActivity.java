package com.example.nueva;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
public class ChoferesActivity extends AppCompatActivity {
    LinearLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choferes);

        contenedor = findViewById(R.id.contenedorChoferes);
        Button btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(view -> {
            Intent intent = new Intent(ChoferesActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        new Thread(() -> {
            try {
                URL url = new URL("https://uteqia.com/api/choferes");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                InputStream is = con.getInputStream();
                Scanner scanner = new Scanner(is);
                StringBuilder result = new StringBuilder();
                while (scanner.hasNext()) result.append(scanner.nextLine());

                JSONArray array = new JSONArray(result.toString());

                runOnUiThread(() -> {
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject obj = array.getJSONObject(i);

                            TextView tarjeta = new TextView(this);
                            tarjeta.setText("ID: " + obj.getInt("id") + "\n" +
                                    "Nombre: " + obj.getString("nombre") + "\n" +
                                    "Cédula: " + obj.getString("cedula") + "\n" +
                                    "Teléfono: " + obj.getString("telefono"));
                            tarjeta.setPadding(20, 20, 20, 20);
                            tarjeta.setBackgroundColor(0xFFDDEEFF); // azul claro
                            tarjeta.setTextSize(16);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 24);
                            tarjeta.setLayoutParams(params);

                            contenedor.addView(tarjeta);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}