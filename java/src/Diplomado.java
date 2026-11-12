import java.util.List;

public class Diplomado extends ProgramaAcademico {

    public Diplomado() {
        super("Diplomado");
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        double promedio = notas.stream().mapToDouble(Double::doubleValue).sum() / notas.size();
        return promedio >= 14;
    }
}
