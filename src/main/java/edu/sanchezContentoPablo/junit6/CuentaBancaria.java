package edu.sanchezContentoPablo.junit6;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Cuenta bancaria sencilla que registra todos sus movimientos.
 *
 * <p>Los importes se manejan con {@link BigDecimal} y dos decimales para evitar
 * los errores de redondeo de {@code double} con dinero.</p>
 *
 * @author Pablo Sánchez Contento
 */
public class CuentaBancaria {

    private final String titular;
    private final List<Movimiento> movimientos = new ArrayList<>();
    private BigDecimal saldo = BigDecimal.ZERO.setScale(2);

    /**
     * Abre una cuenta vacía.
     *
     * @param titular nombre del titular; no puede estar vacío
     * @throws IllegalArgumentException si el titular es nulo o está vacío
     */
    public CuentaBancaria(String titular) {
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("El titular es obligatorio");
        }
        this.titular = titular.strip();
    }

    /**
     * Ingresa dinero en la cuenta.
     *
     * @param importe  importe positivo en euros
     * @param concepto descripción del ingreso
     * @throws IllegalArgumentException si el importe no es positivo
     */
    public void ingresar(BigDecimal importe, String concepto) {
        registrar(new Movimiento(TipoMovimiento.INGRESO, normalizar(importe), concepto));
    }

    /**
     * Retira dinero de la cuenta.
     *
     * @param importe  importe positivo en euros
     * @param concepto descripción de la retirada
     * @throws IllegalArgumentException    si el importe no es positivo
     * @throws SaldoInsuficienteException si no hay saldo suficiente
     */
    public void retirar(BigDecimal importe, String concepto) {
        BigDecimal cantidad = normalizar(importe);
        comprobarSaldo(cantidad);
        registrar(new Movimiento(TipoMovimiento.RETIRADA, cantidad, concepto));
    }

    /**
     * Transfiere dinero a otra cuenta. Si falla, ninguna de las dos cuentas cambia.
     *
     * @param destino cuenta que recibe el dinero; distinta de esta
     * @param importe importe positivo en euros
     * @throws IllegalArgumentException    si el destino es esta misma cuenta o el importe no es válido
     * @throws SaldoInsuficienteException si no hay saldo suficiente
     */
    public void transferir(CuentaBancaria destino, BigDecimal importe) {
        Objects.requireNonNull(destino, "destino");
        if (destino == this) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }
        BigDecimal cantidad = normalizar(importe);
        comprobarSaldo(cantidad);
        registrar(new Movimiento(TipoMovimiento.TRANSFERENCIA_ENVIADA, cantidad, "A " + destino.titular));
        destino.registrar(new Movimiento(TipoMovimiento.TRANSFERENCIA_RECIBIDA, cantidad, "De " + titular));
    }

    /**
     * Nombre del titular.
     *
     * @return el titular
     */
    public String getTitular() {
        return titular;
    }

    /**
     * Saldo actual con dos decimales.
     *
     * @return el saldo
     */
    public BigDecimal getSaldo() {
        return saldo;
    }

    /**
     * Movimientos en orden cronológico. La lista no se puede modificar.
     *
     * @return los movimientos de la cuenta
     */
    public List<Movimiento> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }

    private void registrar(Movimiento movimiento) {
        movimientos.add(movimiento);
        saldo = saldo.add(movimiento.importeConSigno());
    }

    private void comprobarSaldo(BigDecimal cantidad) {
        if (cantidad.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException(cantidad, saldo);
        }
    }

    private static BigDecimal normalizar(BigDecimal importe) {
        Objects.requireNonNull(importe, "importe");
        if (importe.signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser positivo: " + importe);
        }
        return importe.setScale(2, RoundingMode.HALF_EVEN);
    }
}
