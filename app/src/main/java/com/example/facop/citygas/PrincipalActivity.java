package com.example.facop.citygas;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class PrincipalActivity extends AppCompatActivity {
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private Button _InvitadoButton;
    private TextView _signupLink;

    GlobalConection url  = null;

    AdminSQLiteOpenHelper admin;

    Usuario usuario = new Usuario();

    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);



        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _InvitadoButton = (Button) findViewById(R.id.btn_invitado);

        admin = new AdminSQLiteOpenHelper(this, "cuenta_bancaria", null, 1);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), activity_signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        _InvitadoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    void Registro(){}

    public void login() {
        Log.d(null,"Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        /*
        final ProgressDialog progressDialog = new ProgressDialog(PrincipalActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        */



        //usuario.setEmail(_emailText.getText().toString());
        //usuario. = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    usuario.setEmail(_emailText.getText().toString());
                    usuario.setContrasenia(_passwordText.getText().toString());
                    String cadena = performGetCall(url.geturl()+"login/"+usuario.getEmail()+"/"+usuario.getContrasenia(),null);
                    Log.d("json recibido", "read: " + cadena);
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(cadena);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Boolean value = null;
                    try {
                        value = jsonObj.getBoolean("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (value){
                        usuario.setIdUsuario(jsonObj.getInt("IdUsuario"));
                        SQLiteDatabase bd = admin.getWritableDatabase();
                        ContentValues registro = new ContentValues();
                        registro.put("IdUsuario", usuario.getIdUsuario());
                        registro.put("Nombre", usuario.getNombre());
                        registro.put("Email", usuario.getEmail());
                        bd.insert("usuario", null, registro);
                        bd.close();
                        onLoginSuccess();

                    }
                    else{
                        onLoginFailed();
                    }

                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        });
        thread.start();



    }



    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        showToast("Inicio correcto");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onLoginFailed() {
        showToast("Fallo el inicio");


    }
    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                _loginButton.setEnabled(true);
                Toast.makeText(PrincipalActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
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
            url = new URL(buildURL(requestURL,getDataParams));

            HttpURLConnection urlConnection  = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-type", "application/json");

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){

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
}
