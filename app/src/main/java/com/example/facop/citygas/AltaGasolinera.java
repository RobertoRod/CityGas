package com.example.facop.citygas;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AltaGasolinera extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //  Controladores de componentes
    EditText _coordenadaX,_coordenadaY,_nombre;
    List<Gasolinera> Gasolineras = null;
    Spinner _spinner;
    Button _CrearCuente;

    //Variable de conexion a internet
    HttpURLConnection con;
    //Variable para obtener el link de la api
    GlobalConection url = null;

    //Controladores para listener
    ArrayAdapter<String> dataAdapter;
    List<String> categories;

    //Objeto para post
    Ubicacion ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_gasolinera);

        RunDataGasolinerasJson();

        _coordenadaX = (EditText) findViewById(R.id.input_coordenadaX);
        _coordenadaY = (EditText) findViewById(R.id.input_coordenadaY);
        _nombre = (EditText) findViewById(R.id.input_nombre);
        _spinner = (Spinner) findViewById(R.id.spinner);
        _CrearCuente = (Button) findViewById(R.id.btn_registrar);


        _coordenadaX.setText(Double.toString(this.getIntent().getExtras().getDouble("Latitud")));
        _coordenadaY.setText(Double.toString(this.getIntent().getExtras().getDouble("Longitud")));

        propiedadesSpiner();

        ubicacion = new Ubicacion(null,null,null,null,null);
        _CrearCuente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate()) {
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                    // call AsynTask to perform network operation on separate thread
                    new AltaGasolinera.HttpAsyncTask().execute(url.geturl()+"ubicacion");
                    //_emailText.setError("enter a valid email address");
                }
            }
        });


    }

    private boolean validate() {
        boolean noError=true;
        if (_nombre.getText().toString().length() == 0) {
            noError = false;
            _nombre.setError("Nombre no puede quedar vacio");
        }
        return  noError;
    }
    public void propiedadesSpiner(){
        //Funcion al reconocer click
        _spinner.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        categories = new ArrayList<String>();
        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        _spinner.setAdapter(dataAdapter);


    }

    /*  Codigo de listener  */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        ubicacion.setIdGasolinera(Gasolineras.get(position).getIdGasolinera());
        //ubicacion.setIdGasolinera(Gasolineras.get(position).getIdGasolinera());
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void RunDataGasolinerasJson() {
        /*Comprobar la disponibilidad de la Red
         */
        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                new JsonGasolinerasTask().
                        execute(
                                new URL(url.geturl() + "gasolinera"));
            } else {
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /*  Obtener Lista de gasolineras desde la web   */
    public class JsonGasolinerasTask extends AsyncTask<URL, Void, List<Gasolinera>> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

    }

    @Override
    protected List<Gasolinera> doInBackground(URL... urls) {


        try {

            // Establecer la conexión
            con = (HttpURLConnection) urls[0].openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(10000);

            // Obtener el estado del recurso
            int statusCode = con.getResponseCode();

            if (statusCode != 200) {
                Gasolineras = new ArrayList<>();
                Gasolineras.add(new Gasolinera(null, null, null, null));

            } else {
                // Parsear el flujo con formato JSON
                InputStream in = new BufferedInputStream(con.getInputStream());

                JsonGasolineraParser parser = new JsonGasolineraParser();
                //GsonAnimalParser parser = new GsonAnimalParser();

                Gasolineras = parser.leerFlujoJson(in);


            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            con.disconnect();
        }
        return Gasolineras;
    }

    @Override
    protected void onPostExecute(List<Gasolinera> animales) {
            /*
            Asignar los objetos de Json parseados al adaptador
             */
        if (animales != null) {

            int j=0;
            do {
                categories.add(Gasolineras.get(j).getNombre());
                j++;
            } while (j < Gasolineras.size());
            dataAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(
                    getBaseContext(),
                    "Ocurrió un error de Parsing Json",
                    Toast.LENGTH_SHORT)
                    .show();
        }

    }
}


    //  POST
    public String POST(String url, Ubicacion ubicacion){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("IdGasolinera", ubicacion.getIdGasolinera());
            jsonObject.accumulate("Nombre", ubicacion.getNombre());
            jsonObject.accumulate("CoordenadaX", ubicacion.getCoordenadaX());
            jsonObject.accumulate("CoordenadaY", ubicacion.getCoordenadaY());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();


            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();


            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {


            ubicacion.setNombre(_nombre.getText().toString());
            ubicacion.setCoordenadaX(_coordenadaX.getText().toString());
            ubicacion.setCoordenadaY(_coordenadaY.getText().toString());


        }
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],ubicacion);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Registro correcto!", Toast.LENGTH_LONG).show();
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Integer value = null;
            try {
                value = jsonObj.getInt("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (value ==200){
                finish();
            }
        }
    }

}
