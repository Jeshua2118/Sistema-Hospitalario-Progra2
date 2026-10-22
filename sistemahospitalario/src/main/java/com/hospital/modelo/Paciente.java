
package com.hospital.modelo;

/**
 *
 * @author DELL
 */
public class Paciente extends Persona{
    private String tipoSangre; 
    private String alergias; 
    
public Paciente (String dpi, String nombreCompleto, String telefono, String tipoSangre, String alergias){
    super(dpi, nombreCompleto, telefono);
    this.alergias = alergias; 
    this.tipoSangre = tipoSangre;  
}
    public String generaProtocoloAtencion(){
        return "Paciente registrado. Pendiente evaluación médica";
    }
    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }
    
}
