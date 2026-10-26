
package hospitalswing.datos;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorTicketPDF {

    public static Path generarDesdeBD(int idPaciente)
            throws SQLException, IOException, DocumentException {

        String nombre;
        String apellido;
        String dpi;
        String urgencia;
        int edad;

        // 1. BUSCAR AL PACIENTE EN MYSQL
        String sql = "SELECT id, nombre, apellido, dpi, "
                   + "edad, urgencia FROM pacientes WHERE id = ?";

        Conexion puente = new Conexion();

        try (Connection conexion = puente.conectar()) {

            if (conexion == null) {
                throw new SQLException(
                    "No se pudo conectar a MySQL.");
            }

            try (PreparedStatement ps =
                    conexion.prepareStatement(sql)) {

                ps.setInt(1, idPaciente);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                            "Paciente no encontrado.");
                    }

                    nombre = rs.getString("nombre");
                    apellido = rs.getString("apellido");
                    dpi = rs.getString("dpi");
                    edad = rs.getInt("edad");
                    urgencia = rs.getString("urgencia");
                }
            }
        }

        // 2. CREAR LA CARPETA DE TICKETS
        Path carpeta = Paths.get(
            System.getProperty("user.home"),
            "TicketsHospital"
        );

        Files.createDirectories(carpeta);

        String fechaArchivo = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );

        Path archivo = carpeta.resolve(
            "Ticket_Paciente_" + idPaciente
            + "_" + fechaArchivo + ".pdf"
        );

        // 3. CREAR EL DOCUMENTO PDF
        Document documento = new Document(PageSize.A5);

        try (OutputStream salida =
                Files.newOutputStream(archivo)) {

            PdfWriter.getInstance(documento, salida);

            try {
                documento.open();

                Font titulo = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 16
                );

                Font normal = FontFactory.getFont(
                    FontFactory.HELVETICA, 11
                );

                Paragraph encabezado = new Paragraph(
                    "SISTEMA HOSPITALARIO", titulo
                );

                encabezado.setAlignment(
                    Element.ALIGN_CENTER
                );

                documento.add(encabezado);

                Paragraph subtitulo = new Paragraph(
                    "COMPROBANTE DE ADMISION\n\n",
                    normal
                );

                subtitulo.setAlignment(
                    Element.ALIGN_CENTER
                );

                documento.add(subtitulo);

                documento.add(new Paragraph(
                    "----------------------------------------"
                ));

                // 4. DATOS DEL PACIENTE
                documento.add(new Paragraph(
                    "No. de ingreso: " + idPaciente, normal
                ));

                documento.add(new Paragraph(
                    "Nombre: " + nombre + " " + apellido,
                    normal
                ));

                documento.add(new Paragraph(
                    "Edad: " + edad + " años", normal
                ));

                // Ocultar parte del DPI por privacidad
                String dpiOculto = "No disponible";

                if (dpi != null && dpi.length() >= 4) {
                    dpiOculto = "*********"
                        + dpi.substring(dpi.length() - 4);
                }

                documento.add(new Paragraph(
                    "DPI: " + dpiOculto, normal
                ));

                documento.add(new Paragraph(
                    "Tipo de atencion: " + urgencia, normal
                ));

                String fecha = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                    )
                );

                documento.add(new Paragraph(
                    "Fecha de emision: " + fecha, normal
                ));

                documento.add(new Paragraph(
                    "----------------------------------------"
                ));

                // 5. MENSAJE FINAL
                Paragraph mensaje = new Paragraph(
                    "\nRegistro hospitalario confirmado.\n"
                    + "Presente este comprobante en recepcion.",
                    normal
                );

                mensaje.setAlignment(
                    Element.ALIGN_CENTER
                );

                documento.add(mensaje);

            } finally {
                documento.close();
            }
        }

        // 6. DEVOLVER LA UBICACIÓN DEL PDF
        return archivo;
    }
}
