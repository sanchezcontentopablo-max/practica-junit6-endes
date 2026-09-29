package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

/**
 * <h2>Tema 6 · Asunciones y ejecución condicional</h2>
 *
 * <p>Una <b>aserción</b> que falla marca el test como <em>fallido</em>.
 * Una <b>asunción</b> que no se cumple lo marca como <em>abortado</em>
 * (ni pasa ni falla): el test no tiene sentido en ese entorno.</p>
 *
 * <p>Las anotaciones {@code @EnabledOn...} / {@code @DisabledIf...} deciden
 * antes de ejecutar si el test se lanza o se salta.</p>
 */
@DisplayName("Tema 6 · Asunciones y condiciones")
class Tema06AsuncionesYCondicionesTest {

    @Test
    @DisplayName("assumeTrue: solo se ejecuta si el separador de rutas es conocido")
    void asuncion() {
        String separador = FileSystems.getDefault().getSeparator();
        assumeTrue(separador.equals("/") || separador.equals("\\"), "sistema de ficheros no soportado");
        assertTrue(separador.length() == 1);
    }

    @Test
    @DisplayName("assumingThat: parte del test solo se ejecuta si se cumple la condición")
    void asuncionParcial() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("10"), "Inicial");

        assumingThat(System.getenv("CI") != null,
                () -> assertEquals("Ana", cuenta.getTitular()));   // solo en integración continua

        assertEquals(new BigDecimal("10.00"), cuenta.getSaldo());   // siempre
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("@EnabledOnOs: solo en Windows")
    void soloWindows() {
        assertEquals("\\", FileSystems.getDefault().getSeparator());
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    @DisplayName("@EnabledOnOs: solo en Linux o macOS")
    void soloUnix() {
        assertEquals("/", FileSystems.getDefault().getSeparator());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_21)
    @DisplayName("@EnabledForJreRange: requiere Java 21 o superior")
    void soloJava21() {
        // List.getLast() existe desde Java 21.
        assertEquals(3, java.util.List.of(1, 2, 3).getLast());
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "SIN_RED", matches = "true")
    @DisplayName("@DisabledIfEnvironmentVariable: se salta si SIN_RED=true")
    void dependeDeVariable() {
        assertTrue(true, "aquí iría un test que necesita red");
    }

    @Test
    @Disabled("Ejemplo de test desactivado: explica siempre el motivo")
    @DisplayName("@Disabled: nunca se ejecuta")
    void desactivado() {
        throw new AssertionError("No debería ejecutarse");
    }
}
