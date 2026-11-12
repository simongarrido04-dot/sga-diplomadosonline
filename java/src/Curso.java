import java.util.List;

public class Curso extends ProgramaAcademico {

    public Curso() {
        super("Curso");
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        double promedio = notas.stream().mapToDouble(Double::doubleValue).sum() / notas.size();
        return promedio >= 10;
    }
}
