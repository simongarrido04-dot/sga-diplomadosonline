import java.util.ArrayList;
import java.util.List;

public class Alumno extends Persona {
    public static final int MAX_NOTAS = 3;

    private ProgramaAcademico programa;
    private List<Double> notas;

    public Alumno(String cedula, String nombre, String correo,
                  String tipoPrograma, List<Double> notas) {
        super(cedula, nombre, correo);
        this.programa = ProgramaAcademico.crearPrograma(tipoPrograma);
        this.notas = new ArrayList<>(notas);
    }

    public Alumno(String cedula, String nombre, String correo, String tipoPrograma) {
        this(cedula, nombre, correo, tipoPrograma, new ArrayList<>());
    }

    public ProgramaAcademico getPrograma() {
        return programa;
    }

    // Devuelve una copia para proteger la lista interna
    public List<Double> getNotas() {
        return new ArrayList<>(notas);
    }

    public void agregarNota(double nota) {
        notas.add(nota);
    }

    // La usa la Pila (LIFO) del deshacer
    public Double quitarUltimaNota() {
        if (notas.isEmpty()) {
            return null;
        }
        return notas.remove(notas.size() - 1);
    }

    public double promedio() {
        if (notas.isEmpty()) {
            return 0.0;
        }
        double suma = 0;
        for (double n : notas) {
            suma += n;
        }
        return suma / notas.size();
    }

    public boolean tieneNotasCompletas() {
        return notas.size() >= MAX_NOTAS;
    }

    public boolean estaAprobado() {
        // Polimorfismo: cada programa aplica su propia regla
        return tieneNotasCompletas() && programa.evaluarAprobacion(notas);
    }

    // Equivale a f"{n:g}" de Python: 10.0 se escribe como 10
    public static String formatearNota(double n) {
        if (n == Math.rint(n)) {
            return String.valueOf((long) n);
        }
        return String.valueOf(n);
    }

    // --- Persistencia: formato Cedula,Nombre,Correo,Tipo,N1,N2,N3 ---
    public String aLinea() {
        List<String> campos = new ArrayList<>();
        for (int i = 0; i < MAX_NOTAS; i++) {
            double n = i < notas.size() ? notas.get(i) : 0;
            campos.add(formatearNota(n));
        }
        return getCedula() + "," + getNombre() + "," + getCorreo() + ","
                + programa.getNombre() + "," + String.join(",", campos);
    }

    public static Alumno desdeLinea(String linea) {
        String[] p = linea.trim().split(",");
        if (p.length != 7) {
            throw new IllegalArgumentException("Formato de alumno inválido");
        }
        List<Double> notas = new ArrayList<>();
        for (int i = 4; i < 7; i++) {
            notas.add(Double.parseDouble(p[i]));
        }
        // En el archivo, un 0 al final significa "sin nota"
        while (!notas.isEmpty() && notas.get(notas.size() - 1) == 0) {
            notas.remove(notas.size() - 1);
        }
        return new Alumno(p[0], p[1], p[2], p[3], notas);
    }
}
