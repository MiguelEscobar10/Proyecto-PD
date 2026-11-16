package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Conexión con la base de conocimiento en SWI-Prolog.
 *
 * Copia integrales.pl (que está en resources) a un archivo temporal y,
 * en cada consulta, lanza "swipl" con la función y los límites como
 * argumentos de texto. Prolog responde por pantalla y aquí se interpreta.
 *
 * Requisito: SWI-Prolog instalado. Si "swipl" no está en el PATH, se puede
 * indicar la ruta completa con la variable de entorno SWIPL_PATH
 * o con -Dswipl.path="C:\Program Files\swipl\bin\swipl.exe".
 */
public class Connection {

    /** Resultado de una integral definida, con cada paso en texto. */
    public record Resultado(String primitiva, String valorA, String valorB, String resultado) {
    }

    /** Error con un mensaje listo para mostrarse al usuario. */
    public static class ErrorConsulta extends Exception {
        public ErrorConsulta(String mensaje) {
            super(mensaje);
        }
    }

    private final String swipl;
    private final Path archivoProlog;

    public Connection() throws IOException {
        String ruta = System.getProperty("swipl.path");
        if (ruta == null || ruta.isBlank()) ruta = System.getenv("SWIPL_PATH");
        if (ruta == null || ruta.isBlank()) ruta = "swipl";
        this.swipl = ruta;
        this.archivoProlog = extraerBaseDeConocimiento();
    }

    private Path extraerBaseDeConocimiento() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/integrales.pl")) {
            if (in == null) throw new IllegalStateException("No se encontró integrales.pl en resources");
            Path temporal = Files.createTempFile("integrales", ".pl");
            Files.copy(in, temporal, StandardCopyOption.REPLACE_EXISTING);
            temporal.toFile().deleteOnExit();
            return temporal;
        }
    }

    /**
     * Calcula la integral definida de la función entre a y b.
     *
     * @param funcion texto como "x^2 + 4" o "3*sin(x)"
     * @param a       límite inferior, por ejemplo "0" o "pi/2"
     * @param b       límite superior
     */
    public Resultado definida(String funcion, String a, String b) throws ErrorConsulta {
        String salida = ejecutar(funcion, a, b);
        List<String> lineas = salida.lines()
                .map(String::trim)
                .filter(linea -> !linea.isEmpty())
                .toList();

        if (lineas.isEmpty()) {
            throw new ErrorConsulta("SWI-Prolog no devolvió ninguna respuesta.");
        }

        switch (lineas.get(0)) {
            case "OK":
                if (lineas.size() < 5) throw new ErrorConsulta("Respuesta incompleta de SWI-Prolog.");
                return new Resultado(lineas.get(1), lineas.get(2), lineas.get(3), lineas.get(4));
            case "SIN_SOLUCION":
                throw new ErrorConsulta("""
                        No se pudo resolver esa función con las reglas disponibles.
                        Usa: constantes, x, x^n (n entero distinto de -1), k*función, sumas y restas,
                        a^x, sin(x), cos(x), tan(x), sinh(x) y cosh(x).""");
            case "ERROR":
                String tipo = lineas.size() > 1 ? lineas.get(1) : "otro";
                throw new ErrorConsulta(mensajeDeError(tipo));
            default:
                throw new ErrorConsulta("Respuesta inesperada de SWI-Prolog: " + lineas.get(0));
        }
    }

    private String mensajeDeError(String tipo) {
        return switch (tipo) {
            case "sintaxis" -> "No se pudo leer la expresión. Revisa los paréntesis y escribe el producto "
                    + "con * (por ejemplo 3*x, no 3x).";
            case "evaluacion" -> "No se pudo calcular el resultado. Revisa que los límites sean números "
                    + "o expresiones como pi, pi/2 o 2*pi.";
            case "discontinuidad" -> "La función no es continua en ese intervalo (tan(x) en pi/2 + k·pi, "
                    + "o x^n con n negativo en x = 0), así que la integral no se puede calcular.";
            default -> "Los datos no son válidos.";
        };
    }

    /** Lanza swipl con los argumentos y devuelve lo que Prolog escribió por pantalla. */
    private String ejecutar(String... argumentos) throws ErrorConsulta {
        List<String> comando = new ArrayList<>(List.of(
                swipl, "-q", "-g", "consultar_cli", "-t", "halt", archivoProlog.toString(), "--"));
        comando.addAll(Arrays.asList(argumentos));

        try {
            Process proceso = new ProcessBuilder(comando)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            proceso.getOutputStream().close();

            if (!proceso.waitFor(15, TimeUnit.SECONDS)) {
                proceso.destroyForcibly();
                throw new ErrorConsulta("SWI-Prolog tardó demasiado en responder.");
            }
            return new String(proceso.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new ErrorConsulta("No se pudo ejecutar SWI-Prolog ('" + swipl + "').\n"
                    + "Instálalo y agrégalo al PATH, o define la variable de entorno SWIPL_PATH "
                    + "con la ruta completa de swipl.exe.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ErrorConsulta("La consulta fue interrumpida.");
        }
    }
}
