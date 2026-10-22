package com.hospital.modelo;

public class Persona {
    private String dpi;
    private String nombreCompleto;
    private String telefono; 
    
    public Persona(String dpi, String nombreCompleto, String telefono){
        this.dpi = dpi; 
        this.nombreCompleto = nombreCompleto; 
        this.telefono = telefono; 
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
