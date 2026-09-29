package edu.sanchezContentoPablo.junit6.guia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.sanchezContentoPablo.junit6.CuentaBancaria;
import edu.sanchezContentoPablo.junit6.ExtractoCsv;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * <h2>Tema 7 · Ficheros temporales con {@code @TempDir}</h2>
 *
 * <p>Los tests que escriben ficheros no deben ensuciar el proyecto ni depender
 * de rutas de un ordenador concreto. {@link TempDir} crea una carpeta temporal
 * nueva para cada test y la <b>borra al terminar</b>.</p>
 */
@DisplayName("Tema 7 · Ficheros temporales")
class Tema07FicherosTemporalesTest {

    @TempDir
    Path carpeta;

    @Test
    @DisplayName("exporta el extracto con cabecera y una línea por movimiento")
    void exportaCsv() throws IOException {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("100"), "Nómina");
        cuenta.retirar(new BigDecimal("25.5"), "Supermercado");

        Path csv = ExtractoCsv.exportar(cuenta, carpeta.resolve("extracto.csv"));

        assertTrue(Files.exists(csv));
        assertEquals(List.of(
                "tipo;importe;concepto",
                "INGRESO;100.00;Nómina",
                "RETIRADA;25.50;Supermercado"), Files.readAllLines(csv));
    }

    @Test
    @DisplayName("un ';' en el concepto no rompe las columnas")
    void escapaSeparador() throws IOException {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.ingresar(new BigDecimal("5"), "Bizum; cena");

        Path csv = ExtractoCsv.exportar(cuenta, carpeta.resolve("extracto.csv"));

        assertEquals("INGRESO;5.00;Bizum, cena", Files.readAllLines(csv).get(1));
    }

    @Test
    @DisplayName("una cuenta sin movimientos genera solo la cabecera")
    void soloCabecera(@TempDir Path otraCarpeta) throws IOException {
        // @TempDir también se puede recibir como parámetro del método.
        Path csv = ExtractoCsv.exportar(new CuentaBancaria("Ana"), otraCarpeta.resolve("vacio.csv"));
        assertEquals(List.of(ExtractoCsv.CABECERA), Files.readAllLines(csv));
    }

    @Test
    @DisplayName("si la carpeta de destino no existe, lanza IOException")
    void carpetaInexistente() {
        Path destino = carpeta.resolve("no-existe").resolve("extracto.csv");
        assertThrows(IOException.class, () -> ExtractoCsv.exportar(new CuentaBancaria("Ana"), destino));
    }
}
