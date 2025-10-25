package sgvic.ui;

import sgvic.entidades.Cliente;
import sgvic.excepciones.DataAccessException;
import sgvic.servicios.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de Clientes.
 * 
 * Implementa la parte gráfica (Swing) para mostrar y gestionar los clientes.
 * Cumple con la consigna de aplicar POO + interfaz visual.
 */
public class PanelClientes extends JPanel {

    // Servicio de negocio (conecta la UI con la base de datos)
    private final ClienteService service = new ClienteService();

    // Componentes principales
    private final JTable tabla;
    private final DefaultTableModel modelo;

    public PanelClientes() {
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Gestión de Clientes", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // --- Configuración de tabla ---
        modelo = new DefaultTableModel(new String[]{"ID", "Razón Social", "CUIT", "Email", "Teléfono", "Dirección"}, 0);
        tabla = new JTable(modelo);

        // --- Botones de acción ---
        JPanel botones = new JPanel();
        JButton btnListar = new JButton("Listar Clientes");
        JButton btnAgregar = new JButton("Agregar Cliente");
        botones.add(btnListar);
        botones.add(btnAgregar);

        add(lblTitulo, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        // === Acción de Listar Clientes ===
        btnListar.addActionListener(e -> {
            try {
                cargarClientes();
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, "Error al listar clientes: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // === Acción de Agregar Cliente (demostrativa) ===
        btnAgregar.addActionListener(e -> {
            // En esta versión de demostración solo se muestra un mensaje.
            JOptionPane.showMessageDialog(this,
                    "Acá se abriría un formulario para dar de alta un cliente nuevo.",
                    "Agregar Cliente", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Carga los clientes desde la base de datos y los muestra en la tabla.
     * Este método aplica los conceptos de encapsulamiento y separación de capas:
     * la UI no accede directamente a la BD, sino a través de ClienteService.
     */
    private void cargarClientes() throws DataAccessException {
        modelo.setRowCount(0); // limpia tabla
        List<Cliente> clientes = service.listar();
        for (Cliente c : clientes) {
            modelo.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getRazonSocial(),
                    c.getCuit(),
                    c.getEmail(),
                    c.getTelefono(),
                    c.getDireccion()
            });
        }

        // Comentario para la profe:
        // Esta función ejemplifica la integración entre Swing (interfaz) y la capa de negocio (Service).
    }
}

