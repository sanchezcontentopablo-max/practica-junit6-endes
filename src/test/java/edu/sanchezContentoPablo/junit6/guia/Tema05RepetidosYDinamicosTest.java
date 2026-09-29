package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestFactory;

/**
 * <h2>Tema 5 · Tests repetidos y dinámicos</h2>
 *
 * <ul>
 *   <li>{@link RepeatedTest}: ejecuta el mismo test N veces. Útil con datos
 *       aleatorios para descubrir casos que no se nos habían ocurrido.</li>
 *   <li>{@link TestFactory}: genera los tests <em>en tiempo de ejecución</em>.
 *       Útil cuando los casos salen de un cálculo o de una lista que cambia.</li>
 * </ul>
 */
@DisplayName("Tema 5 · Tests repetidos y dinámicos")
class Tema05RepetidosYDinamicosTest {

    @RepeatedTest(value = 20, name = "ronda {currentRepetition} de {totalRepetitions}")
    @DisplayName("ingresar y retirar la misma cantidad deja el saldo igual")
    void propiedadIngresoRetirada(RepetitionInfo info) {
        // Semilla fija por repetición: aleatorio pero reproducible si falla.
        Random random = new Random(info.getCurrentRepetition());
        BigDecimal importe = BigDecimal.valueOf(1 + random.nextInt(100_000), 2);

        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("50"), "Inicial");
        cuenta.ingresar(importe, "Entrada");
        cuenta.retirar(importe, "Salida");

        assertEquals(new BigDecimal("50.00"), cuenta.getSaldo());
    }

    @TestFactory
    @DisplayName("un test por cada tramo de retirada")
    Stream<DynamicTest> testsGenerados() {
        List<String> importes = List.of("10", "25.50", "99.99", "100");
        return importes.stream().map(importe -> dynamicTest("retirar " + importe + " € de 100 €", () -> {
            CuentaBancaria cuenta = new CuentaBancaria("Ana");
            cuenta.ingresar(new BigDecimal("100"), "Inicial");
            cuenta.retirar(new BigDecimal(importe), "Retirada");
            assertEquals(new BigDecimal("100").subtract(new BigDecimal(importe)).setScale(2), cuenta.getSaldo());
        }));
    }

    @TestFactory
    @DisplayName("contenedores dinámicos: agrupan tests generados")
    Stream<DynamicNode> contenedores() {
        return Stream.of("Ana", "Luis").map(titular -> DynamicContainer.dynamicContainer("cuenta de " + titular,
                Stream.of(
                        dynamicTest("empieza a cero", () ->
                                assertEquals(new BigDecimal("0.00"), new CuentaBancaria(titular).getSaldo())),
                        dynamicTest("guarda el titular", () ->
                                assertEquals(titular, new CuentaBancaria(titular).getTitular())))));
    }
}
