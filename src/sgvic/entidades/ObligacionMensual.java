package sgvic.entidades;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Obligación de periodicidad mensual (ej.: IVA, MON, SIC). */
public class ObligacionMensual extends Obligacion {

    public ObligacionMensual() { }

    public ObligacionMensual(Cliente c, TipoObligacion t, String periodo,
                             LocalDate fechaVenc, BigDecimal monto, EstadoObligacion estado) {
        super(c, t, periodo, fechaVenc, monto, estado);
    }

    @Override
    public BigDecimal calcularInteres(LocalDate hoy) {
        if (!estaVencida(hoy)) return BigDecimal.ZERO;
        long dias = ChronoUnit.DAYS.between(getFechaVenc(), hoy);
        BigDecimal tasaDiaria = new BigDecimal("0.0005"); // 0.05% diario (didáctico)
        return getMonto().multiply(tasaDiaria).multiply(BigDecimal.valueOf(dias))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

