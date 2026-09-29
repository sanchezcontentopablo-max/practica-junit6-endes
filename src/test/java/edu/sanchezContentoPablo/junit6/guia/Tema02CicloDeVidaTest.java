package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * <h2>Tema 2 · Ciclo de vida</h2>
 *
 * <pre>
 * &#64;BeforeAll   una vez, antes de todos los tests (método static)
 *   &#64;BeforeEach  antes de CADA test
 *     &#64;Test
 *   &#64;AfterEach   después de CADA test
 * &#64;AfterAll    una vez, al final (método static)
 * </pre>
 *
 * <p>JUnit crea <b>una instancia nueva de la clase por cada test</b>, así que los
 * atributos no se comparten entre tests: cada uno empieza limpio.</p>
 *
 * <p>Los tests no deberían depender del orden; aquí se fija con
 * {@link TestMethodOrder} solo para poder comprobar la secuencia.</p>
 */
@DisplayName("Tema 2 · Ciclo de vida")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Tema02CicloDeVidaTest {

    /** Estático: se comparte entre todas las instancias para registrar la secuencia. */
    private static final List<String> TRAZA = new ArrayList<>();

    private CuentaBancaria cuenta;

    @BeforeAll
    static void antesDeTodos() {
        TRAZA.add("beforeAll");
    }

    @BeforeEach
    void antesDeCadaTest(TestInfo info) {
        // TestInfo se inyecta como parámetro y da el nombre del test en curso.
        TRAZA.add("beforeEach:" + info.getTestMethod().orElseThrow().getName());
        cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("100"), "Saldo inicial");
    }

    @AfterEach
    void despuesDeCadaTest() {
        TRAZA.add("afterEach");
    }

    @AfterAll
    static void despuesDeTodos() {
        TRAZA.add("afterAll");
        // Útil para cerrar recursos compartidos (conexiones, servidores...).
    }

    @Test
    @Order(1)
    @DisplayName("cada test recibe una cuenta recién preparada")
    void primerTest() {
        cuenta.retirar(new BigDecimal("100"), "Vaciar la cuenta");
        assertEquals(new BigDecimal("0.00"), cuenta.getSaldo());
    }

    @Test
    @Order(2)
    @DisplayName("lo que hizo el test anterior no afecta a este")
    void segundoTest() {
        // El test 1 vació la cuenta, pero @BeforeEach creó otra con 100 €.
        assertEquals(new BigDecimal("100.00"), cuenta.getSaldo());
    }

    @Test
    @Order(3)
    @DisplayName("la traza confirma el orden de ejecución")
    void traza() {
        assertEquals(List.of(
                "beforeAll",
                "beforeEach:primerTest", "afterEach",
                "beforeEach:segundoTest", "afterEach",
                "beforeEach:traza"), TRAZA);
    }
}
