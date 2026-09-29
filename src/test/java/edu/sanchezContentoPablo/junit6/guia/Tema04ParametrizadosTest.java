package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import edu.sanchezContentoPablo.junit6.Movimiento;
import edu.sanchezContentoPablo.junit6.TipoMovimiento;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * <h2>Tema 4 · Tests parametrizados</h2>
 *
 * <p>Un {@link ParameterizedTest} ejecuta el mismo método con distintos datos.
 * Cada fila aparece como un test independiente en el informe.</p>
 *
 * <table>
 *   <caption>Fuentes de datos</caption>
 *   <tr><th>Anotación</th><th>Cuándo usarla</th></tr>
 *   <tr><td>{@code @ValueSource}</td><td>un único parámetro de tipo simple</td></tr>
 *   <tr><td>{@code @CsvSource}</td><td>varios parámetros escritos en línea</td></tr>
 *   <tr><td>{@code @CsvFileSource}</td><td>muchos casos en un fichero CSV</td></tr>
 *   <tr><td>{@code @EnumSource}</td><td>todos (o algunos) valores de un enum</td></tr>
 *   <tr><td>{@code @MethodSource}</td><td>objetos complejos creados en código</td></tr>
 *   <tr><td>{@code @NullAndEmptySource}</td><td>añadir {@code null} y "" a los casos</td></tr>
 * </table>
 */
@DisplayName("Tema 4 · Tests parametrizados")
class Tema04ParametrizadosTest {

    @ParameterizedTest(name = "ingresar {0} € deja el saldo en {0} €")
    @ValueSource(strings = {"0.01", "1", "99.99", "1000000"})
    @DisplayName("@ValueSource: importes válidos")
    void valueSource(String importe) {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal(importe), "Ingreso");
        assertEquals(0, new BigDecimal(importe).compareTo(cuenta.getSaldo()));
    }

    @ParameterizedTest(name = "[{index}] {0} € se guarda como {1} €")
    @CsvSource({
            "10,      10.00",
            "10.5,    10.50",
            "0.125,   0.12",   // redondeo bancario: 0.125 -> 0.12
            "0.135,   0.14"    // redondeo bancario: 0.135 -> 0.14
    })
    @DisplayName("@CsvSource: redondeo a dos decimales")
    void csvSource(String importe, String esperado) {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal(importe), "Ingreso");
        assertEquals(new BigDecimal(esperado), cuenta.getSaldo());
    }

    @ParameterizedTest(name = "saldo {0} - retirada {1} = {2}")
    @CsvFileSource(resources = "/retiradas.csv", numLinesToSkip = 1)
    @DisplayName("@CsvFileSource: casos leídos de src/test/resources/retiradas.csv")
    void csvFileSource(String saldoInicial, String retirada, String saldoFinal) {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal(saldoInicial), "Inicial");
        cuenta.retirar(new BigDecimal(retirada), "Retirada");
        assertEquals(new BigDecimal(saldoFinal), cuenta.getSaldo());
    }

    @ParameterizedTest(name = "{0} suma al saldo")
    @EnumSource(value = TipoMovimiento.class, names = {"INGRESO", "TRANSFERENCIA_RECIBIDA"})
    @DisplayName("@EnumSource: tipos que suman")
    void enumSourceSuman(TipoMovimiento tipo) {
        Movimiento m = new Movimiento(tipo, BigDecimal.TEN, "x");
        assertEquals(BigDecimal.TEN, m.importeConSigno());
    }

    @ParameterizedTest(name = "{0} resta del saldo")
    @EnumSource(value = TipoMovimiento.class, mode = EnumSource.Mode.EXCLUDE,
            names = {"INGRESO", "TRANSFERENCIA_RECIBIDA"})
    @DisplayName("@EnumSource con EXCLUDE: tipos que restan")
    void enumSourceRestan(TipoMovimiento tipo) {
        Movimiento m = new Movimiento(tipo, BigDecimal.TEN, "x");
        assertEquals(BigDecimal.TEN.negate(), m.importeConSigno());
    }

    static Stream<Arguments> importesInvalidos() {
        return Stream.of(
                Arguments.of(BigDecimal.ZERO, "cero"),
                Arguments.of(new BigDecimal("-0.01"), "negativo pequeño"),
                Arguments.of(new BigDecimal("-500"), "negativo grande"));
    }

    @ParameterizedTest(name = "{1}: {0}")
    @MethodSource("importesInvalidos")
    @DisplayName("@MethodSource: importes que se rechazan")
    void methodSource(BigDecimal importe, String descripcion) {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        assertThrows(IllegalArgumentException.class, () -> cuenta.ingresar(importe, descripcion));
    }

    @ParameterizedTest(name = "titular [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    @DisplayName("@NullAndEmptySource: titulares no válidos")
    void nullAndEmpty(String titular) {
        assertThrows(IllegalArgumentException.class, () -> new CuentaBancaria(titular));
    }
}
