package com.hospital.servicio;

import com.hospital.modelo.Paciente;
import java.time.LocalDateTime;

/** Datos de una admisión y el objeto Paciente del backend asociado. */
public final class Ingreso {
    private final int id;
    private final Paciente paciente;
    private final int edad;
    private final String motivo;
    private final String detalle;
    private final String tipo;
    private final int prioridad;
    private final String registradoPor;
    private final LocalDateTime fecha;

    Ingreso(int id, Paciente paciente, int edad, String motivo, String detalle,
            String tipo, int prioridad, String registradoPor, LocalDateTime fecha) {
        this.id = id;
        this.paciente = paciente;
        this.edad = edad;
        this.motivo = motivo;
        this.detalle = detalle;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.registradoPor = registradoPor;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public int getEdad() { return edad; }
    public String getMotivo() { return motivo; }
    public String getDetalle() { return detalle; }
    public String getTipo() { return tipo; }
    public int getPrioridad() { return prioridad; }
    public String getRegistradoPor() { return registradoPor; }
    public LocalDateTime getFecha() { return fecha; }
}
