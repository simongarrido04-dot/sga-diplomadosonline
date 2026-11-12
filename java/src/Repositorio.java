import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Repositorio {
    public static final Path ARCHIVO_ALUMNOS = Paths.get("alumnos.txt");
    public static final Path ARCHIVO_PROFESORES = Paths.get("profesores.txt");
    public static final Path ARCHIVO_CERTIFICADOS = Paths.get("certificados_pendientes.txt");

    private Map<String, Alumno> alumnos;      // cedula -> Alumno
    private Map<String, Profesor> profesores; // cedula -> Profesor

    public Repositorio() {
        alumnos = leer(ARCHIVO_ALUMNOS, Alumno::desdeLinea);
        profesores = leer(ARCHIVO_PROFESORES, Profesor::desdeLinea);
    }

    // ---------- Lectura ----------
    private static <T extends Persona> Map<String, T> leer(Path ruta, Function<String, T> constructor) {
        Map<String, T> datos = new LinkedHashMap<>();
        if (!Files.exists(ruta)) {
            return datos;
        }
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                try {
                    T obj = constructor.apply(linea);
                    datos.put(obj.getCedula(), obj);
                } catch (IllegalArgumentException e) {
                    System.out.println("Aviso: línea ignorada por formato inválido: " + linea.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer " + ruta + ": " + e.getMessage());
        }
        return datos;
    }

    // ---------- Escritura ----------
    private void guardarAlumnos() {
        try (BufferedWriter escritor = Files.newBufferedWriter(ARCHIVO_ALUMNOS, StandardCharsets.UTF_8)) {
            for (Alumno a : alumnos.values()) {
                escritor.write(a.aLinea());
                escritor.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo guardar " + ARCHIVO_ALUMNOS + ": " + e.getMessage());
        }
    }

    private void guardarProfesores() {
        try (BufferedWriter escritor = Files.newBufferedWriter(ARCHIVO_PROFESORES, StandardCharsets.UTF_8)) {
            for (Profesor p : profesores.values()) {
                escritor.write(p.aLinea());
                escritor.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo guardar " + ARCHIVO_PROFESORES + ": " + e.getMessage());
        }
    }

    // ---------- Operaciones ----------
    public boolean existeCedula(String cedula) {
        return alumnos.containsKey(cedula) || profesores.containsKey(cedula);
    }

    public void registrarAlumno(Alumno alumno) {
        if (existeCedula(alumno.getCedula())) {
            throw new IllegalArgumentException("Ya existe una persona con esa cédula");
        }
        alumnos.put(alumno.getCedula(), alumno);
        guardarAlumnos(); // se escribe de inmediato
    }

    public void registrarProfesor(Profesor profesor) {
        if (existeCedula(profesor.getCedula())) {
            throw new IllegalArgumentException("Ya existe una persona con esa cédula");
        }
        profesores.put(profesor.getCedula(), profesor);
        guardarProfesores(); // se escribe de inmediato
    }

    public Alumno buscarAlumno(String cedula) {
        return alumnos.get(cedula);
    }

    // Llamar después de agregar o quitar una nota
    public void guardarCambiosAlumnos() {
        guardarAlumnos();
    }

    public List<Alumno> todosLosAlumnos() {
        return new ArrayList<>(alumnos.values());
    }

    public List<Profesor> todosLosProfesores() {
        return new ArrayList<>(profesores.values());
    }
}
