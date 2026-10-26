# Sistema Hospitalario integrado (Java Swing + backend POO)

Este proyecto reúne los dos formularios originales de NetBeans (`LoginFrame` y
`AdmisionFrame`, con sus archivos `.form`) y las siete clases del modelo:
`Persona`, `Empleado`, `Medico`, `Usuario`, `Paciente`, `PacienteConsulta` y
`PacienteEmergencia`.

## Abrir en NetBeans

1. Descomprimir el ZIP.
2. Abrir NetBeans y elegir **Archivo > Abrir proyecto**.
3. Seleccionar la carpeta **SistemaHospitalario_Integrado** que contiene `pom.xml`.
4. Tener instalado JDK 8 o posterior (recomendado JDK 17 o 21).
5. Ejecutar el proyecto; clase principal:
   `com.mycompany.sistemahospitalario.SistemaHospitalario`.

También se puede compilar desde terminal con `mvn compile` y ejecutar con
`mvn exec:java` (requiere Maven). En Windows puede usarse `ejecutar.bat` sin Maven
si `javac` y `java` están en el PATH.

## Cuentas de demostración

| Usuario | Clave | Tipo | Modelo asociado |
| --- | --- | --- | --- |
| `admin` | `admin123` | ADMINISTRADOR | `Empleado` |
| `medico1` | `medico123` | MEDICO | `Medico extends Empleado` |

Estas cuentas se crean por código con fines exclusivamente académicos: **no son
seguras para datos clínicos reales**. La clase `Usuario` conserva su diseño
original con contraseñas en texto plano. Para producción, reemplazar por
autenticación segura con hash y base de datos.

## Comportamiento

- `LoginFrame` usa `ServicioAutenticacion` para recuperar `Sesion`, `Usuario`
  y su `Empleado` o `Medico` correspondiente.
- `AdmisionFrame` llama a `RegistroAdmisiones.registrar()` y crea instancias de
  `PacienteConsulta` para Consulta general y `PacienteEmergencia` para Urgencia.
- Se usa `generaProtocoloAtencion()` (polimorfismo) en la confirmación del registro.
- Los datos de la tabla se derivan de los objetos del backend y se actualizan
  **solo después de un registro válido**.
- Se valida DPI de 13 dígitos, teléfono de 8, edad, nombre, apellido y motivo.
- No se permiten DPI repetidos durante la ejecución; los registros siguen
  disponibles en la misma ejecución aunque se cierre y reabra sesión.
- El modelo original no contempla edad, detalle de área y fecha; esos datos
  viven en la clase adicional `Ingreso` para no romper constructores existentes.
- `PacienteConsulta` tenía los argumentos invertidos al invocar `super`;
  se corrigió para almacenar nombre, teléfono, sangre y alergias correctamente.
- Los formularios no recogen tipo de sangre, alergias ni oxígeno: en el modelo
  se usa `No indicado` para las primeras dos y no se informa un estado de
  oxígeno en pantalla. **No se deducen esos valores.**

## Límites (importante)

El registro se guarda **solo en memoria**, no en una base de datos ni archivo.
Los pacientes registrados se pierden al cerrar completamente la aplicación.
Este es un proyecto académico, no apto para operación hospitalaria real ni
almacenamiento de información sensible en producción. El modelo no permite
representar la situación de oxígeno como "desconocido" (es booleano), por lo
que el campo se deja por defecto en `false`, sin inferir clínicamente su estado.

## Estructura

```
src/main/java/
  com/hospital/modelo/       # 7 clases originales; 2 correcciones puntuales
  com/hospital/servicio/     # Sesion, ServicioAutenticacion, Ingreso, RegistroAdmisiones
  com/mycompany/sistemahospitalario/SistemaHospitalario.java
  hospitalswing/vista/      # 2 JFrame + 2 archivos .form originales
```
