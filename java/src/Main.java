import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Scanner ENTRADA = new Scanner(System.in);

    // Pide un texto y no acepta vacíos ni comas (rompen el formato del .txt)
    private static String pedirTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = ENTRADA.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("Error: este campo no puede estar vacío.");
            } else if (valor.contains(",")) {
                System.out.println("Error: no use comas en este campo.");
            } else {
                return valor;
            }
        }
    }

    // Pide un número y repite hasta que sea válido
    private static double pedirNumero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(ENTRADA.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numérico válido");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("=".repeat(50));
        System.out.println("SGA-DO: SISTEMA DIPLOMADOSONLINE");
        System.out.println("=".repeat(50));
        System.out.println("1. Registrar Alumno");
        System.out.println("2. Registrar Profesor");
        System.out.println("3. Registrar Notas a un Alumno");
        System.out.println("4. Deshacer Último Registro de Nota");
        System.out.println("5. Generar Cola de Certificados");
        System.out.println("6. Mostrar Reporte General");
        System.out.println("7. Salir");
        System.out.println("=".repeat(50));
    }

    private static void registrarAlumno(Repositorio repo) {
        String cedula = pedirTexto("Cédula: ");
        if (repo.existeCedula(cedula)) {
            System.out.println("Error: ya existe una persona con esa cédula.");
            return;
        }
        String nombre = pedirTexto("Nombre completo: ");
        String correo = pedirTexto("Correo: ");
        String tipo = pedirTexto("Tipo de programa (Curso, Diplomado o Bootcamp): ");
        try {
            repo.registrarAlumno(new Alumno(cedula, nombre, correo, tipo));
            System.out.println("Alumno registrado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarProfesor(Repositorio repo) {
        String cedula = pedirTexto("Cédula: ");
        if (repo.existeCedula(cedula)) {
            System.out.println("Error: ya existe una persona con esa cédula.");
            return;
        }
        String nombre = pedirTexto("Nombre completo: ");
        String correo = pedirTexto("Correo: ");
        String especialidad = pedirTexto("Especialidad: ");
        String materia = pedirTexto("Materia asignada: ");
        try {
            repo.registrarProfesor(new Profesor(cedula, nombre, correo, especialidad, materia));
            System.out.println("Profesor registrado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarNota(Repositorio repo, HistorialNotas historial) {
        String cedula = pedirTexto("Cédula del alumno: ");
        Alumno alumno = repo.buscarAlumno(cedula);
        if (alumno == null) {
            System.out.println("Error: no existe un alumno con esa cédula.");
            return;
        }
        System.out.println("Alumno: " + alumno.getNombre() + " (" + alumno.getPrograma().getNombre() + ")");
        System.out.println("Notas actuales: " + alumno.getNotas());
        double nota = pedirNumero("Nota: ");
        historial.registrarNota(cedula, nota);
        System.out.println("Nota registrada correctamente.");
    }

    private static void deshacer(HistorialNotas historial) {
        HistorialNotas.Deshecho resultado = historial.deshacer();
        if (resultado == null) {
            System.out.println("No hay notas para deshacer.");
        } else {
            System.out.println("Se deshizo la nota " + Alumno.formatearNota(resultado.nota())
                    + " del alumno " + resultado.cedula() + ".");
        }
    }

    private static void colaCertificados(Repositorio repo) {
        int total = ServicioCertificados.generarCertificados(repo);
        System.out.println("Cola generada: " + total + " graduando(s). Archivo: certificados_pendientes.txt");
    }

    private static void reporteGeneral() {
        // Se relee desde los archivos, como pide el enunciado
        Repositorio lector = new Repositorio();
        System.out.println("=".repeat(50));
        System.out.println("REPORTE GENERAL");
        System.out.println("=".repeat(50));
        System.out.println("PROFESORES ACTIVOS:");
        List<Profesor> profesores = lector.todosLosProfesores();
        if (profesores.isEmpty()) {
            System.out.println("  (sin profesores registrados)");
        }
        for (Profesor p : profesores) {
            System.out.println("  [" + p.getCedula() + "] " + p.getNombre() + " - "
                    + p.getEspecialidad() + " - " + p.getMateria());
        }
        System.out.println("-".repeat(50));
        System.out.println("ALUMNOS REGISTRADOS:");
        List<Alumno> alumnos = lector.todosLosAlumnos();
        if (alumnos.isEmpty()) {
            System.out.println("  (sin alumnos registrados)");
        }
        for (Alumno a : alumnos) {
            String estatus = a.estaAprobado() ? "APROBADO" : "REPROBADO";
            System.out.println("  [" + a.getCedula() + "] " + a.getNombre() + " - " + a.getPrograma().getNombre());
            System.out.println("      Notas: " + a.getNotas() + " | Promedio: "
                    + String.format(Locale.US, "%.1f", a.promedio()) + " | " + estatus);
        }
        System.out.println("=".repeat(50));
    }

    public static void main(String[] args) {
        Repositorio repo = new Repositorio();
        HistorialNotas historial = new HistorialNotas(repo);

        while (true) {
            mostrarMenu();
            System.out.print("Seleccione una opción (1-7): ");
            int opcion;
            try {
                opcion = Integer.parseInt(ENTRADA.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numérico válido");
                continue;
            }

            switch (opcion) {
                case 1 -> registrarAlumno(repo);
                case 2 -> registrarProfesor(repo);
                case 3 -> registrarNota(repo, historial);
                case 4 -> deshacer(historial);
                case 5 -> colaCertificados(repo);
                case 6 -> reporteGeneral();
                case 7 -> {
                    repo.guardarCambiosAlumnos();
                    System.out.println("Datos guardados. Hasta pronto.");
                    return;
                }
                default -> System.out.println("Error: elija una opción entre 1 y 7.");
            }
        }
    }
}
