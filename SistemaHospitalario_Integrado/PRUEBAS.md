# Pruebas realizadas

Las pruebas fueron ejecutadas mediante `javac` y `java` con OpenJDK 21,
compilando las fuentes con `--release 8`.

- Compilación de las 14 clases fuente Java: **correcta** (sin errores; avisos de compatibilidad JDK 8 por usar compilador reciente).
- Inicio de sesión con `admin` y `medico1`: **correcto**.
- Credenciales incorrectas y usuario desconocido: **rechazados**.
- Registro de consulta general: crea `PacienteConsulta`; nombre, teléfono y motivo coinciden con los datos introducidos: **correcto**.
- Registro de urgencia: crea `PacienteEmergencia`; se conserva el nivel elegido y se calcula la prioridad: **correcto**.
- DPI duplicado, DPI con longitud incorrecta, teléfono inválido, edad fuera de rango y área incompatible con el tipo: **rechazados sin crear ingresos**.
- Flujo de interfaz Swing ejecutado en pantalla virtual: login, formulario, guardado, actualización de tabla, cierre de sesión, segundo login y recarga de tabla: **correcto**.

**Alcance:** pruebas locales de un proyecto académico. No se probó dentro de la interfaz de NetBeans, ni contra una base de datos, ni se realizó una auditoría de seguridad. No hay persistencia entre ejecuciones.
