package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import edu.sanchezContentoPablo.junit6.SaldoInsuficienteException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <h2>Tema 3 · Tests anidados y nombres legibles</h2>
 *
 * <p>{@link Nested} permite agrupar tests por <em>situación</em>, al estilo
 * "Dado… cuando… entonces". Cada clase interna puede tener su propio
 * {@code @BeforeEach}, que se ejecuta <b>después</b> del de la clase exterior.</p>
 *
 * <p>{@link DisplayNameGeneration} con {@code ReplaceUnderscores} convierte
 * {@code retirar_todo_deja_el_saldo_a_cero} en "retirar todo deja el saldo a cero".</p>
 */
@DisplayName("Tema 3 · Tests anidados")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Tema03AnidadosTest {

    private CuentaBancaria cuenta;

    @BeforeEach
    void abrirCuenta() {
        cuenta = new CuentaBancaria("Ana");
    }

    @Test
    void una_cuenta_nueva_no_tiene_movimientos() {
        assertTrue(cuenta.getMovimientos().isEmpty());
    }

    @Nested
    class Dada_una_cuenta_vacia {

        @Test
        void no_se_puede_retirar_dinero() {
            assertThrows(SaldoInsuficienteException.class,
                    () -> cuenta.retirar(new BigDecimal("1"), "Café"));
        }

        @Test
        void no_se_puede_transferir() {
            CuentaBancaria destino = new CuentaBancaria("Luis");
            assertThrows(SaldoInsuficienteException.class,
                    () -> cuenta.transferir(destino, new BigDecimal("1")));
        }
    }

    @Nested
    class Dada_una_cuenta_con_100_euros {

        @BeforeEach
        void ingresar100() {
            // Se ejecuta después de abrirCuenta() de la clase exterior.
            cuenta.ingresar(new BigDecimal("100"), "Nómina");
        }

        @Test
        void retirar_todo_deja_el_saldo_a_cero() {
            cuenta.retirar(new BigDecimal("100"), "Alquiler");
            assertEquals(new BigDecimal("0.00"), cuenta.getSaldo());
        }

        @Test
        void retirar_mas_del_saldo_no_modifica_la_cuenta() {
            assertThrows(SaldoInsuficienteException.class,
                    () -> cuenta.retirar(new BigDecimal("100.01"), "Demasiado"));
            assertEquals(new BigDecimal("100.00"), cuenta.getSaldo());
            assertEquals(1, cuenta.getMovimientos().size());
        }

        @Nested
        class Cuando_transfiere_30_euros_a_otra_cuenta {

            private final CuentaBancaria destino = new CuentaBancaria("Luis");

            @BeforeEach
            void transferir() {
                cuenta.transferir(destino, new BigDecimal("30"));
            }

            @Test
            void el_origen_queda_con_70() {
                assertEquals(new BigDecimal("70.00"), cuenta.getSaldo());
            }

            @Test
            void el_destino_recibe_30() {
                assertEquals(new BigDecimal("30.00"), destino.getSaldo());
            }

            @Test
            void ambos_conceptos_indican_la_otra_parte() {
                assertEquals("A Luis", cuenta.getMovimientos().getLast().concepto());
                assertEquals("De Ana", destino.getMovimientos().getLast().concepto());
            }
        }
    }
}
