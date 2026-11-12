import java.util.ArrayDeque;
import java.util.Deque;

public class HistorialNotas {

    // Resultado del deshacer: qué alumno y qué nota se quitó
    public record Deshecho(String cedula, double nota) {
    }

    private final Repositorio repo;
    private final Deque<String> pila = new ArrayDeque<>(); // guarda la cédula de cada nota agregada

    public HistorialNotas(Repositorio repo) {
        this.repo = repo;
    }

    public void registrarNota(String cedula, double nota) {
        Alumno alumno = repo.buscarAlumno(cedula);
        if (alumno == null) {
            throw new IllegalArgumentException("No existe un alumno con esa cédula");
        }
        alumno.agregarNota(nota);
        repo.guardarCambiosAlumnos();
        pila.push(cedula); // push
    }

    // Quita la última nota ingresada (LIFO). Devuelve null si no hay nada que deshacer
    public Deshecho deshacer() {
        if (pila.isEmpty()) {
            return null;
        }
        String cedula = pila.pop(); // pop: la última en entrar
        Alumno alumno = repo.buscarAlumno(cedula);
        Double nota = alumno.quitarUltimaNota();
        repo.guardarCambiosAlumnos();
        if (nota == null) {
            return null;
        }
        return new Deshecho(cedula, nota);
    }
}
