package com.hospital.modelo;


public class PacienteConsulta extends Paciente{
    private String sintomaPrincipal;
    
public PacienteConsulta(String dpi, String nombreCompleto, String telefono, String tipoSangre, String alergias, String sintomaPrincipal){
    super(dpi, telefono, nombreCompleto, alergias, tipoSangre);
    this.sintomaPrincipal = sintomaPrincipal; 
}   
@Override
public String generaProtocoloAtencion(){
    return "ESTABLE: El paciente esta en sala de espera. El motivo de su consulta es: "+this.sintomaPrincipal;
}

    public String getSintomaPrincipal() {
        return sintomaPrincipal;
    }

    public void setSintomaPrincipal(String sintomaPrincipal) {
        this.sintomaPrincipal = sintomaPrincipal;
    }

}
