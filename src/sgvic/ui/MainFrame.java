package sgvic.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("SGVIC - Sistema de Gestión de Vencimientos Impositivos y Contables");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes", new PanelClientes());
        tabs.addTab("Obligaciones", new PanelObligaciones());
        tabs.addTab("Pagos", new PanelPagos());
        tabs.addTab("Alertas", new PanelAlertas()); // ← esto funciona si PanelAlertas extends JPanel

        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

