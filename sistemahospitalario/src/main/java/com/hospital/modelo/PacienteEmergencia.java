package com.hospital.modelo;

public class PacienteEmergencia extends Paciente{
    private String nivelUrgencia;
    private boolean requiereOxigeno;
    
public PacienteEmergencia(String dpi, String nombreCompleto, String telefono, String tipoSangre, String alergias, String nivelUrgencia, boolean requiereOxigeno){
    super(dpi, nombreCompleto, telefono, tipoSangre, alergias);
    this.nivelUrgencia = nivelUrgencia; 
    this.requiereOxigeno = requiereOxigeno; 
}

    public String getNivelUrgencia() {
        return nivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }

    public boolean isRequiereOxigeno() {
        return requiereOxigeno;
    }

    public void setRequiereOxigeno(boolean requiereOxigeno) {
        this.requiereOxigeno = requiereOxigeno;
    }
@Override
public String generaProtocoloAtencion(){
    String estadoOxigeno = requiereOxigeno ? "Con tanque de oxigeno" : "Sin tanque de oxigeno";
    return "ALERTA ROJA: Trasladar a shock/trauma. Urgencia: "+ this.nivelUrgencia+" "+estadoOxigeno;
}


}//clase PacienteEmegencia
