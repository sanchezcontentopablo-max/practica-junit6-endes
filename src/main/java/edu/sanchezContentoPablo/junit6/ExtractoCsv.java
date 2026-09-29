package edu.sanchezContentoPablo.junit6;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Exporta los movimientos de una cuenta a un fichero CSV.
 *
 * <p>Formato: una cabecera {@code tipo;importe;concepto} y una línea por
 * movimiento, separadas por punto y coma para que Excel en español lo abra bien.</p>
 */
public final class ExtractoCsv {

    /** Cabecera del fichero. */
    public static final String CABECERA = "tipo;importe;concepto";

    private ExtractoCsv() {
    }

    /**
     * Escribe el extracto de la cuenta en el fichero indicado, sobrescribiéndolo.
     *
     * @param cuenta  cuenta a exportar
     * @param destino ruta del fichero CSV
     * @return la ruta escrita
     * @throws IOException si no se puede escribir el fichero
     */
    public static Path exportar(CuentaBancaria cuenta, Path destino) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add(CABECERA);
        for (Movimiento m : cuenta.getMovimientos()) {
            String concepto = m.concepto().replace(";", ",");
            lineas.add(m.tipo() + ";" + m.importe().toPlainString() + ";" + concepto);
        }
        return Files.write(destino, lineas, StandardCharsets.UTF_8);
    }
}
