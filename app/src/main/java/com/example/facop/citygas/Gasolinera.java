package com.example.facop.citygas;

import android.graphics.Bitmap;

/**
 * Created by facop on 29/12/2017.
 */

public class Gasolinera {
    private Integer IdGasolinera;
    private String Nombre;
    private String UrlLogo;
    private Bitmap ImagenDescargada;//Variable que contendra el icono ya descargado


    public Gasolinera(Integer idGasolinera, String nombre, String urlLogo, Bitmap imagenDescargada) {
        this.IdGasolinera = idGasolinera;
        this.Nombre = nombre;
        this.UrlLogo = urlLogo;
        this.ImagenDescargada = imagenDescargada;
    }

    public Integer getIdGasolinera() {
        return IdGasolinera;
    }

    public void setIdGasolinera(Integer idGasolinera) {
        this.IdGasolinera = idGasolinera;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getUrlLogo() {
        return UrlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.UrlLogo = urlLogo;
    }

    public Bitmap getImagenDescargada() {
        return ImagenDescargada;
    }

    public void setImagen(Bitmap imgDescargada) {
        this.ImagenDescargada = imgDescargada;
    }

}