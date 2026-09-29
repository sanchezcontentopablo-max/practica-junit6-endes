package edu.sanchezContentoPablo.junit6;

/**
 * Tipos de movimiento que puede registrar una {@link CuentaBancaria}.
 */
public enum TipoMovimiento {

    /** Dinero que entra en la cuenta por un ingreso. */
    INGRESO(true),

    /** Dinero que sale de la cuenta por una retirada. */
    RETIRADA(false),

    /** Dinero que sale hacia otra cuenta. */
    TRANSFERENCIA_ENVIADA(false),

    /** Dinero que llega desde otra cuenta. */
    TRANSFERENCIA_RECIBIDA(true);

    private final boolean suma;

    TipoMovimiento(boolean suma) {
        this.suma = suma;
    }

    /**
     * Indica si el movimiento aumenta el saldo.
     *
     * @return {@code true} si el movimiento suma dinero a la cuenta
     */
    public boolean sumaAlSaldo() {
        return suma;
    }
}
