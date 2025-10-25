package sgvic.ui;

import sgvic.entidades.Alerta;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.servicios.AlertaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Deque;

/**
 * Panel de Alertas (Swing).
 * Integra la cola de alertas con una JTable.
 */
public class PanelAlertas extends JPanel {

    private final AlertaService service = new AlertaService();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Leída"}, 0
    );
    private final JTable tabla = new JTable(modelo);

    public PanelAlertas() {
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Alertas Pendientes", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel acciones = new JPanel();
        JButton btnGenerar = new JButton("Generar (vencidas / por vencer)");
        JButton btnListar = new JButton("Listar pendientes");
        JButton btnMarcar = new JButton("Marcar como leída (seleccionada)");
        acciones.add(btnGenerar);
        acciones.add(btnListar);
        acciones.add(btnMarcar);

        add(lblTitulo, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);

        // Generar nuevas alertas (hoy, con días de aviso)
        btnGenerar.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(this, "Días de aviso (ej. 7):");
            if (s == null || s.isBlank()) return;
            try {
                int dias = Integer.parseInt(s.trim());
                Deque<Alerta> nuevas = service.generarPendientes(LocalDate.now(), dias);
                JOptionPane.showMessageDialog(this, "Generadas " + nuevas.size() + " alertas nuevas.",
                        "Resultado", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, "Error al generar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Listar pendientes (Queue → JTable)
        btnListar.addActionListener(e -> {
            try {
                cargarPendientes();
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, "Error al listar alertas: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Marcar como leída la seleccionada
        btnMarcar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una alerta de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(modelo.getValueAt(row, 0).toString());
            try {
                service.marcarLeida(id);
                cargarPendientes(); // refresco
            } catch (DomainException | DataAccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void cargarPendientes() throws DataAccessException {
        modelo.setRowCount(0);
        Deque<Alerta> cola = service.listarPendientes();
        for (Alerta a : cola) {
            modelo.addRow(new Object[]{
                    a.getIdAlerta(),
                    a.getFecha(),
                    a.isLeida() ? "Sí" : "No"
            });
        }
    }
}



