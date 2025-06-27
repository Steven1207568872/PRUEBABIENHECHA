package com.example.nueva;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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


public class VolquetasActivity extends AppCompatActivity {
    LinearLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volquetas);

        contenedor = findViewById(R.id.contenedorVolquetas);
        Button btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(view -> {
            Intent intent = new Intent(VolquetasActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Hilo para conexión con la API
        new Thread(() -> {
            try {
                URL url = new URL("https://uteqia.com/api/volquetas");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                int responseCode = con.getResponseCode();
                if (responseCode != 200) {
                    Log.e("API_ERROR", "HTTP error code: " + responseCode);
                    return;
                }

                InputStream is = con.getInputStream();
                Scanner scanner = new Scanner(is);
                StringBuilder result = new StringBuilder();
                while (scanner.hasNext()) result.append(scanner.nextLine());

                JSONArray array = new JSONArray(result.toString());

                runOnUiThread(() -> {
                    try {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            String placa = obj.optString("placa");
                            String dispositivo = "DISP-" + obj.optInt("id");
                            // Aquí podrías usar obj.optString("estado") si tu API lo devuelve.
                            String estado = i % 2 == 0 ? "Disponible" : "En Ruta"; // Simulado

                            // Crear tarjeta visual
                            LinearLayout tarjeta = new LinearLayout(this);
                            tarjeta.setOrientation(LinearLayout.VERTICAL);
                            tarjeta.setPadding(24, 24, 24, 24);
                            tarjeta.setBackgroundColor(0xFFF5F5F5); // fondo suave
                            tarjeta.setElevation(10f);

                            LinearLayout.LayoutParams tarjetaParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            tarjetaParams.setMargins(0, 0, 0, 24);
                            tarjeta.setLayoutParams(tarjetaParams);

                            // Placa
                            TextView txtPlaca = new TextView(this);
                            txtPlaca.setText(placa);
                            txtPlaca.setTextSize(18);
                            txtPlaca.setTypeface(null, android.graphics.Typeface.BOLD);
                            tarjeta.addView(txtPlaca);

                            // Dispositivo
                            TextView txtDisp = new TextView(this);
                            txtDisp.setText(dispositivo);
                            txtDisp.setTextSize(14);
                            txtDisp.setTextColor(0xFF666666);
                            tarjeta.addView(txtDisp);

                            // Estado visual
                            TextView txtEstado = new TextView(this);
                            txtEstado.setText(estado);
                            txtEstado.setTextSize(14);
                            txtEstado.setTypeface(null, android.graphics.Typeface.BOLD);
                            txtEstado.setTextColor(0xFFFFFFFF);
                            txtEstado.setGravity(Gravity.CENTER);
                            txtEstado.setPadding(24, 8, 24, 8);

                            int colorFondo = 0xFFBDBDBD; // gris claro

                            if (estado.equalsIgnoreCase("Disponible")) {
                                colorFondo = 0xFF4CAF50; // verde
                            } else if (estado.equalsIgnoreCase("En Ruta")) {
                                colorFondo = 0xFFFF9800; // naranja
                            }

                            txtEstado.setBackgroundColor(colorFondo);

                            LinearLayout.LayoutParams estadoParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            estadoParams.topMargin = 16;
                            txtEstado.setLayoutParams(estadoParams);

                            tarjeta.addView(txtEstado);

                            // Agregar al contenedor principal
                            contenedor.addView(tarjeta);
                        }
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", "Error al procesar JSON", e);
                    }
                });

            } catch (Exception e) {
                Log.e("CONEXION_ERROR", "Error en conexión API", e);
            }
        }).start();
    }
}