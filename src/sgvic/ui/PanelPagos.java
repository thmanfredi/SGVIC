package sgvic.ui;

import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.excepciones.NotFoundException;
import sgvic.servicios.PagoService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Panel de Pagos (Swing).
 * Toma datos desde la UI y llama a PagoService.registrarPago.
 */
public class PanelPagos extends JPanel {

    private final PagoService service = new PagoService();

    private final JTextField txtIdObl = new JTextField();
    private final JTextField txtFecha = new JTextField(); // AAAA-MM-DD (opcional)
    private final JTextField txtMonto = new JTextField();
    private final JTextField txtMedio = new JTextField();

    public PanelPagos() {
        setLayout(new BorderLayout());
        JLabel lblTitulo = new JLabel("Registro de Pagos", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("ID Obligación:"));
        form.add(txtIdObl);

        form.add(new JLabel("Fecha (AAAA-MM-DD) [vacío=hoy]:"));
        form.add(txtFecha);

        form.add(new JLabel("Monto:"));
        form.add(txtMonto);

        form.add(new JLabel("Medio:"));
        form.add(txtMedio);

        JButton btnRegistrar = new JButton("Registrar Pago");
        form.add(new JLabel()); // filler
        form.add(btnRegistrar);

        add(lblTitulo, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        // Acción: Registrar pago
        btnRegistrar.addActionListener(e -> {
            try {
                int idObl = Integer.parseInt(txtIdObl.getText().trim());
                LocalDate fecha = txtFecha.getText().trim().isBlank()
                        ? LocalDate.now()
                        : LocalDate.parse(txtFecha.getText().trim());
                BigDecimal monto = new BigDecimal(txtMonto.getText().trim().replace(",", "."));
                String medio = txtMedio.getText().trim();

                service.registrarPago(idObl, fecha, medio, monto);

                JOptionPane.showMessageDialog(this, "Pago registrado y obligación marcada como PAGADA.",
                        "OK", JOptionPane.INFORMATION_MESSAGE);

                // Limpio campos
                txtIdObl.setText("");
                txtFecha.setText("");
                txtMonto.setText("");
                txtMedio.setText("");

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID de obligación o monto inválido.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (DomainException | DataAccessException | NotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

