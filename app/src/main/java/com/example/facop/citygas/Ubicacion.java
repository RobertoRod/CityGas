package com.example.facop.citygas;

/**
 * Created by facop on 29/12/2017.
 */

public class Ubicacion {
    private Integer IdUbicacion;
    private Integer IdGasolinera;
    private String Nombre;
    private String CoordenadaX;
    private String CoordenadaY;//Variable que contendra el icono ya descargado


    public Ubicacion(Integer idUbicacion, Integer idGasolinera,String Nombre, String coordenadaX, String CoordenadaY) {
        this.IdUbicacion = idUbicacion;
        this.IdGasolinera = idGasolinera;
        this.CoordenadaX = coordenadaX;
        this.CoordenadaY = CoordenadaY;
        this.Nombre =Nombre;
    }


    public Integer IdUbicacion() {
        return IdUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion) {
        this.IdUbicacion = idUbicacion;
    }
    public Integer getIdGasolinera() {return IdGasolinera;}

    public void setIdGasolinera(Integer idGasolinera) {
        this.IdGasolinera = idGasolinera;
    }

    public String getCoordenadaX() {
        return CoordenadaX;
    }

    public void setCoordenadaX(String coordenadaX) {
        this.CoordenadaX = coordenadaX;
    }

    public String getCoordenadaY() {
        return CoordenadaY;
    }

    public void setCoordenadaY(String coordenadaY) {
        this.CoordenadaY = coordenadaY;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

}
