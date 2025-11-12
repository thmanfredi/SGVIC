package sgvic.ui;

import sgvic.entidades.Cliente;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.servicios.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de clientes.
 * Permite:
 *  - Listar clientes desde la BD en una JTable.
 *  - Dar de alta un nuevo cliente y guardarlo en MySQL.
 *
 * Esta funcionalidad es central para el sistema SGVIC.
 */
public class PanelClientes extends JPanel {

    // Servicio que encapsula la lógica de negocio
    private final ClienteService clienteService = new ClienteService();

    // Componentes de la UI
    private JTable tablaClientes;
    private JButton btnListar;
    private JButton btnAgregar;

    public PanelClientes() {
        initComponents();
    }

    /**
     * Inicializa los componentes visuales del panel.
     * No uso el diseñador, sino código directo para que sea fácil de leer.
     */
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // --- Panel superior con botones ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnListar = new JButton("Listar clientes");
        btnAgregar = new JButton("Agregar cliente");

        panelBotones.add(btnListar);
        panelBotones.add(btnAgregar);

        add(panelBotones, BorderLayout.NORTH);

        // --- Tabla de clientes en el centro ---
        tablaClientes = new JTable();
        tablaClientes.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "ID", "Razón Social", "CUIT", "Email", "Teléfono", "Dirección"
                }
        ) {
            // Evito que se pueda editar directamente una celda
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaClientes);
        add(scroll, BorderLayout.CENTER);

        // --- Asignar acciones a los botones ---
        btnListar.addActionListener(e -> cargarClientesEnTabla());
        btnAgregar.addActionListener(e -> agregarCliente());
    }

    /**
     * Carga la tabla con los clientes que vienen de la BD.
     * Usa el ClienteService y maneja las excepciones mostrando mensajes.
     */
    private void cargarClientesEnTabla() {
        try {
            List<Cliente> clientes = clienteService.listar();

            DefaultTableModel model = (DefaultTableModel) tablaClientes.getModel();
            model.setRowCount(0); // limpio todas las filas

            for (Cliente c : clientes) {
                model.addRow(new Object[]{
                        c.getIdCliente(),
                        c.getRazonSocial(),
                        c.getCuit(),
                        c.getEmail(),
                        c.getTelefono(),
                        c.getDireccion()
                });
            }

        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al listar clientes:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Muestra un diálogo para dar de alta un cliente nuevo
     * y lo guarda en la BD usando ClienteService.
     */
    private void agregarCliente() {
        // Campos de entrada simples
        JTextField txtRazon = new JTextField();
        JTextField txtCuit = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtTel = new JTextField();
        JTextField txtDir = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Razón social:"));
        panel.add(txtRazon);
        panel.add(new JLabel("CUIT:"));
        panel.add(txtCuit);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTel);
        panel.add(new JLabel("Dirección:"));
        panel.add(txtDir);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Nuevo cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Llamo al servicio con los datos cargados por el usuario
                clienteService.guardarNuevoCliente(
                        txtRazon.getText(),
                        txtCuit.getText(),
                        txtEmail.getText(),
                        txtTel.getText(),
                        txtDir.getText()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Cliente guardado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Vuelvo a listar para ver el nuevo cliente en la tabla
                cargarClientesEnTabla();

            } catch (DomainException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Datos inválidos:\n" + e.getMessage(),
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
            } catch (DataAccessException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al guardar el cliente:\n" + e.getMessage(),
                        "Error BD",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}


