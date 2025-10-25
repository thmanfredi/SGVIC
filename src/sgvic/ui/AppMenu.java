package sgvic.ui;

import sgvic.dao.ClienteDAO;
import sgvic.dao.TipoObligacionDAO;
import sgvic.entidades.*;
import sgvic.entidades.TipoObligacion.Periodicidad;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.excepciones.NotFoundException;
import sgvic.servicios.ClienteService;
import sgvic.servicios.ObligacionService;
import sgvic.servicios.PagoService;
import sgvic.servicios.AlertaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Menú por consola (MVP) para demostrar:
 * - Estructuras de control (while / switch / if)
 * - Declaración y creación de objetos
 * - Uso de Servicios (reglas) y DAO por debajo
 * - Excepciones con mensajes amigables
 */
public class AppMenu {

    private final Scanner in = new Scanner(System.in);

    // Servicios (capa de negocio)
    private final ClienteService clienteService = new ClienteService();
    private final ObligacionService obligacionService = new ObligacionService();
    private final PagoService pagoService = new PagoService();
    private final AlertaService alertaService = new AlertaService();

    // DAOs útiles para listados de apoyo en la UI (no reglas)
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoObligacionDAO tipoDAO = new TipoObligacionDAO();

    public static void main(String[] args) {
        new AppMenu().run();
    }

    private void run() {
        int op;
        do {
            System.out.println("\n=== SGVIC - Menú Principal ===");
            System.out.println("1) Clientes");
            System.out.println("2) Obligaciones");
            System.out.println("3) Pagos");
            System.out.println("4) Alertas");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            op = leerEntero();

            switch (op) {
                case 1: menuClientes(); break;
                case 2: menuObligaciones(); break;
                case 3: menuPagos(); break;
                case 4: menuAlertas(); break;
                case 0: System.out.println("Hasta luego!"); break;
                default: System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    // ==================== MENÚ CLIENTES ====================
    private void menuClientes() {
        int op;
        do {
            System.out.println("\n--- Clientes ---");
            System.out.println("1) Alta");
            System.out.println("2) Listar");
            System.out.println("3) Buscar por CUIT");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            op = leerEntero();

            try {
                switch (op) {
                    case 1: altaCliente(); break;
                    case 2: listarClientes(); break;
                    case 3: buscarClientePorCuit(); break;
                    case 0: break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (DomainException | DataAccessException e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        } while (op != 0);
    }

    private void altaCliente() throws DomainException, DataAccessException {
        System.out.println("\n> Alta de Cliente");
        System.out.print("Razón social: ");
        String razon = in.nextLine().trim();
        System.out.print("CUIT (11 dígitos): ");
        String cuit = in.nextLine().trim();
        System.out.print("Email: ");
        String email = in.nextLine().trim();
        System.out.print("Teléfono: ");
        String tel = in.nextLine().trim();
        System.out.print("Dirección: ");
        String dir = in.nextLine().trim();

        Cliente c = new Cliente(razon, cuit, email, tel, dir);
        clienteService.alta(c);
        System.out.println("✅ Cliente dado de alta. ID: " + c.getIdCliente());
        pausar();
    }

    private void listarClientes() throws DataAccessException {
        System.out.println("\n> Listado de clientes:");
        clienteService.listar().forEach(System.out::println);
        pausar();
    }

    private void buscarClientePorCuit() throws DomainException, DataAccessException {
        System.out.print("\nCUIT: ");
        String cuit = in.nextLine().trim();
        Cliente c = clienteService.buscarPorCuit(cuit);
        System.out.println(c != null ? c : "No encontrado.");
        pausar();
    }

    // ==================== MENÚ OBLIGACIONES ====================
    private void menuObligaciones() {
        int op;
        do {
            System.out.println("\n--- Obligaciones ---");
            System.out.println("1) Alta");
            System.out.println("2) Listar por cliente");
            System.out.println("3) Ordenar por vencimiento (asc)");
            System.out.println("4) Búsqueda binaria por período (AAAA-MM)");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            op = leerEntero();

            try {
                switch (op) {
                    case 1: altaObligacion(); break;
                    case 2: listarObligacionesPorCliente(); break;
                    case 3: ordenarPorVencimiento(); break;
                    case 4: busquedaBinariaPorPeriodo(); break;
                    case 0: break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (DomainException | DataAccessException e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        } while (op != 0);
    }

    private void altaObligacion() throws DomainException, DataAccessException {
        System.out.println("\n> Alta de Obligación");
        // Mostrar clientes para elegir
        List<Cliente> clientes = clienteDAO.listar();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes. Primero dé de alta un cliente.");
            pausar();
            return;
        }
        System.out.println("Clientes disponibles:");
        clientes.forEach(c -> System.out.println(c.getIdCliente() + " - " + c.getRazonSocial() + " (" + c.getCuit() + ")"));
        System.out.print("ID de cliente: ");
        int idCliente = leerEntero();
        Cliente cli = clientes.stream().filter(c -> c.getIdCliente() == idCliente).findFirst().orElse(null);
        if (cli == null) {
            System.out.println("Cliente inválido.");
            pausar();
            return;
        }

        // Mostrar tipos de obligación
        List<TipoObligacion> tipos = tipoDAO.listar();
        System.out.println("Tipos disponibles:");
        tipos.forEach(t -> System.out.println(t.getIdTipo() + " - " + t.getCodigo() + " (" + t.getPeriodicidad() + ")"));
        System.out.print("ID de tipo: ");
        int idTipo = leerEntero();
        TipoObligacion tipo = tipos.stream().filter(t -> t.getIdTipo() == idTipo).findFirst().orElse(null);
        if (tipo == null) {
            System.out.println("Tipo inválido.");
            pausar();
            return;
        }

        System.out.print("Período (AAAA-MM): ");
        String periodo = in.nextLine().trim();
        System.out.print("Fecha de vencimiento (AAAA-MM-DD): ");
        LocalDate fechaVenc = leerFecha();
        System.out.print("Monto: ");
        BigDecimal monto = leerDecimal();

        // Estado inicial: Pendiente
        EstadoObligacion estado = EstadoObligacion.PENDIENTE;

        // Instanciar subclase según periodicidad (polimorfismo)
        Obligacion obl = crearObligacionPorPeriodicidad(cli, tipo, periodo, fechaVenc, monto, estado);

        obligacionService.alta(obl);
        System.out.println("✅ Obligación creada. ID: " + obl.getIdObligacion());
        pausar();
    }

    private void listarObligacionesPorCliente() throws DataAccessException {
        System.out.print("\nID de cliente: ");
        int id = leerEntero();
        List<Obligacion> lista = obligacionService.listarPorCliente(id);
        if (lista.isEmpty()) System.out.println("(sin obligaciones)");
        else lista.forEach(o -> System.out.println(
                "#" + o.getIdObligacion() + " | " + o.getTipo().getCodigo() + " | " +
                o.getPeriodo() + " | vence " + o.getFechaVenc() + " | " + o.getEstado() + " | $" + o.getMonto()
        ));
        pausar();
    }

    private void ordenarPorVencimiento() throws DataAccessException {
        List<Obligacion> todas = obligacionService.listar();
        obligacionService.ordenarPorVencimiento(todas);
        System.out.println("\nObligaciones ordenadas por vencimiento:");
        todas.forEach(o -> System.out.println(o.getFechaVenc() + " - " + o.getTipo().getCodigo() + " - " + o.getPeriodo()));
        pausar();
    }

    private void busquedaBinariaPorPeriodo() throws DataAccessException {
        List<Obligacion> todas = obligacionService.listar();
        System.out.print("Período a buscar (AAAA-MM): ");
        String p = in.nextLine().trim();
        int idx = obligacionService.ordenarPorPeriodoYBuscarBinario(todas, p);
        if (idx >= 0) {
            Obligacion o = todas.get(idx);
            System.out.println("Encontrada: #" + o.getIdObligacion() + " " + o.getTipo().getCodigo() + " " + o.getPeriodo());
        } else {
            System.out.println("No encontrada para período " + p);
        }
        pausar();
    }

    // ==================== MENÚ PAGOS ====================
    private void menuPagos() {
        int op;
        do {
            System.out.println("\n--- Pagos ---");
            System.out.println("1) Registrar pago");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            op = leerEntero();

            try {
                switch (op) {
                    case 1: registrarPago(); break;
                    case 0: break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (DomainException | DataAccessException | NotFoundException e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        } while (op != 0);
    }

    private void registrarPago() throws DomainException, DataAccessException, NotFoundException {
        System.out.print("\nID de obligación: ");
        int idObl = leerEntero();
        System.out.print("Fecha (AAAA-MM-DD) [Enter = hoy]: ");
        String f = in.nextLine().trim();
        LocalDate fecha = f.isBlank() ? LocalDate.now() : LocalDate.parse(f);
        System.out.print("Medio (Efectivo/Transferencia/etc.): ");
        String medio = in.nextLine().trim();
        System.out.print("Monto: ");
        BigDecimal monto = leerDecimal();

        pagoService.registrarPago(idObl, fecha, medio, monto);
        System.out.println("✅ Pago registrado y obligación marcada como PAGADA.");
        pausar();
    }
    // ==================== MENÚ ALERTAS ====================
private void menuAlertas() {
    int op;
    do {
        System.out.println("\n--- Alertas ---");
        System.out.println("1) Generar alertas (vencidas y por vencer)");
        System.out.println("2) Listar alertas pendientes (Queue)");
        System.out.println("3) Marcar alerta como leída");
        System.out.println("0) Volver");
        System.out.print("Opción: ");
        op = leerEntero();

        try {
            switch (op) {
                case 1: generarAlertas(); break;
                case 2: listarAlertasPendientes(); break;
                case 3: marcarAlertaLeida(); break;
                case 0: break;
                default: System.out.println("Opción inválida.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ " + e.getMessage());
        }
    } while (op != 0);
}

private void generarAlertas() throws DataAccessException {
    System.out.print("Días de aviso (ej. 7): ");
    int dias = leerEntero();
    java.time.LocalDate hoy = java.time.LocalDate.now();
    java.util.Deque<Alerta> nuevas = alertaService.generarPendientes(hoy, dias);
    System.out.println("Se generaron " + nuevas.size() + " alertas nuevas para " + hoy + ".");
    pausar();
}

private void listarAlertasPendientes() throws DataAccessException {
    java.util.Deque<Alerta> cola = alertaService.listarPendientes();
    if (cola.isEmpty()) {
        System.out.println("(no hay alertas pendientes)");
    } else {
        System.out.println("Alertas pendientes (FIFO):");
        // Mostramos en orden; como Alerta.obligacion puede ser null, imprimimos seguro
        for (Alerta a : cola) {
            System.out.println("#" + a.getIdAlerta() + " | fecha=" + a.getFecha() + " | leida=" + a.isLeida());
        }
    }
    pausar();
}

private void marcarAlertaLeida() throws DataAccessException, sgvic.excepciones.DomainException {
    System.out.print("ID de alerta a marcar como leída: ");
    int id = leerEntero();
    alertaService.marcarLeida(id);
    System.out.println("✅ Alerta #" + id + " marcada como leída.");
    pausar();
}


    // ==================== Helpers de UI ====================
    private Obligacion crearObligacionPorPeriodicidad(Cliente c, TipoObligacion t, String periodo,
                                                      LocalDate fechaVenc, BigDecimal monto, EstadoObligacion estado) {
        if (t.getPeriodicidad() == Periodicidad.ANUAL) {
            return new ObligacionAnual(c, t, periodo, fechaVenc, monto, estado);
        } else {
            return new ObligacionMensual(c, t, periodo, fechaVenc, monto, estado);
        }
    }

    private int leerEntero() {
        while (true) {
            String s = in.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.print("Ingrese un número válido: "); }
        }
    }

    private LocalDate leerFecha() {
        while (true) {
            String s = in.nextLine().trim();
            try { return LocalDate.parse(s); }
            catch (Exception e) { System.out.print("Formato inválido. Use AAAA-MM-DD: "); }
        }
    }

    private BigDecimal leerDecimal() {
        while (true) {
            String s = in.nextLine().trim().replace(",", ".");
            try { return new BigDecimal(s); }
            catch (Exception e) { System.out.print("Monto inválido. Ingrese un número: "); }
        }
    }

    private void pausar() {
        System.out.print("\n[Enter para continuar]");
        in.nextLine();
    }
}
