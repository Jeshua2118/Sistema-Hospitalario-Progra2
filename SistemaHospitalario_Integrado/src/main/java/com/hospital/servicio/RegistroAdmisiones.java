package com.hospital.servicio;

import com.hospital.modelo.Paciente;
import com.hospital.modelo.PacienteConsulta;
import com.hospital.modelo.PacienteEmergencia;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Coordina validación, creación de objetos Paciente y registro de ingresos. */
public final class RegistroAdmisiones {
    private static final RegistroAdmisiones INSTANCIA = new RegistroAdmisiones();
    private final List<Ingreso> ingresos = new ArrayList<Ingreso>();
    private int siguienteId = 1;

    private static final String[] AREAS = {
        "Medicina General", "Pediatría", "Ginecología", "Traumatología", "Consulta Externa"
    };
    private static final String[] NIVELES = {
        "Rojo - Crítico", "Naranja - Emergencia", "Amarillo - Urgente", "Verde - Menos urgente"
    };

    private RegistroAdmisiones() {}
    public static RegistroAdmisiones getInstancia() { return INSTANCIA; }

    public synchronized Ingreso registrar(Sesion sesion, String nombres, String apellidos,
            String dpi, int edad, String telefono, String motivo, boolean esUrgencia,
            String detalle) {
        if (sesion == null) throw new IllegalArgumentException("Inicie sesión antes de registrar pacientes.");
        if (nombres == null || !nombres.trim().matches("[\\p{L} ]{2,50}"))
            throw new IllegalArgumentException("Los nombres solo deben tener letras (2 a 50 caracteres).");
        if (apellidos == null || !apellidos.trim().matches("[\\p{L} ]{2,50}"))
            throw new IllegalArgumentException("Los apellidos solo deben tener letras (2 a 50 caracteres).");
        if (dpi == null || !dpi.matches("\\d{13}"))
            throw new IllegalArgumentException("El DPI debe tener exactamente 13 dígitos.");
        if (edad < 0 || edad > 120)
            throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años.");
        if (telefono == null || !telefono.matches("\\d{8}"))
            throw new IllegalArgumentException("El teléfono debe tener 8 dígitos.");
        if (motivo == null || motivo.trim().length() < 3)
            throw new IllegalArgumentException("Describa el motivo de consulta (mínimo 3 caracteres).");
        String[] opciones = esUrgencia ? NIVELES : AREAS;
        int opcion = -1;
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(detalle)) { opcion = i; break; }
        }
        if (opcion < 0) throw new IllegalArgumentException("Seleccione un área o nivel de urgencia válido.");
        for (Ingreso existente : ingresos) {
            if (existente.getPaciente().getDpi().equals(dpi))
                throw new IllegalArgumentException("Este DPI ya está registrado. No se pueden duplicar pacientes en esta sesión.");
        }

        String nombreCompleto = nombres.trim() + " " + apellidos.trim();
        // La interfaz no solicita tipo de sangre, alergias ni oxígeno:
        // no se inventan datos médicos; se marca información pendiente.
        final String datoNoDisponible = "No indicado";
        Paciente paciente;
        if (esUrgencia) {
            paciente = new PacienteEmergencia(dpi, nombreCompleto, telefono,
                    datoNoDisponible, datoNoDisponible, detalle, false);
        } else {
            paciente = new PacienteConsulta(dpi, nombreCompleto, telefono,
                    datoNoDisponible, datoNoDisponible, motivo.trim());
        }
        Ingreso nuevo = new Ingreso(siguienteId++, paciente, edad, motivo.trim(),
                detalle, esUrgencia ? "Urgencia" : "General", esUrgencia ? opcion + 1 : 5,
                sesion.getUsuario().getNombreUsuario(), LocalDateTime.now());
        ingresos.add(nuevo);
        return nuevo;
    }

    public synchronized List<Ingreso> listar() {
        return Collections.unmodifiableList(new ArrayList<Ingreso>(ingresos));
    }
}
