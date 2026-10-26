package com.hospital.servicio;

import com.hospital.modelo.Empleado;
import com.hospital.modelo.Usuario;

/** Identidad de un usuario que inició sesión. */
public final class Sesion {
    private final Usuario usuario;
    private final Empleado empleado;

    Sesion(Usuario usuario, Empleado empleado) {
        this.usuario = usuario;
        this.empleado = empleado;
    }

    public Usuario getUsuario() { return usuario; }
    public Empleado getEmpleado() { return empleado; }

    public String getDescripcion() {
        return usuario.getNombreUsuario() + " (" + usuario.getRol() + ") - "
                + empleado.getNombreCompleto();
    }
}
