package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import edu.sanchezContentoPablo.junit6.Movimiento;
import edu.sanchezContentoPablo.junit6.SaldoInsuficienteException;
import edu.sanchezContentoPablo.junit6.TipoMovimiento;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * <h2>Tema 1 · Aserciones</h2>
 *
 * <p>Una aserción comprueba que algo es cierto. Si no lo es, el test falla con
 * un mensaje que explica la diferencia entre lo esperado y lo obtenido.</p>
 *
 * <p>Regla de oro: <b>esperado primero, obtenido después</b>
 * ({@code assertEquals(esperado, real)}).</p>
 */
@DisplayName("Tema 1 · Aserciones")
class Tema01AsercionesTest {

    @Test
    @DisplayName("assertEquals compara valores; con BigDecimal usa la misma escala")
    void assertEqualsBasico() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("100"), "Nómina");

        // BigDecimal("100.00") no es equals a BigDecimal("100"): la escala importa.
        assertEquals(new BigDecimal("100.00"), cuenta.getSaldo());
        // El tercer parámetro es el mensaje que se muestra si falla.
        assertEquals("Ana", cuenta.getTitular(), "el titular no coincide");
    }

    @Test
    @DisplayName("assertTrue / assertFalse para condiciones booleanas")
    void booleanos() {
        assertTrue(TipoMovimiento.INGRESO.sumaAlSaldo());
        assertFalse(TipoMovimiento.RETIRADA.sumaAlSaldo());
    }

    @Test
    @DisplayName("assertAll ejecuta todas las comprobaciones aunque falle alguna")
    void assertAllAgrupa() {
        CuentaBancaria cuenta = new CuentaBancaria("  Luis  ");

        // Sin assertAll, el primer fallo detendría el test y no veríamos los demás.
        assertAll("cuenta recién abierta",
                () -> assertEquals("Luis", cuenta.getTitular()),
                () -> assertEquals(new BigDecimal("0.00"), cuenta.getSaldo()),
                () -> assertTrue(cuenta.getMovimientos().isEmpty()));
    }

    @Test
    @DisplayName("assertThrows devuelve la excepción para inspeccionarla")
    void assertThrowsDevuelveExcepcion() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("50"), "Regalo");

        SaldoInsuficienteException e = assertThrows(SaldoInsuficienteException.class,
                () -> cuenta.retirar(new BigDecimal("80"), "Zapatillas"));

        assertEquals(new BigDecimal("50.00"), e.getDisponible());
        assertTrue(e.getMessage().contains("80.00"));
    }

    @Test
    @DisplayName("assertDoesNotThrow deja claro que el caso debe funcionar")
    void assertDoesNotThrowExplicito() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("10"), "Ingreso");
        assertDoesNotThrow(() -> cuenta.retirar(new BigDecimal("10"), "Todo el saldo"));
    }

    @Test
    @DisplayName("assertIterableEquals compara colecciones elemento a elemento")
    void colecciones() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("20"), "Uno");
        cuenta.retirar(new BigDecimal("5"), "Dos");

        List<TipoMovimiento> tipos = cuenta.getMovimientos().stream().map(Movimiento::tipo).toList();
        assertIterableEquals(List.of(TipoMovimiento.INGRESO, TipoMovimiento.RETIRADA), tipos);
    }

    @Test
    @DisplayName("assertSame, assertNotNull y assertInstanceOf")
    void identidadYTipos() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        CuentaBancaria mismaReferencia = cuenta;

        assertSame(cuenta, mismaReferencia);   // misma instancia (==), no solo equals
        assertNotNull(cuenta.getMovimientos());

        Exception e = assertThrows(Exception.class, () -> cuenta.retirar(BigDecimal.ONE, "x"));
        // assertInstanceOf comprueba el tipo y devuelve el objeto ya convertido.
        SaldoInsuficienteException saldo = assertInstanceOf(SaldoInsuficienteException.class, e);
        assertEquals(new BigDecimal("0.00"), saldo.getDisponible());
    }
}
