package com.hospital.servicio;

import com.hospital.modelo.Empleado;
import com.hospital.modelo.Medico;
import com.hospital.modelo.Usuario;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registro de usuarios demostrativo basado en las clases Usuario, Empleado y Medico.
 * Reemplazar por almacenamiento seguro antes de usar con datos reales.
 */
public final class ServicioAutenticacion {
    private static final Map<String, Sesion> CUENTAS = new LinkedHashMap<String, Sesion>();
    static {
        // Cuentas de ejemplo para probar el flujo heredado del login original.
        agregar(new Usuario("admin", "admin123", "ADMINISTRADOR"),
                new Empleado("0000000000000", "Administrador del sistema", "00000000", "EMP-001", "Diurno"));
        agregar(new Usuario("medico1", "medico123", "MEDICO"),
                new Medico("1111111111111", "Medico de demostración", "00000000",
                        "MED-001", "Diurno", "Medicina General", "DEMO-001"));
    }

    private ServicioAutenticacion() {}

    private static void agregar(Usuario usuario, Empleado empleado) {
        CUENTAS.put(usuario.getNombreUsuario(), new Sesion(usuario, empleado));
    }

    public static Sesion iniciarSesion(String nombreUsuario, char[] clave) {
        if (nombreUsuario == null || clave == null) return null;
        Sesion sesion = CUENTAS.get(nombreUsuario.trim());
        if (sesion == null) return null;
        char[] esperada = sesion.getUsuario().getContraseña().toCharArray();
        try {
            // Las credenciales originales están en texto plano; solo son para una demostración.
            return Arrays.equals(esperada, clave) ? sesion : null;
        } finally {
            Arrays.fill(esperada, '\0');
        }
    }
}
