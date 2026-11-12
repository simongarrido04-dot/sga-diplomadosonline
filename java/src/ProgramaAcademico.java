import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ProgramaAcademico {
    private String nombre;

    public ProgramaAcademico(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // Cada programa define su propia regla (polimorfismo)
    public abstract boolean evaluarAprobacion(List<Double> notas);

    public String descripcionRegla() {
        return "";
    }

    // Fábrica: convierte el texto del archivo en un objeto, sin cadenas de if/else
    private static final Map<String, Supplier<ProgramaAcademico>> TIPOS = Map.of(
            "curso", Curso::new,
            "diplomado", Diplomado::new,
            "bootcamp", Bootcamp::new);

    public static ProgramaAcademico crearPrograma(String tipo) {
        Supplier<ProgramaAcademico> fabrica = TIPOS.get(tipo.trim().toLowerCase());
        if (fabrica == null) {
            throw new IllegalArgumentException("Tipo de programa inválido (Curso, Diplomado o Bootcamp)");
        }
        return fabrica.get();
    }
}
