package sgvic.entidades;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Obligación de periodicidad anual (ej.: Ganancias). */
public class ObligacionAnual extends Obligacion {

    public ObligacionAnual() { }

    public ObligacionAnual(Cliente c, TipoObligacion t, String periodo,
                           LocalDate fechaVenc, BigDecimal monto, EstadoObligacion estado) {
        super(c, t, periodo, fechaVenc, monto, estado);
    }

    @Override
    public BigDecimal calcularInteres(LocalDate hoy) {
        if (!estaVencida(hoy)) return BigDecimal.ZERO;
        long dias = ChronoUnit.DAYS.between(getFechaVenc(), hoy);
        BigDecimal tasaDiaria = new BigDecimal("0.0003"); // 0.03% diario (didáctico)
        return getMonto().multiply(tasaDiaria).multiply(BigDecimal.valueOf(dias))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
