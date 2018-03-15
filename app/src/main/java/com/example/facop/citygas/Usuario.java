package com.example.facop.citygas;

/**
 * Created by facop on 01/01/2018.
 */

public class Usuario {
    private Integer IdUsuario;
    private Integer IdTipoUsuario;
    private String Nombre;
    private String Email;
    private String Contrasenia;

    public Integer getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(Integer IdUsuario) {
        this.IdUsuario = IdUsuario;
    }

    public Integer getIdTipoUsuario() {
        return IdTipoUsuario;
    }

    public void setIdTipoUsuario(Integer IdTipoUsuario) {
        this.IdTipoUsuario = IdTipoUsuario;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }
    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
    public String getContrasenia() {
        return Contrasenia;
    }

    public void setContrasenia(String Contrasenia) {
        this.Contrasenia = Contrasenia;
    }




}
