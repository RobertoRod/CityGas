package com.example.facop.citygas;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    FloatingActionButton _btnAdd;

    /*Variables para el movimiento del gps */
    private LocationManager locationManager;
    private static final long MIN_TIME = 30000;
    private static final float MIN_DISTANCE = 5;
    Location locationG;
    int Zoom =17;
    int ZoomMetros=100;

    /*  Variable para el control del mapa*/
    private GoogleMap mMap;

    //Listas para iterar la aplicacion
    List<Gasolinera> Gasolineras = null;
    List<Ubicacion> Ubicaciones = null;

    //Variable para obtener el link de la api
    GlobalConection url = null;

    //Variable de conexion a internet
    HttpURLConnection con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RunDataGasolinerasJson();
        RunDataUbicacionesJson();


        // Obteniendo un Manejador de GoogleMapFragment
        MapFragment map = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map.getMapAsync(this);

        PropiedadesBoton();
        PropiedadesBotonesZoom();
        GPSStart();
    }

    public void PropiedadesBotonesZoom() {
        FloatingActionButton _zoomMas = (FloatingActionButton) findViewById(R.id.zoomMas);
        FloatingActionButton _zoomMenos = (FloatingActionButton) findViewById(R.id.zoomMenos);
        _zoomMas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Zoom == 17){
                    return;
                }
                Zoom++;
                ZoomMetros=ZoomMetros-200;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(Zoom));
            }
        });

        _zoomMenos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Zoom == 1){
                    return;
                }
                Zoom--;
                ZoomMetros=ZoomMetros+200;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(Zoom));
                     }
        });

    }
    //Funcion que asignapropiedades al boton
    public void PropiedadesBoton() {
        _btnAdd = (FloatingActionButton) findViewById(R.id.add);
        if (consulta())
            _btnAdd.show();
        else
            _btnAdd.hide();


        _btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent intent = null;
                Double Latitud, Longitus;
                Latitud = locationG.getLatitude();
                Longitus = locationG.getLongitude();
                Intent intent = new Intent(MainActivity.this, AltaGasolinera.class);
                intent.putExtra("Latitud", Latitud);
                intent.putExtra("Longitud", Longitus);
                startActivity(intent);
            }
        });
    }

    /*  Metodo para saber si el usuario esta logueado o es invitado */
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

    /*  Conexiones a internet   */
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

    public void RunDataUbicacionesJson() {
        /*Comprobar la disponibilidad de la Red
         */
        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                new JsonUbicacionesTask().
                        execute(
                                new URL(url.geturl() + "ubicacion"));
            } else {
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /*  Parseo de datos */
    //Clase creada para obtener las gasolineras disponibles desde UrlJson
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
                //adaptador = new AdaptadorDeAnimales(getBaseContext(), animales);
                //lista.setAdapter(adaptador);
            } else {
                Toast.makeText(
                        getBaseContext(),
                        "Ocurrió un error de Parsing Json",
                        Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    //Clase creada para obtener las ubicaciones disponibles desde UrlJson
    public class JsonUbicacionesTask extends AsyncTask<URL, Void, List<Ubicacion>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Descargando ubicaciones", Toast.LENGTH_LONG).show();

        }

        @Override
        protected List<Ubicacion> doInBackground(URL... urls) {


            try {

                // Establecer la conexión
                con = (HttpURLConnection) urls[0].openConnection();
                con.setConnectTimeout(15000);
                con.setReadTimeout(10000);

                // Obtener el estado del recurso
                int statusCode = con.getResponseCode();

                if (statusCode != 200) {
                    Ubicaciones = new ArrayList<>();
                    Ubicaciones.add(new Ubicacion(null, null, null, null, null));

                } else {
                    // Parsear el flujo con formato JSON
                    InputStream in = new BufferedInputStream(con.getInputStream());

                    JsonUbicacionParser parser = new JsonUbicacionParser();
                    //GsonAnimalParser parser = new GsonAnimalParser();

                    Ubicaciones = parser.leerFlujoJson(in);


                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                con.disconnect();
            }
            return Ubicaciones;
        }

        @Override
        protected void onPostExecute(List<Ubicacion> ubicaciones) {
            /*
            Asignar los objetos de Json parseados al adaptador
             */
            if (ubicaciones != null) {
                //adaptador = new AdaptadorDeAnimales(getBaseContext(), animales);
                //lista.setAdapter(adaptador);
                int i = 0;
                do {
                    String title = "Arre";
                    String snippet = "";
                    Double coordenadax = 0.0;
                    Double coordenaday = 0.0;
                    Bitmap icon = null;
                    int j = 0;
                    do {
                        if (Gasolineras.get(j).getIdGasolinera() == ubicaciones.get(i).getIdGasolinera()) {
                            snippet = Gasolineras.get(j).getIdGasolinera() + " " + Gasolineras.get(j).getNombre();
                            icon = Gasolineras.get(j).getImagenDescargada();
                            break;
                        }
                        j++;
                    } while (j < Gasolineras.size());
                    title =  ubicaciones.get(i).getNombre();
                    coordenadax = Double.parseDouble(ubicaciones.get(i).getCoordenadaX());
                    coordenaday = Double.parseDouble(ubicaciones.get(i).getCoordenadaY());

                    AgregarMapa(title, snippet, coordenadax, coordenaday, icon);
                    i++;
                } while (i < ubicaciones.size());


            } else {
                Toast.makeText(
                        getBaseContext(),
                        "Ocurrió un error de Parsing Json",
                        Toast.LENGTH_SHORT)
                        .show();
            }

        }

        public void AgregarMapa(String Title, String Snippet, Double CoordenadaX, Double CoordenadaY, Bitmap imagen) {
            LatLng coor = new LatLng(CoordenadaX, CoordenadaY);
            mMap.addMarker(new MarkerOptions()
                    .title(Title)
                    .snippet(Snippet)
                    .position(coor)
                    .icon(BitmapDescriptorFactory.fromBitmap(imagen)));
        }


    }


    /*  Metodos GPS */
    //Iniciamos el seguimiento del gps
    public void GPSStart() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this, Looper.getMainLooper()); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Creando una variable con las coordenadas de la UAS
        LatLng uas = new LatLng(25.788931, -108.996306);
        // Cambio la camara hacia la localización del punto en el mapa map.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uas, 12));
        // Agrego una marca
        // al momento de pulsar sobre ella, me muestra más información
        Resources res = getResources();

        /*mMap.addMarker(new MarkerOptions()
                .title("Universidad Autónoma de Sinaloa ")
                .snippet("Facultad de Ingenieria Mochis (Ing. Software)")
                .position(uas)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        */
        setUpMapIfNeeded();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this, InfoGasolinera.class);
                intent.putExtra("Title", marker.getTitle());
                intent.putExtra("Snippet", marker.getSnippet());
                intent.putExtra("Latitud", marker.getPosition().latitude);
                intent.putExtra("Longitud", marker.getPosition().longitude);
                startActivity(intent);

            }
        });
    }

    private void setUpMapIfNeeded() {

        // Chequeamos si se ha obtenido correctamente una referencia al objeto GoogleMap
        if (mMap != null) {
            // El objeto GoogleMap ha sido referenciado correctamente
            //ahora podemos manipular sus propiedades

            //Seteamos el tipo de mapa
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //Activamos la capa o layer MyLocation
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

    }
    //Se ejecuta al actualizar la ubicacion
    @Override
    public void onLocationChanged(Location location) {
        locationG = location;
        LatLng latLng = new LatLng(locationG.getLatitude(), locationG.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, Zoom);
        mMap.animateCamera(cameraUpdate);
        //locationManager.removeUpdates(this);
        //Toast.makeText(this, "Latitud:" +locationG.getLatitude() + locationG.getLatitude() +" Longitud" , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    /*  Terminan procedimientos de Mpas */

}
