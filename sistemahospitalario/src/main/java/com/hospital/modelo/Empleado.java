package com.hospital.modelo;

public class Empleado  extends Persona{
    String codigoEmpleado; 
    String turno;
    
public Empleado(String dpi, String nombreCompleto, String telefono, String codigoEmpleado, String turno){
    super(dpi, nombreCompleto, telefono);
    this.codigoEmpleado = codigoEmpleado;
    this.turno = turno; 
    }    

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
    
}
