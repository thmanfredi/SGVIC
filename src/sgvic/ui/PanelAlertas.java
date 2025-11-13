package sgvic.ui;

import sgvic.entidades.Cliente;
import sgvic.entidades.Obligacion;
import sgvic.entidades.TipoObligacion;
import sgvic.excepciones.DataAccessException;
import sgvic.servicios.ObligacionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de alertas de vencimientos.
 *
 * Muestra:
 *  - Obligaciones vencidas
 *  - Obligaciones próximas a vencer (0, 7 y 15 días)
 *
 * Incluye:
 *  - Generar alertas
 *  - Limpiar
 *  - Marcar como leída (quita la fila seleccionada)
 */
public class PanelAlertas extends JPanel {

    private final ObligacionService obligacionService = new ObligacionService();

    private JTable tablaAlertas;
    private JButton btnGenerar;
    private JButton btnLimpiar;
    private JButton btnMarcarLeida;

    // Configuración de días de aviso
    private static final int[] DIAS_AVISO = {0, 7, 15};

    private final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelAlertas() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnGenerar = new JButton("Generar alertas");
        btnLimpiar = new JButton("Limpiar");
        btnMarcarLeida = new JButton("Marcar como leída");

        panelBotones.add(btnGenerar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnMarcarLeida);

        add(panelBotones, BorderLayout.NORTH);

        tablaAlertas = new JTable();
        tablaAlertas.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Cliente", "Tipo", "Período",
                        "Fecha Venc.", "Días restantes", "Situación"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });

        tablaAlertas.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(tablaAlertas);
        add(scroll, BorderLayout.CENTER);

        btnGenerar.addActionListener(e -> generarAlertas());
        btnLimpiar.addActionListener(e -> limpiar());
        btnMarcarLeida.addActionListener(e -> marcarComoLeida());
    }

    /**
     * Ajusta los anchos de las columnas para que el nombre del cliente
     * y tipo se vean completos.
     */
    private void ajustarColumnas() {
        TableColumnModel col = tablaAlertas.getColumnModel();
        col.getColumn(0).setPreferredWidth(170);  // Cliente
        col.getColumn(1).setPreferredWidth(110);  // Tipo obligacion
        col.getColumn(2).setPreferredWidth(80);   // Periodo
        col.getColumn(3).setPreferredWidth(90);   // Fecha venc
        col.getColumn(4).setPreferredWidth(90);   // Días restantes
        col.getColumn(5).setPreferredWidth(130);  // Situación
    }

    /**
     * Genera alertas a partir de las obligaciones en BD.
     */
    private void generarAlertas() {
        try {
            List<Obligacion> obligaciones = obligacionService.listar();
            List<Obligacion> conAlerta = filtrarConAlerta(obligaciones);

            cargarEnTabla(conAlerta);

            JOptionPane.showMessageDialog(
                    this,
                    "Se generaron " + conAlerta.size() + " alertas.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar alertas:\n" + e.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Devuelve obligaciones vencidas o próximas a vencer.
     */
    private List<Obligacion> filtrarConAlerta(List<Obligacion> origen) {
        List<Obligacion> resultado = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Obligacion o : origen) {
            if (o.getFechaVenc() == null) continue;

            long dias = ChronoUnit.DAYS.between(hoy, o.getFechaVenc());

            boolean esVencida = dias < 0;
            boolean esProxima = dias >= 0 && (
                    dias <= DIAS_AVISO[1] || dias <= DIAS_AVISO[2]
            );

            if (esVencida || esProxima) {
                resultado.add(o);
            }
        }

        return resultado;
    }

    /**
     * Carga datos en tabla con formateos.
     */
    private void cargarEnTabla(List<Obligacion> lista) {
        DefaultTableModel model = (DefaultTableModel) tablaAlertas.getModel();
        model.setRowCount(0);

        LocalDate hoy = LocalDate.now();

        for (Obligacion o : lista) {
            Cliente c = o.getCliente();
            TipoObligacion t = o.getTipo();

            String cliente = c != null ? c.getRazonSocial() : "(sin cliente)";
            String tipo = t != null ? t.getCodigo() : "(sin tipo)";
            String periodo = o.getPeriodo();
            String fechaVenc = o.getFechaVenc() != null
                    ? o.getFechaVenc().format(FORMATO_FECHA)
                    : "";

            long dias = o.getFechaVenc() != null ? ChronoUnit.DAYS.between(hoy, o.getFechaVenc()) : 0;

            String situacion;
            if (dias < 0) situacion = "VENCIDA";
            else if (dias == 0) situacion = "Vence HOY";
            else if (dias <= 7) situacion = "Próxima a vencer";
            else if (dias <= 15) situacion = "Aviso temprano";
            else situacion = "En término";

            model.addRow(new Object[]{
                    cliente, tipo, periodo, fechaVenc, dias, situacion
            });
        }

        ajustarColumnas();
    }

    /**
     * Limpia la tabla.
     */
    private void limpiar() {
        DefaultTableModel model = (DefaultTableModel) tablaAlertas.getModel();
        model.setRowCount(0);
    }

    /**
     * Elimina la alerta seleccionada.
     */
    private void marcarComoLeida() {
        int fila = tablaAlertas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una alerta para marcarla como leída.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ((DefaultTableModel) tablaAlertas.getModel()).removeRow(fila);
    }
}






