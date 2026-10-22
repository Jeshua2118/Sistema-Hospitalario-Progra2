package com.hospital.modelo;

public class Usuario {
    private String nombreUsuario; 
    private String contraseña;
    private String rol;
    
public Usuario(String nombreUsuario, String contraseña, String rol){
    this.contraseña = contraseña; 
    this.nombreUsuario = nombreUsuario; 
    this.rol = rol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
