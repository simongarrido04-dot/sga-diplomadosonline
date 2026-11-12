public class Profesor extends Persona {
    private String especialidad;
    private String materia;

    public Profesor(String cedula, String nombre, String correo,
                    String especialidad, String materia) {
        super(cedula, nombre, correo);
        this.especialidad = especialidad;
        this.materia = materia;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getMateria() {
        return materia;
    }

    public String aLinea() {
        return getCedula() + "," + getNombre() + "," + getCorreo() + ","
                + especialidad + "," + materia;
    }

    public static Profesor desdeLinea(String linea) {
        String[] p = linea.trim().split(",");
        if (p.length != 5) {
            throw new IllegalArgumentException("Formato de profesor inválido");
        }
        return new Profesor(p[0], p[1], p[2], p[3], p[4]);
    }
}
