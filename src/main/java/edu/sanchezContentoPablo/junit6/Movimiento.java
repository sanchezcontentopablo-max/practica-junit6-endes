package edu.sanchezContentoPablo.junit6;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Movimiento registrado en una cuenta. Es inmutable.
 *
 * @param tipo     tipo de movimiento
 * @param importe  importe positivo, en euros
 * @param concepto descripción libre del movimiento
 */
public record Movimiento(TipoMovimiento tipo, BigDecimal importe, String concepto) {

    /**
     * Crea un movimiento comprobando que sus datos son válidos.
     *
     * @throws NullPointerException     si el tipo o el importe son nulos
     * @throws IllegalArgumentException si el importe no es positivo
     */
    public Movimiento {
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(importe, "importe");
        if (importe.signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser positivo");
        }
        concepto = concepto == null ? "" : concepto;
    }

    /**
     * Importe con signo: positivo si suma al saldo y negativo si resta.
     *
     * @return el importe con signo
     */
    public BigDecimal importeConSigno() {
        return tipo.sumaAlSaldo() ? importe : importe.negate();
    }
}
