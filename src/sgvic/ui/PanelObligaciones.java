package sgvic.ui;

import sgvic.entidades.Cliente;
import sgvic.entidades.Obligacion;
import sgvic.entidades.TipoObligacion;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.servicios.ObligacionService;
import sgvic.servicios.ClienteService;
import sgvic.servicios.TipoObligacionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de gestión de obligaciones fiscales.
 *
 * Funcionalidades:
 *  - Listar obligaciones desde la BD.
 *  - Ordenar por fecha de vencimiento (usa algoritmo en ObligacionService).
 *  - Buscar por período (búsqueda binaria).
 *  - Agregar nuevas obligaciones desde la interfaz.
 *
 * Este panel muestra bien la integración entre:
 *  - Lógica de negocio (servicio),
 *  - Acceso a datos (DAO),
 *  - y la interfaz Swing.
 */
public class PanelObligaciones extends JPanel {

    // Servicios
    private final ObligacionService obligacionService = new ObligacionService();
    private final ClienteService clienteService = new ClienteService();
    private final TipoObligacionService tipoObligacionService = new TipoObligacionService();

    // Componentes UI
    private JTable tablaObligaciones;
    private JButton btnListar;
    private JButton btnOrdenar;
    private JButton btnBuscar;
    private JButton btnAgregar;
    private JTextField txtPeriodo;

    /**
     * Última lista de obligaciones cargada en la tabla.
     * La uso para ordenar y buscar sin ir todo el tiempo a la BD.
     */
    private List<Obligacion> ultimaLista = new ArrayList<>();

    private final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelObligaciones() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // --- Panel superior con botones y campo de búsqueda ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnListar = new JButton("Listar obligaciones");
        btnOrdenar = new JButton("Ordenar por vencimiento");
        btnAgregar = new JButton("Agregar obligación");

        JLabel lblPeriodo = new JLabel("Período (AAAA-MM):");
        txtPeriodo = new JTextField(8);
        btnBuscar = new JButton("Buscar por período");

        panelSuperior.add(btnListar);
        panelSuperior.add(btnOrdenar);
        panelSuperior.add(btnAgregar);
        panelSuperior.add(Box.createHorizontalStrut(15));
        panelSuperior.add(lblPeriodo);
        panelSuperior.add(txtPeriodo);
        panelSuperior.add(btnBuscar);

        add(panelSuperior, BorderLayout.NORTH);

        // --- Tabla central ---
        tablaObligaciones = new JTable();
        tablaObligaciones.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "ID", "Cliente", "Tipo", "Período", "Vencimiento", "Estado", "Monto"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });

        configurarAnchosColumnas();

        JScrollPane scroll = new JScrollPane(tablaObligaciones);
        add(scroll, BorderLayout.CENTER);

        // --- Acciones de los botones ---
        btnListar.addActionListener(e -> listarObligaciones());
        btnOrdenar.addActionListener(e -> ordenarPorVencimiento());
        btnBuscar.addActionListener(e -> buscarPorPeriodo());
        btnAgregar.addActionListener(e -> agregarObligacion());
    }

    /**
     * Ajusta los anchos de las columnas de la tabla de obligaciones.
     */
    private void configurarAnchosColumnas() {
        tablaObligaciones.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // ID
        tablaObligaciones.getColumnModel().getColumn(0).setPreferredWidth(40);
        // Cliente
        tablaObligaciones.getColumnModel().getColumn(1).setPreferredWidth(220);
        // Tipo
        tablaObligaciones.getColumnModel().getColumn(2).setPreferredWidth(140);
        // Período
        tablaObligaciones.getColumnModel().getColumn(3).setPreferredWidth(90);
        // Vencimiento
        tablaObligaciones.getColumnModel().getColumn(4).setPreferredWidth(110);
        // Estado
        tablaObligaciones.getColumnModel().getColumn(5).setPreferredWidth(100);
        // Monto
        tablaObligaciones.getColumnModel().getColumn(6).setPreferredWidth(110);
    }

    /**
     * Llama al servicio para traer todas las obligaciones de la BD
     * y las carga en la tabla.
     */
    private void listarObligaciones() {
        try {
            ultimaLista = obligacionService.listar();
            cargarEnTabla(ultimaLista);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al listar obligaciones:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Ordena la lista actual por fecha de vencimiento usando el servicio
     * y vuelve a cargar la tabla.
     */
    private void ordenarPorVencimiento() {
        if (ultimaLista == null || ultimaLista.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe listar las obligaciones.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        List<Obligacion> ordenadas = obligacionService.ordenarPorVencimiento(ultimaLista);
        cargarEnTabla(ordenadas);
    }

    /**
     * Busca una obligación por período utilizando búsqueda binaria.
     * Si la encuentra, selecciona la fila correspondiente en la tabla.
     */
    private void buscarPorPeriodo() {
        if (ultimaLista == null || ultimaLista.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe listar las obligaciones.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String periodo = txtPeriodo.getText();
        if (periodo == null || periodo.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un período en formato AAAA-MM.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Obligacion encontrada = obligacionService.buscarPorPeriodo(ultimaLista, periodo);
        if (encontrada == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró ninguna obligación para el período: " + periodo,
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Buscar la fila que corresponde a la obligación encontrada
        DefaultTableModel model = (DefaultTableModel) tablaObligaciones.getModel();
        int filaEncontrada = -1;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object idTabla = model.getValueAt(i, 0);
            if (idTabla instanceof Integer) {
                int id = (Integer) idTabla;
                if (id == encontrada.getIdObligacion()) {
                    filaEncontrada = i;
                    break;
                }
            } else if (idTabla != null) {
                int id = Integer.parseInt(idTabla.toString());
                if (id == encontrada.getIdObligacion()) {
                    filaEncontrada = i;
                    break;
                }
            }
        }

        if (filaEncontrada >= 0) {
            tablaObligaciones.setRowSelectionInterval(filaEncontrada, filaEncontrada);
            tablaObligaciones.scrollRectToVisible(
                    tablaObligaciones.getCellRect(filaEncontrada, 0, true)
            );
        }

        JOptionPane.showMessageDialog(
                this,
                "Obligación encontrada para el período " + periodo +
                        ".\nSe seleccionó la fila correspondiente en la tabla.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Abre un formulario simple para crear una nueva obligación
     * y la guarda en la BD a través del servicio.
     */
    private void agregarObligacion() {
        try {
            List<Cliente> clientes = clienteService.listar();
            List<TipoObligacion> tipos = tipoObligacionService.listarTodos();

            if (clientes.isEmpty() || tipos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debe haber al menos un cliente y un tipo de obligación cargados.",
                        "Datos faltantes",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<Cliente> cmbClientes = new JComboBox<>(clientes.toArray(new Cliente[0]));
            JComboBox<TipoObligacion> cmbTipos = new JComboBox<>(tipos.toArray(new TipoObligacion[0]));
            JTextField txtPeriodo = new JTextField(7);      // AAAA-MM
            JTextField txtFechaVenc = new JTextField(10);   // dd/MM/aaaa
            JTextField txtMonto = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.setPreferredSize(new java.awt.Dimension(450, 180));

            panel.add(new JLabel("Cliente:"));
            panel.add(cmbClientes);
            panel.add(new JLabel("Tipo obligación:"));
            panel.add(cmbTipos);
            panel.add(new JLabel("Período (AAAA-MM):"));
            panel.add(txtPeriodo);
            panel.add(new JLabel("Fecha venc. (dd/MM/aaaa):"));
            panel.add(txtFechaVenc);
            panel.add(new JLabel("Monto:"));
            panel.add(txtMonto);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Nueva obligación", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                Cliente clienteSel = (Cliente) cmbClientes.getSelectedItem();
                TipoObligacion tipoSel = (TipoObligacion) cmbTipos.getSelectedItem();
                String periodo = txtPeriodo.getText().trim();
                String fechaStr = txtFechaVenc.getText().trim();
                String montoStr = txtMonto.getText().trim().replace(',', '.');

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate fechaVenc = LocalDate.parse(fechaStr, fmt);
                BigDecimal monto = new BigDecimal(montoStr);

                obligacionService.crearObligacion(clienteSel, tipoSel, periodo, fechaVenc, monto);

                JOptionPane.showMessageDialog(this,
                        "Obligación creada correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Refrescar la tabla
                listarObligaciones();
            }

        } catch (DomainException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de datos", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, "Error de acceso a datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear la obligación: " + ex.getMessage(),
                    "Error inesperado", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Carga una lista de obligaciones en la JTable.
     */
    private void cargarEnTabla(List<Obligacion> lista) {
        DefaultTableModel model = (DefaultTableModel) tablaObligaciones.getModel();
        model.setRowCount(0);

        for (Obligacion o : lista) {
            Cliente c = o.getCliente();
            TipoObligacion t = o.getTipo();
            String nombreCliente = c != null ? c.getRazonSocial() : "(sin cliente)";
            String tipo = t != null ? t.getCodigo() : "(sin tipo)";
            String periodo = o.getPeriodo();
            String venc = o.getFechaVenc() != null ? o.getFechaVenc().format(FORMATO_FECHA) : "";
            String estado = o.getEstado() != null ? o.getEstado().name() : "";
            BigDecimal monto = o.getMonto();

            model.addRow(new Object[]{
                    o.getIdObligacion(),
                    nombreCliente,
                    tipo,
                    periodo,
                    venc,
                    estado,
                    monto
            });
        }

        configurarAnchosColumnas();
    }
}

