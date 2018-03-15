package com.example.facop.citygas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by facop on 29/12/2017.
 */

public class JsonGasolineraParser {
    public List<Gasolinera> leerFlujoJson(InputStream in) throws IOException {
        // Nueva instancia JsonReader
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            // Leer Array
            return leerArrayGasolineras(reader);
        } finally {
            reader.close();
        }

    }
    public List<Gasolinera> leerArrayGasolineras(JsonReader reader) throws IOException {
        // Lista temporal
        ArrayList<Gasolinera> gasolineras = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            // Leer objeto
            gasolineras.add(leerGasolinera(reader));
        }
        reader.endArray();
        return gasolineras;
    }
    public Gasolinera leerGasolinera(JsonReader reader) throws IOException {
        // Variables locales
        Integer IdGasolinera = null;
        String Nombre = null;
        String UrlLogo = null;
        Bitmap ImagenDescargada =null;

        // Iniciar objeto
        reader.beginObject();

        /*
        Lectura de cada atributo
         */
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "IdGasolinera"://Como viene en el JSON
                    IdGasolinera = Integer.parseInt(reader.nextString());
                    break;
                case "Nombre":
                    Nombre = reader.nextString();
                    break;
                case "UrlLogo":
                    UrlLogo = reader.nextString();
                    ImagenDescargada = ObtenerImagen(new URL(UrlLogo));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        /*
        #   Creo que aqui iria el asignarle una imagen al bojeto
        #
        */
        reader.endObject();
        return new Gasolinera(IdGasolinera, Nombre, UrlLogo,ImagenDescargada);
    }

    /*
#   Creo que aqui iria el asignarle una imagen al bojeto
#
*/


    Bitmap ObtenerImagen(URL imageUrl){
        HttpURLConnection conn = null;
        Bitmap imagen=null;
        try {
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
             imagen = BitmapFactory.decodeStream(conn.getInputStream());

        } catch (IOException e) {

            e.printStackTrace();

        }
        return imagen;
    }
}
