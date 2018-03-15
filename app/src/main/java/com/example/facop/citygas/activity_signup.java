package com.example.facop.citygas;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class activity_signup extends AppCompatActivity  {


    //private TextInputLayout _emailBox;



    EditText _emailText,_nombreText,_pass,_repass;
    Button _CrearCuente;

    Usuario usuario;
    GlobalConection url  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Obtener referencia a la vista.
        _nombreText = (EditText) findViewById(R.id.input_nombre);
        _emailText = (EditText) findViewById(R.id.input_correo);
        _pass = (EditText) findViewById(R.id.input_password);
        _repass = (EditText) findViewById(R.id.input_reEnterPassword);
        _CrearCuente = (Button) findViewById(R.id.btn_signup);



        _CrearCuente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate()) {
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                    // call AsynTask to perform network operation on separate thread
                    new HttpAsyncTask().execute(url.geturl()+"usuario");
                    //_emailText.setError("enter a valid email address");
                }
            }
        });
    }

    public String POST(String url, Usuario usuario){
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
            jsonObject.accumulate("IdTipoUsuario", "2");
            jsonObject.accumulate("Nombre", usuario.getNombre());
            jsonObject.accumulate("Email", usuario.getEmail());
            jsonObject.accumulate("Contrasenia", usuario.getContrasenia());

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



    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

            usuario = new Usuario();
            usuario.setNombre(_nombreText.getText().toString());
            usuario.setEmail(_emailText.getText().toString());
            usuario.setContrasenia(_pass.getText().toString());
            usuario.setIdTipoUsuario(1);

        }
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],usuario);
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


    private boolean validate() {
        boolean noError=true;
        int as = _nombreText.getText().toString().trim().length();
        if (_nombreText.getText().toString().length() == 0) {
            noError = false;
            _nombreText.setError("Nombre no puede quedar vacio");
        }
         if (_emailText.getText().toString().trim().length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(_emailText.getText().toString()).matches()) {
             noError = false;
             _emailText.setError("Introduzca un correo valido");
         }
        if (!(_repass.getText().toString().equals(_pass.getText().toString()))) {
            noError=false;
            _repass.setError("Las contraseñas no coinciden");
            _pass.setError("Las contraseñas no coinciden");
        }
         if (_pass.getText().toString().trim().length() == 0) {
             noError = false;
             _pass.setError("Contraseña no puede quedar vacia");
         }
         if (_repass.getText().toString().trim().length() == 0) {
             noError = false;
             _repass.setError("Debe repetir la contraseña");
         }
        return  noError;
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

    private void toast(String mesage) {
        Toast.makeText(getBaseContext(), mesage, Toast.LENGTH_LONG).show();
    }


}