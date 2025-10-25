package sgvic.ui;

import sgvic.entidades.Obligacion;
import sgvic.excepciones.DataAccessException;
import sgvic.servicios.ObligacionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de Obligaciones (Swing).
 * Lista obligaciones desde la BD usando la capa de servicios.
 */
public class PanelObligaciones extends JPanel {

    private final ObligacionService service = new ObligacionService();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Cliente", "Tipo", "Período", "Vence", "Estado", "Monto"}, 0
    );
    private final JTable tabla = new JTable(modelo);

    public PanelObligaciones() {
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Obligaciones", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel acciones = new JPanel();
        JButton btnListar = new JButton("Listar Obligaciones");
        JButton btnOrdenar = new JButton("Ordenar por Vencimiento");
        JButton btnBuscarPeriodo = new JButton("Búsqueda binaria por Período");
        acciones.add(btnListar);
        acciones.add(btnOrdenar);
        acciones.add(btnBuscarPeriodo);

        add(titulo, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);

        // Listar: llama al Service y llena la JTable
        btnListar.addActionListener(e -> {
            try {
                cargarObligaciones();
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, "Error al listar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ordenar por vencimiento (en memoria, sobre lo cargado)
        btnOrdenar.addActionListener(e -> {
            try {
                List<Obligacion> todas = service.listar();
                service.ordenarPorVencimiento(todas);
                volcarTabla(todas);
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo ordenar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Búsqueda binaria por período (AAAA-MM)
        btnBuscarPeriodo.addActionListener(e -> {
            String p = JOptionPane.showInputDialog(this, "Período (AAAA-MM):");
            if (p == null || p.isBlank()) return;
            try {
                List<Obligacion> todas = service.listar();
                int idx = service.ordenarPorPeriodoYBuscarBinario(todas, p.trim());
                if (idx >= 0) {
                    // Refresco tabla ordenada por período y selecciono el hallado
                    volcarTabla(todas);
                    tabla.setRowSelectionInterval(idx, idx);
                    tabla.scrollRectToVisible(tabla.getCellRect(idx, 0, true));
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró obligación para período " + p,
                            "Resultado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo buscar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Carga desde BD y llena la tabla
    private void cargarObligaciones() throws DataAccessException {
        List<Obligacion> lista = service.listar();
        volcarTabla(lista);
    }

    // Helper para renderizar la lista en la JTable
    private void volcarTabla(List<Obligacion> lista) {
        modelo.setRowCount(0);
        for (Obligacion o : lista) {
            modelo.addRow(new Object[]{
                    o.getIdObligacion(),
                    (o.getCliente() != null ? o.getCliente().getRazonSocial() : "(sin cliente)"),
                    (o.getTipo() != null ? o.getTipo().getCodigo() : "(sin tipo)"),
                    o.getPeriodo(),
                    (o.getFechaVenc() != null ? o.getFechaVenc() : "(sin fecha)"),
                    o.getEstado(),
                    (o.getMonto() != null ? o.getMonto() : 0)
            });
        }
    }
}


