import model.ConexionBD;
import view.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada principal para la aplicación de Gestión de Biblioteca.
 * Universidad Tecnológica de León (UTL)
 * Programación Orientada a Objetos - Examen Extraordinario Oportunidad 2
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look & Feel según el sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            // Probar conexión a MySQL al inicio
            if (!ConexionBD.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Advertencia: No se pudo conectar a la base de datos MySQL (" + ConexionBD.getDatabaseName() + ").\n" +
                        "Verifique que el servicio de MySQL esté corriendo y que los datos en 'config.properties' sean correctos.",
                        "Aviso de Conexión MySQL",
                        JOptionPane.WARNING_MESSAGE);
            }

            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
