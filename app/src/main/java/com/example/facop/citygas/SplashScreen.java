package com.example.facop.citygas;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class SplashScreen extends Activity {
    private final int DURACION_SPLASH = 4000;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        checkPermission();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent =null;

                if (consulta()){
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                }else{
                    intent = new Intent(SplashScreen.this, PrincipalActivity.class);
                }
                startActivity(intent);
                finish();

            }

            ;
        }, DURACION_SPLASH);
    }

    public boolean consulta() {
        boolean existe = false;
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuenta_bancaria", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            String _id = "1";
            Cursor fila = bd.rawQuery("Select * from usuario where id=" + _id, null);
            if (fila.moveToFirst()) {
                existe = true;
            }
            bd.close();
        } catch (Exception e) {
            Log.e("Error", "Error al consultar un registro");
        }
        return existe;
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            //Toast.makeText(this, "Esta version no soporta los permisos especiales " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        } else {

            int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);

                //Toast.makeText(this, "Solicitando permisos", Toast.LENGTH_LONG).show();

            } else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(this, "Los permisos ya han sido otorgados ", Toast.LENGTH_LONG).show();


            }

        }

        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos otorgados! :-) ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permisos no otorgados ! :-( ", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
