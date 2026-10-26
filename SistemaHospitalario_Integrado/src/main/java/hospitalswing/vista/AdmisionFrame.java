package hospitalswing.vista;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import com.hospital.servicio.Ingreso;
import com.hospital.servicio.RegistroAdmisiones;
import com.hospital.servicio.Sesion;
import hospitalswing.datos.Conexion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import hospitalswing.datos.GeneradorTicketPDF;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Statement;


public class AdmisionFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdmisionFrame.class.getName());

        private static final String[] AREAS = {
        "Medicina General", "Pediatría", "Ginecología", "Traumatología", "Consulta Externa"};
    private static final String[] NIVELES = {
        "Rojo - Crítico", "Naranja - Emergencia", "Amarillo - Urgente", "Verde - Menos urgente"};
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DefaultTableModel modelo;
    private final Sesion sesion;
    private final RegistroAdmisiones registro = RegistroAdmisiones.getInstancia();

    public AdmisionFrame(Sesion sesion) {
        if (sesion == null) throw new IllegalArgumentException("Se requiere una sesión activa.");
        this.sesion = sesion;
        initComponents();
        lblUsuario.setText("Usuario: " + sesion.getDescripcion() + "   ");
        configurarTabla();
        cargarIngresos();
        actualizarDetalle();
        setSize(1200, 650);
        setLocationRelativeTo(null);
    }

    // ---------- JTable ----------
    private void configurarTabla() {
        modelo = new DefaultTableModel(new String[]{"ID", "Tipo", "Paciente", "DPI", "Edad",
                "Teléfono", "Área / Nivel", "Prioridad", "Ingreso"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 0 || c == 4 || c == 7) ? Integer.class : String.class;
            }
        };
        tablaPacientes.setModel(modelo);
        tablaPacientes.setRowHeight(30);
        tablaPacientes.setAutoCreateRowSorter(true);       // clic en el encabezado para ordenar

        // Columna Prioridad con colores de semáforo
        tablaPacientes.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            { setHorizontalAlignment(CENTER); }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                if (!sel) {
                    switch (String.valueOf(v)) {
                        case "1": comp.setForeground(new Color(0xC62828)); break;
                        case "2": comp.setForeground(new Color(0xEF6C00)); break;
                        case "3": comp.setForeground(new Color(0xB78103)); break;
                        case "4": comp.setForeground(new Color(0x2E7D32)); break;
                        default:  comp.setForeground(Color.GRAY);
                    }
                }
                return comp;
            }
        });
    }

    // Carga todos los ingresos registrados mientras la aplicación sigue abierta.
    private void cargarIngresos() {
        modelo.setRowCount(0);
        for (Ingreso ingreso : registro.listar()) {
            agregarFila(ingreso);
        }
    }

    private void agregarFila(Ingreso ingreso) {
        modelo.addRow(new Object[]{
            ingreso.getId(), ingreso.getTipo(), ingreso.getPaciente().getNombreCompleto(),
            ingreso.getPaciente().getDpi(), ingreso.getEdad(), ingreso.getPaciente().getTelefono(),
            ingreso.getDetalle(), ingreso.getPrioridad(), ingreso.getFecha().format(FORMATO)
        });
    }

    // ---------- JComboBox dependiente ----------
    private void actualizarDetalle() {
        boolean urgencia = cmbTipo.getSelectedIndex() == 1;
        lblDetalle.setText(urgencia ? "Nivel de urgencia (triage)" : "Área");
        cmbDetalle.setModel(new DefaultComboBoxModel<String>(urgencia ? NIVELES : AREAS));
    }

    // ---------- Guardar con protección estricta (try-catch) ----------
    private void guardar() {
        try {
            String nombre   = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dpi      = txtDpi.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String motivo   = txtMotivo.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());   // puede lanzar NumberFormatException

            if (!nombre.matches("[\\p{L} ]{2,50}"))
                throw new IllegalArgumentException("El nombre solo debe tener letras (2 a 50 caracteres).");
            if (!apellido.matches("[\\p{L} ]{2,50}"))
                throw new IllegalArgumentException("El apellido solo debe tener letras (2 a 50 caracteres).");
            if (!dpi.matches("\\d{13}"))
                throw new IllegalArgumentException("El DPI debe tener exactamente 13 dígitos.");
            if (edad < 0 || edad > 120)
                throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años.");
            if (!telefono.matches("\\d{8}"))
                throw new IllegalArgumentException("El teléfono debe tener 8 dígitos.");
            if (motivo.length() < 3)
                throw new IllegalArgumentException("Describa el motivo de consulta.");

            boolean urgencia = cmbTipo.getSelectedIndex() == 1;
            String detalle = (String) cmbDetalle.getSelectedItem();
            int prioridad = urgencia ? cmbDetalle.getSelectedIndex() + 1 : 5;   // Rojo=1 ... Verde=4, General=5

            // Crear PacienteConsulta o PacienteEmergencia en el backend, antes de tocar la tabla.
            Ingreso ingreso = registro.registrar(sesion, nombre, apellido,
                    dpi, edad, telefono, motivo, urgencia, detalle);
            agregarFila(ingreso);
            JOptionPane.showMessageDialog(this,
                    "Ingreso #" + ingreso.getId() + " registrado: "
                            + ingreso.getPaciente().getNombreCompleto() + "\n"
                            + ingreso.getPaciente().generaProtocoloAtencion(),
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiar();

        } catch (NumberFormatException ex) {
            error("La edad debe ser un número entero (solo dígitos).");
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (Exception ex) {
            error("Error inesperado: " + ex.getMessage());
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtApellido.setText(""); txtDpi.setText("");
        txtEdad.setText(""); txtTelefono.setText(""); txtMotivo.setText("");
        cmbTipo.setSelectedIndex(0);
        txtNombre.requestFocus();
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Dato inválido", JOptionPane.WARNING_MESSAGE);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pnlHeader = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        pnlFormulario = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        lblDpi = new javax.swing.JLabel();
        txtDpi = new javax.swing.JTextField();
        lblEdad = new javax.swing.JLabel();
        txtEdad = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblMotivo = new javax.swing.JLabel();
        txtMotivo = new javax.swing.JTextField();
        lblTipo = new javax.swing.JLabel();
        cmbTipo = new javax.swing.JComboBox<>();
        lblDetalle = new javax.swing.JLabel();
        cmbDetalle = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnCargar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaPacientes = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hospital - Admisión de pacientes");

        pnlHeader.setBackground(new java.awt.Color(11, 93, 122));
        pnlHeader.setPreferredSize(new java.awt.Dimension(100, 56));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText(" Admisión de pacientes");
        pnlHeader.add(lblTitulo, java.awt.BorderLayout.LINE_START);

        lblUsuario.setForeground(new java.awt.Color(255, 255, 255));
        lblUsuario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pnlHeader.add(lblUsuario, java.awt.BorderLayout.CENTER);

        btnCerrarSesion.setText("Cerrar sesión");
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);
        pnlHeader.add(btnCerrarSesion, java.awt.BorderLayout.LINE_END);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.PAGE_START);

        pnlFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 1, 1, 1));
        pnlFormulario.setPreferredSize(new java.awt.Dimension(340, 620));
        pnlFormulario.setLayout(new java.awt.GridLayout(18, 1, 0, 2));

        lblNombre.setText("Nombres");
        pnlFormulario.add(lblNombre);

        txtNombre.addActionListener(this::txtNombreActionPerformed);
        pnlFormulario.add(txtNombre);

        lblApellido.setText("Apellidos");
        pnlFormulario.add(lblApellido);
        pnlFormulario.add(txtApellido);

        lblDpi.setText("DPI (13 dígitos)");
        pnlFormulario.add(lblDpi);
        pnlFormulario.add(txtDpi);

        lblEdad.setText("Edad");
        pnlFormulario.add(lblEdad);
        pnlFormulario.add(txtEdad);

        lblTelefono.setText("Teléfono (8 dígitos)");
        pnlFormulario.add(lblTelefono);
        pnlFormulario.add(txtTelefono);

        lblMotivo.setText("Motivo de consulta");
        pnlFormulario.add(lblMotivo);
        pnlFormulario.add(txtMotivo);

        lblTipo.setText("Tipo de atención");
        pnlFormulario.add(lblTipo);

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Consulta general", "Urgencia" }));
        cmbTipo.addActionListener(this::cmbTipoActionPerformed);
        pnlFormulario.add(cmbTipo);

        lblDetalle.setText("Detalle");
        pnlFormulario.add(lblDetalle);

        cmbDetalle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pnlFormulario.add(cmbDetalle);

        btnGuardar.setBackground(new java.awt.Color(11, 93, 122));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar Ingreso");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        pnlFormulario.add(btnGuardar);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);
        pnlFormulario.add(btnLimpiar);

        getContentPane().add(pnlFormulario, java.awt.BorderLayout.LINE_START);

        btnCargar.setText("Cargar");
        btnCargar.addActionListener(this::btnCargarActionPerformed);
        getContentPane().add(btnCargar, java.awt.BorderLayout.LINE_END);

        tablaPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7"
            }
        ));
        jScrollPane2.setViewportView(tablaPacientes);

        getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
String nombre = txtNombre.getText().trim();
    String apellido = txtApellido.getText().trim();
    String dpi = txtDpi.getText().trim();
    String edadTexto = txtEdad.getText().trim();
    String telefono = txtTelefono.getText().trim();
    String motivo = txtMotivo.getText().trim();

    String urgencia =
        cmbTipo.getSelectedItem().toString();

    // 1. VALIDACIONES
    if (nombre.isEmpty() || apellido.isEmpty()
            || motivo.isEmpty()) {

        JOptionPane.showMessageDialog(this,
            "Complete todos los campos.");
        return;
    }

    if (!dpi.matches("\\d{13}")) {
        JOptionPane.showMessageDialog(this,
            "El DPI debe contener 13 digitos.");
        return;
    }

    if (!telefono.matches("\\d{8}")) {
        JOptionPane.showMessageDialog(this,
            "El telefono debe contener 8 digitos.");
        return;
    }

    int edad;

    try {
        edad = Integer.parseInt(edadTexto);

        if (edad < 0 || edad > 120) {
            JOptionPane.showMessageDialog(this,
                "La edad debe estar entre 0 y 120.");
            return;
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "La edad debe ser numerica.");
        return;
    }

    // 2. REGISTRAR AL PACIENTE
    String sql = "INSERT INTO pacientes "
        + "(nombre, edad, apellido, dpi, telefono, "
        + "motivo, urgencia) VALUES (?, ?, ?, ?, ?, ?, ?)";

    int idPaciente = -1;

    try (Connection conexion =
            new Conexion().conectar()) {

        if (conexion == null) {
            throw new SQLException(
                "No se pudo conectar a MySQL.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
                )) {

            ps.setString(1, nombre);
            ps.setInt(2, edad);
            ps.setString(3, apellido);
            ps.setString(4, dpi);
            ps.setString(5, telefono);
            ps.setString(6, motivo);
            ps.setString(7, urgencia);

            // Guardar en la base de datos
            int registros = ps.executeUpdate();

            if (registros != 1) {
                throw new SQLException(
                    "No se pudo guardar el paciente.");
            }

            // 3. OBTENER EL ID GENERADO
            try (ResultSet claves =
                    ps.getGeneratedKeys()) {

                if (claves.next()) {
                    idPaciente = claves.getInt(1);
                }
            }
        }

    } catch (SQLException e) {

        if (e.getErrorCode() == 1062) {
            JOptionPane.showMessageDialog(this,
                "Ya existe un registro duplicado.\n"
                + "Verifique el DPI del paciente.");
        } else {
            JOptionPane.showMessageDialog(this,
                "Error de MySQL: " + e.getMessage());
        }

        return;
    }

    // A partir de este punto, MySQL ya guardo
    // el registro correctamente.

    if (idPaciente == -1) {

        JOptionPane.showMessageDialog(this,
            "Paciente guardado en MySQL, pero no "
            + "se pudo obtener su ID para el PDF.");

    } else {

        // 4. GENERAR EL TICKET PDF
        try {

            Path ticket =
                GeneradorTicketPDF.generarDesdeBD(
                    idPaciente
                );

            JOptionPane.showMessageDialog(this,
                "Paciente registrado correctamente.\n"
                + "Ticket PDF generado en:\n"
                + ticket.toAbsolutePath());

        } catch (SQLException | IOException
                | DocumentException e) {

            JOptionPane.showMessageDialog(this,
                "Paciente guardado en MySQL, "
                + "pero no se pudo crear el PDF:\n"
                + e.getMessage());
        }
    }

    // 5. LIMPIAR LOS CAMPOS
    limpiar();

    // 6. ACTUALIZAR LA TABLA
    btnCargarActionPerformed(null);


    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
       limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void cmbTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoActionPerformed
       actualizarDetalle();
    }//GEN-LAST:event_cmbTipoActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        dispose();
        new LoginFrame().setVisible(true);
        
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
Conexion puente = new Conexion();
Connection conexionViva = null;
        try {
            conexionViva = puente.conectar();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
if (conexionViva != null) {
try {
// 1. Preparamos el modelo visual de la tabla
DefaultTableModel modelo = new DefaultTableModel();
modelo.addColumn("ID");
modelo.addColumn("Nombre");
modelo.addColumn("Apellido");
modelo.addColumn("Dpi");
modelo.addColumn("Edad");
modelo.addColumn("Telefono");
modelo.addColumn("Motivo");
modelo.addColumn("Urgencia");
tablaPacientes.setModel(modelo);
// 2. Ejecutamos la consulta a la Base de Datos (SELECT)
String ordenSQL = "SELECT * FROM pacientes";
Statement declaracion = conexionViva.createStatement();
ResultSet resultados = declaracion.executeQuery(ordenSQL);
// 3. Extraemos fila por fila (Mientras haya pacientes, los agregamos)
while (resultados.next()) {
String[] fila = new String[8];
fila[0] = String.valueOf(resultados.getInt("id"));
fila[1] = resultados.getString("nombre");
fila[2] = String.valueOf(resultados.getString("apellido"));
fila[3] = resultados.getString("dpi");
fila[4] = resultados.getString("edad");
fila[5] = resultados.getString("telefono");
fila[6] = resultados.getString("motivo");
fila[7] = resultados.getString("urgencia");
modelo.addRow(fila); // Lo agregamos visualmente a la tabla
}
} catch (SQLException e) {
JOptionPane.showMessageDialog(this, "Error al consultar BD: " + e.getMessage());
}
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnCargarActionPerformed
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbDetalle;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblDetalle;
    private javax.swing.JLabel lblDpi;
    private javax.swing.JLabel lblEdad;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JTable tablaPacientes;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtDpi;
    private javax.swing.JTextField txtEdad;
    private javax.swing.JTextField txtMotivo;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
