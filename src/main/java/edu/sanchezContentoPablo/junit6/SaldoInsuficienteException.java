package edu.sanchezContentoPablo.junit6;

import java.math.BigDecimal;

/**
 * Se lanza al intentar sacar más dinero del que hay en la cuenta.
 */
public class SaldoInsuficienteException extends RuntimeException {

    private final BigDecimal disponible;

    /**
     * Crea la excepción indicando el saldo disponible.
     *
     * @param solicitado importe que se intentó retirar
     * @param disponible saldo que había en la cuenta
     */
    public SaldoInsuficienteException(BigDecimal solicitado, BigDecimal disponible) {
        super("Saldo insuficiente: se solicitan " + solicitado + " € y hay " + disponible + " €");
        this.disponible = disponible;
    }

    /**
     * Saldo que había en la cuenta en el momento del error.
     *
     * @return el saldo disponible
     */
    public BigDecimal getDisponible() {
        return disponible;
    }
}
