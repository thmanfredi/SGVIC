package sgvic.ui;

import sgvic.entidades.Pago;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.excepciones.NotFoundException;
import sgvic.servicios.PagoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel de gestión de pagos.
 *
 * Permite:
 *  - Registrar un pago para una obligación (ID obligación, fecha, medio, monto).
 *  - Listar los pagos de una obligación dada.
 */
public class PanelPagos extends JPanel {

    private final PagoService pagoService = new PagoService();

    private JTextField txtIdObligacion;
    private JTextField txtFecha;
    private JTextField txtMonto;
    private JComboBox<String> cmbMedio;
    private JTable tablaPagos;
    private JButton btnRegistrar;
    private JButton btnVerPagos;

    // Arreglo de medios de pago (cumple con el uso de arreglos + ArrayList)
    private static final String[] MEDIOS_PAGO = {
            "VEP - Volante electronico de pago",
            "Plan de Pago",
            "Tarjeta",
            "Efectivo"
    };

    private final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelPagos() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // --- Panel superior con formulario ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblIdObl = new JLabel("ID Obligación:");
        JLabel lblFecha = new JLabel("Fecha pago (dd/MM/aaaa):");
        JLabel lblMedio = new JLabel("Medio de pago:");
        JLabel lblMonto = new JLabel("Monto:");

        txtIdObligacion = new JTextField(8);
        txtFecha = new JTextField(10);
        txtMonto = new JTextField(10);
        cmbMedio = new JComboBox<>(MEDIOS_PAGO);

        btnRegistrar = new JButton("Registrar pago");
        btnVerPagos = new JButton("Ver pagos de la obligación");

        int fila = 0;

        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(lblIdObl, gbc);
        gbc.gridx = 1;
        panelForm.add(txtIdObligacion, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(lblFecha, gbc);
        gbc.gridx = 1;
        panelForm.add(txtFecha, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(lblMedio, gbc);
        gbc.gridx = 1;
        panelForm.add(cmbMedio, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(lblMonto, gbc);
        gbc.gridx = 1;
        panelForm.add(txtMonto, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnVerPagos);
        panelForm.add(panelBotones, gbc);

        add(panelForm, BorderLayout.NORTH);

        // --- Tabla de pagos ---
        tablaPagos = new JTable();
        tablaPagos.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "ID Pago", "ID Obligación", "Cliente", "Período", "Fecha", "Medio", "Monto"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        configurarAnchosColumnasPagos();

        JScrollPane scroll = new JScrollPane(tablaPagos);
        add(scroll, BorderLayout.CENTER);

        // --- Acciones de los botones ---
        btnRegistrar.addActionListener(e -> registrarPago());
        btnVerPagos.addActionListener(e -> listarPagosDeObligacion());
    }

    private void configurarAnchosColumnasPagos() {
        tablaPagos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // ID Pago
        tablaPagos.getColumnModel().getColumn(0).setPreferredWidth(70);
        // ID Obligación
        tablaPagos.getColumnModel().getColumn(1).setPreferredWidth(90);
        // Cliente
        tablaPagos.getColumnModel().getColumn(2).setPreferredWidth(220);
        // Período
        tablaPagos.getColumnModel().getColumn(3).setPreferredWidth(90);
        // Fecha
        tablaPagos.getColumnModel().getColumn(4).setPreferredWidth(100);
        // Medio
        tablaPagos.getColumnModel().getColumn(5).setPreferredWidth(180);
        // Monto
        tablaPagos.getColumnModel().getColumn(6).setPreferredWidth(100);
    }

    /**
     * Registra un pago usando PagoService.
     */
    private void registrarPago() {
        try {
            int idObl = Integer.parseInt(txtIdObligacion.getText().trim());

            LocalDate fecha;
            String textoFecha = txtFecha.getText().trim();
            if (textoFecha.isEmpty()) {
                fecha = LocalDate.now(); // si no carga nada, uso hoy
            } else {
                try {
                    fecha = LocalDate.parse(textoFecha, FORMATO_FECHA);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "La fecha debe tener formato dd/MM/aaaa.",
                            "Validación",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            String medio = (String) cmbMedio.getSelectedItem();

            BigDecimal monto;
            try {
                monto = new BigDecimal(txtMonto.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "El monto debe ser un número válido.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            pagoService.registrarPago(idObl, fecha, medio, monto);

            JOptionPane.showMessageDialog(
                    this,
                    "Pago registrado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Refresco la tabla de pagos de esa obligación
            listarPagosDeObligacion();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "El ID de la obligación debe ser un número entero.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (DomainException | NotFoundException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Atención",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar el pago:\n" + e.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Lista los pagos de la obligación indicada en txtIdObligacion.
     */
    private void listarPagosDeObligacion() {
        try {
            int idObl = Integer.parseInt(txtIdObligacion.getText().trim());
            List<Pago> pagos = pagoService.listarPorObligacion(idObl);

            DefaultTableModel model = (DefaultTableModel) tablaPagos.getModel();
            model.setRowCount(0);

            for (Pago p : pagos) {
                // Datos adicionales desde la obligación asociada
                String cliente = "";
                String periodo = "";
                if (p.getObligacion() != null) {
                    periodo = p.getObligacion().getPeriodo();
                    if (p.getObligacion().getCliente() != null) {
                        cliente = p.getObligacion().getCliente().getRazonSocial();
                    }
                }

                String fechaStr = "";
                if (p.getFecha() != null) {
                    fechaStr = p.getFecha().format(FORMATO_FECHA);
                }

                model.addRow(new Object[]{
                        p.getIdPago(),
                        p.getObligacion() != null ? p.getObligacion().getIdObligacion() : idObl,
                        cliente,
                        periodo,
                        fechaStr,
                        p.getMedio(),
                        p.getMonto()
                });
            }

            configurarAnchosColumnasPagos();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "El ID de la obligación debe ser un número entero.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (NotFoundException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Atención",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al listar pagos:\n" + e.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

