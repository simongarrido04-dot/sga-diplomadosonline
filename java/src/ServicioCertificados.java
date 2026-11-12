import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Queue;

public class ServicioCertificados {

    private static void escribir(BufferedWriter w, String texto) throws IOException {
        w.write(texto);
        w.newLine();
    }

    // Mete a los aprobados en una COLA (FIFO) y la exporta al .txt
    public static int generarCertificados(Repositorio repo) {
        Queue<Alumno> cola = new ArrayDeque<>();
        for (Alumno alumno : repo.todosLosAlumnos()) {
            if (alumno.estaAprobado()) {
                cola.offer(alumno); // enqueue
            }
        }

        int total = cola.size();
        try (BufferedWriter w = Files.newBufferedWriter(Repositorio.ARCHIVO_CERTIFICADOS, StandardCharsets.UTF_8)) {
            escribir(w, "=".repeat(41));
            escribir(w, "REPORTE DE CERTIFICADOS PENDIENTES");
            escribir(w, "=".repeat(41));
            escribir(w, "Total de graduandos en cola: " + total);
            escribir(w, "");

            int posicion = 1;
            while (!cola.isEmpty()) {
                Alumno a = cola.poll(); // dequeue: el primero en entrar
                String regla = a.getPrograma().descripcionRegla();
                escribir(w, posicion + ". [" + a.getCedula() + "] " + a.getNombre());
                escribir(w, "   - Programa: " + a.getPrograma().getNombre());
                escribir(w, "   - Promedio Final: " + String.format(Locale.US, "%.1f", a.promedio()));
                escribir(w, "   - Estatus: APROBADO" + regla);
                escribir(w, "");
                posicion++;
            }

            escribir(w, "=".repeat(41));
            escribir(w, "* Fin del reporte - Generado por SGA-DO *");
        } catch (IOException e) {
            System.out.println("Error: no se pudo escribir el archivo de certificados: " + e.getMessage());
        }
        return total;
    }
}
