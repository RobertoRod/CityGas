package com.example.facop.citygas;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by facop on 29/12/2017.
 */

public class JsonUbicacionParser {
    public List<Ubicacion> leerFlujoJson(InputStream in) throws IOException {
        // Nueva instancia JsonReader
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            // Leer Array
            return leerArrayUbicacions(reader);
        } finally {
            reader.close();
        }

    }
    public List<Ubicacion> leerArrayUbicacions(JsonReader reader) throws IOException {
        // Lista temporal
        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            // Leer objeto
            ubicaciones.add(leerUbicacion(reader));
        }
        reader.endArray();
        return ubicaciones;
    }
    public Ubicacion leerUbicacion(JsonReader reader) throws IOException {
        // Variables locales

        Integer IdUbicacion = null;
        Integer IdGasolinera = null;
        String Nombre = null;
        String CoordenadaX = null;
        String CoordenadaY = null;

        // Iniciar objeto
        reader.beginObject();

        /*
        Lectura de cada atributo
         */
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "IdUbicacion"://Como viene en el JSON
                    IdUbicacion = Integer.parseInt(reader.nextString());
                    break;
                case "IdGasolinera":
                    IdGasolinera = Integer.parseInt(reader.nextString());
                    break;
                case "CoordenadaX":
                    CoordenadaX = reader.nextString();
                    break;
                case "CoordenadaY":
                    CoordenadaY =reader.nextString();
                    break;
                case "Nombre":
                    Nombre =reader.nextString();
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
        return new Ubicacion(IdUbicacion, IdGasolinera, Nombre , CoordenadaX, CoordenadaY);
    }
}
