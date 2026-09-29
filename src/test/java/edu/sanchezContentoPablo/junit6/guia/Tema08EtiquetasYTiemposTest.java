package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * <h2>Tema 8 · Etiquetas y límites de tiempo</h2>
 *
 * <p>{@link Tag} clasifica los tests. En el {@code pom.xml} se excluyen los
 * etiquetados como {@code lento}; para ejecutarlos todos:</p>
 *
 * <pre>mvn test -Ptodos</pre>
 *
 * <p>o solo los de una etiqueta: {@code mvn test -Dgroups=rapido}.</p>
 *
 * <p>{@link Timeout} y {@code assertTimeout} hacen fallar un test que tarda
 * demasiado, útil para detectar bucles infinitos o regresiones de rendimiento.</p>
 */
@DisplayName("Tema 8 · Etiquetas y tiempos")
class Tema08EtiquetasYTiemposTest {

    @Test
    @Tag("rapido")
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    @DisplayName("@Timeout: 10 000 ingresos en menos de medio segundo")
    void muchosIngresos() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        for (int i = 0; i < 10_000; i++) {
            cuenta.ingresar(BigDecimal.ONE, "Ingreso " + i);
        }
        assertEquals(new BigDecimal("10000.00"), cuenta.getSaldo());
    }

    @Test
    @Tag("rapido")
    @DisplayName("assertTimeout: limita solo un bloque del test")
    void bloqueConLimite() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        BigDecimal saldo = assertTimeout(Duration.ofMillis(200), () -> {
            cuenta.ingresar(new BigDecimal("42"), "Rápido");
            return cuenta.getSaldo();
        });
        assertEquals(new BigDecimal("42.00"), saldo);
    }

    @Test
    @Tag("lento")
    @DisplayName("@Tag(\"lento\"): un millón de movimientos (solo con -Ptodos)")
    void unMillonDeMovimientos() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        for (int i = 0; i < 1_000_000; i++) {
            cuenta.ingresar(BigDecimal.ONE, "");
        }
        assertEquals(1_000_000, cuenta.getMovimientos().size());
    }
}
