package com.practica_1.seguridaddismov.practica_1;

/**
 * Created by Mario Martin on 06/02/2018.
 */

public class Usuario {
    private String nombre;
    private String genero;
    private String localizacion;
    private String imagenPerfil;
    private String fechaRegistro;
    private String usuario;
    private String contrasena;

    public Usuario (String nombre, String genero, String localizacion, String imagenPerfil, String fechaRegistro, String usuario, String contrasena){
        this.nombre = nombre;
        this.genero = genero;
        this.localizacion = localizacion;
        this.imagenPerfil = imagenPerfil;
        this.fechaRegistro = fechaRegistro;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
