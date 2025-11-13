package sgvic.ui;

import sgvic.entidades.Cliente;
import sgvic.entidades.Obligacion;
import sgvic.entidades.TipoObligacion;
import sgvic.excepciones.DataAccessException;
import sgvic.servicios.ObligacionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de alertas de vencimientos.
 *
 * Genera alertas a partir de las obligaciones:
 *  - Obligaciones vencidas.
 *  - Obligaciones próximas a vencer (dentro de X días).
 *
 * No persiste alertas en BD, sino que las calcula en el momento
 * a partir de los datos de obligaciones.
 */
public class PanelAlertas extends JPanel {

    private final ObligacionService obligacionService = new ObligacionService();

    private JTable tablaAlertas;
    private JButton btnGenerar;
    private JButton btnLimpiar;

    // Arreglo de días de aviso (ejemplo simple de uso de arreglos)
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

        panelBotones.add(btnGenerar);
        panelBotones.add(btnLimpiar);

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

        configurarAnchosColumnasAlertas();

        JScrollPane scroll = new JScrollPane(tablaAlertas);
        add(scroll, BorderLayout.CENTER);

        btnGenerar.addActionListener(e -> generarAlertas());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void configurarAnchosColumnasAlertas() {
        tablaAlertas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Cliente
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(220);
        // Tipo
        tablaAlertas.getColumnModel().getColumn(1).setPreferredWidth(140);
        // Período
        tablaAlertas.getColumnModel().getColumn(2).setPreferredWidth(90);
        // Fecha Venc.
        tablaAlertas.getColumnModel().getColumn(3).setPreferredWidth(110);
        // Días restantes
        tablaAlertas.getColumnModel().getColumn(4).setPreferredWidth(110);
        // Situación
        tablaAlertas.getColumnModel().getColumn(5).setPreferredWidth(130);
    }

    /**
     * Genera alertas a partir de las obligaciones,
     * considerando las constantes DIAS_AVISO.
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
     * Devuelve solo las obligaciones vencidas o próximas a vencer
     * según los días configurados en DIAS_AVISO.
     */
    private List<Obligacion> filtrarConAlerta(List<Obligacion> origen) {
        List<Obligacion> resultado = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Obligacion o : origen) {
            if (o.getFechaVenc() == null) continue;

            long dias = ChronoUnit.DAYS.between(hoy, o.getFechaVenc());

            boolean esVencida = dias < 0;
            boolean esProxima = false;

            if (!esVencida) {
                for (int limite : DIAS_AVISO) {
                    if (limite > 0 && dias <= limite) {
                        esProxima = true;
                        break;
                    }
                }
            }

            if (esVencida || esProxima) {
                resultado.add(o);
            }
        }

        return resultado;
    }

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
            String fechaVenc = o.getFechaVenc() != null ? o.getFechaVenc().format(FORMATO_FECHA) : "";

            long dias = o.getFechaVenc() != null ? ChronoUnit.DAYS.between(hoy, o.getFechaVenc()) : 0;
            String situacion;
            if (dias < 0) {
                situacion = "VENCIDA";
            } else if (dias == 0) {
                situacion = "Vence HOY";
            } else if (dias <= 7) {
                situacion = "Próxima a vencer";
            } else if (dias <= 15) {
                situacion = "Aviso temprano";
            } else {
                situacion = "En término";
            }

            model.addRow(new Object[]{
                    cliente,
                    tipo,
                    periodo,
                    fechaVenc,
                    dias,
                    situacion
            });
        }

        configurarAnchosColumnasAlertas();
    }

    private void limpiar() {
        DefaultTableModel model = (DefaultTableModel) tablaAlertas.getModel();
        model.setRowCount(0);
    }
}




