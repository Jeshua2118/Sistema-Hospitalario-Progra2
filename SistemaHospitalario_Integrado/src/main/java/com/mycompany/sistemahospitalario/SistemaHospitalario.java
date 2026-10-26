package com.mycompany.sistemahospitalario;

import hospitalswing.vista.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada para ejecutar el proyecto desde NetBeans. */
public final class SistemaHospitalario {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
                catch (Exception ignorado) { /* apariencia predeterminada */ }
                new LoginFrame().setVisible(true);
            }
        });
    }
}
