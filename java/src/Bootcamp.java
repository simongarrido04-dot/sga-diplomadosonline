import java.util.List;

public class Bootcamp extends ProgramaAcademico {

    public Bootcamp() {
        super("Bootcamp");
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        for (double nota : notas) {
            if (nota < 14) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String descripcionRegla() {
        return " (Cumple regla de ninguna nota < 14)";
    }
}
