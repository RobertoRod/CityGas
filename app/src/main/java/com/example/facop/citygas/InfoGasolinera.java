package com.example.facop.citygas;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class InfoGasolinera extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {


    Double LongitudX, LongitudY;
    TextView _lbl_nombre, _lbl_gasolinera;
    //Variable para obtener el link de la api
    GlobalConection url = null;

    LinearLayout constrain ;

    int IdGasolinera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_gasolinera);

        constrain = (LinearLayout)findViewById(R.id.info);



        _lbl_nombre = (TextView) findViewById(R.id.lbl_nombre);
        _lbl_gasolinera = (TextView) findViewById(R.id.lbl_gasolinera);
        String[] bar = this.getIntent().getExtras().getString("Snippet").split(" ");
        IdGasolinera = Integer.parseInt(bar[0]);
        _lbl_nombre.setText("Nombre: " + this.getIntent().getExtras().getString("Title"));
        _lbl_gasolinera.setText("Gasolinera: " + bar[1]);


        LongitudX = this.getIntent().getExtras().getDouble("Latitud");
        LongitudY = this.getIntent().getExtras().getDouble("Longitud");

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String cadena = performGetCall(url.geturl() + "listaservicios/" + IdGasolinera, null);
                    Log.d("json recibido", "read: " + cadena);
                    JSONArray jsonObj = null;
                    try {
                        jsonObj = new JSONArray(cadena);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String Gasolina;
                    Double Precio;
                    try {
                        AgregarTextView("Servicios:");
                        for (int i = 0; i < jsonObj.length(); i++) {
                            JSONObject row = jsonObj.getJSONObject(i);
                            Precio = row.getDouble("Precio");
                            Gasolina= row.getString("Descripcion");
                            AgregarTextView("*" + Gasolina+ ": $" + Precio);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        });
        thread.start();

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setPosition(new LatLng(LongitudX, LongitudY));
    }


    public static String buildURL(String url, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build().toString();
    }

    public static String performGetCall(String requestURL,
                                        HashMap<String, String> getDataParams) {

        URL url;
        StringBuilder response = new StringBuilder();
        try {
            url = new URL(buildURL(requestURL, getDataParams));

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-type", "application/json");

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

            }
            urlConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    public void AgregarTextView(final String Mensaje){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView valueTV = new TextView(InfoGasolinera.this);
                valueTV.setText(Mensaje);
                valueTV.setTextColor(Color.parseColor("#FFFFFF"));
                valueTV.setTextSize(18);
                constrain.addView(valueTV);


            }
        });

    }
}
