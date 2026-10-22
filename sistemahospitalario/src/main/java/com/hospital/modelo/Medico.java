package com.hospital.modelo;


public class Medico extends Empleado{
    private String especialidad; 
    private String numColegiado; 
    
    public Medico(String dpi, String nombreCompleto, String telefono, String codigoEmpleado, String turno, String especialidad, String numeroColegiado) {
        
        super(dpi, nombreCompleto, telefono, codigoEmpleado, turno); 
        this.especialidad = especialidad;
        this.numColegiado = numeroColegiado;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNumColegiado() {
        return numColegiado;
    }

    public void setNumColegiado(String numColegiado) {
        this.numColegiado = numColegiado;
    }
}
