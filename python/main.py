from repositorio import Repositorio
from personas import Alumno, Profesor
from servicios import HistorialNotas, generar_certificados


def pedir_texto(mensaje):
    """Pide un texto y no acepta vacíos ni comas (rompen el formato del .txt)."""
    while True:
        valor = input(mensaje).strip()
        if not valor:
            print("Error: este campo no puede estar vacío.")
        elif "," in valor:
            print("Error: no use comas en este campo.")
        else:
            return valor


def pedir_numero(mensaje):
    """Pide un número y repite hasta que sea válido."""
    while True:
        try:
            return float(input(mensaje).strip().replace(",", "."))
        except ValueError:
            print("Error: Ingrese un valor numérico válido")


def mostrar_menu():
    print("=" * 50)
    print("SGA-DO: SISTEMA DIPLOMADOSONLINE")
    print("=" * 50)
    print("1. Registrar Alumno")
    print("2. Registrar Profesor")
    print("3. Registrar Notas a un Alumno")
    print("4. Deshacer Último Registro de Nota")
    print("5. Generar Cola de Certificados")
    print("6. Mostrar Reporte General")
    print("7. Salir")
    print("=" * 50)


def registrar_alumno(repo):
    cedula = pedir_texto("Cédula: ")
    if repo.existe_cedula(cedula):
        print("Error: ya existe una persona con esa cédula.")
        return
    nombre = pedir_texto("Nombre completo: ")
    correo = pedir_texto("Correo: ")
    tipo = pedir_texto("Tipo de programa (Curso, Diplomado o Bootcamp): ")
    try:
        repo.registrar_alumno(Alumno(cedula, nombre, correo, tipo))
        print("Alumno registrado correctamente.")
    except ValueError as e:
        print(f"Error: {e}")


def registrar_profesor(repo):
    cedula = pedir_texto("Cédula: ")
    if repo.existe_cedula(cedula):
        print("Error: ya existe una persona con esa cédula.")
        return
    nombre = pedir_texto("Nombre completo: ")
    correo = pedir_texto("Correo: ")
    especialidad = pedir_texto("Especialidad: ")
    materia = pedir_texto("Materia asignada: ")
    try:
        repo.registrar_profesor(Profesor(cedula, nombre, correo, especialidad, materia))
        print("Profesor registrado correctamente.")
    except ValueError as e:
        print(f"Error: {e}")


def registrar_nota(repo, historial):
    cedula = pedir_texto("Cédula del alumno: ")
    alumno = repo.buscar_alumno(cedula)
    if alumno is None:
        print("Error: no existe un alumno con esa cédula.")
        return
    print(f"Alumno: {alumno.nombre} ({alumno.programa.nombre})")
    print(f"Notas actuales: {alumno.notas}")
    nota = pedir_numero("Nota: ")
    historial.registrar_nota(cedula, nota)
    print("Nota registrada correctamente.")


def deshacer(historial):
    resultado = historial.deshacer()
    if resultado is None:
        print("No hay notas para deshacer.")
    else:
        cedula, nota = resultado
        print(f"Se deshizo la nota {nota:g} del alumno {cedula}.")


def cola_certificados(repo):
    total = generar_certificados(repo)
    print(f"Cola generada: {total} graduando(s). Archivo: certificados_pendientes.txt")


def reporte_general(repo):
    # Se relee desde los archivos, como pide el enunciado
    lector = Repositorio()
    print("=" * 50)
    print("REPORTE GENERAL")
    print("=" * 50)
    print("PROFESORES ACTIVOS:")
    profesores = lector.todos_los_profesores()
    if not profesores:
        print("  (sin profesores registrados)")
    for p in profesores:
        print(f"  [{p.cedula}] {p.nombre} - {p.especialidad} - {p.materia}")
    print("-" * 50)
    print("ALUMNOS REGISTRADOS:")
    alumnos = lector.todos_los_alumnos()
    if not alumnos:
        print("  (sin alumnos registrados)")
    for a in alumnos:
        estatus = "APROBADO" if a.esta_aprobado() else "REPROBADO"
        print(f"  [{a.cedula}] {a.nombre} - {a.programa.nombre}")
        print(f"      Notas: {a.notas} | Promedio: {a.promedio():.1f} | {estatus}")
    print("=" * 50)


def main():
    repo = Repositorio()
    historial = HistorialNotas(repo)

    while True:
        mostrar_menu()
        try:
            opcion = int(input("Seleccione una opción (1-7): ").strip())
        except ValueError:
            print("Error: Ingrese un valor numérico válido")
            continue

        if opcion == 1:
            registrar_alumno(repo)
        elif opcion == 2:
            registrar_profesor(repo)
        elif opcion == 3:
            registrar_nota(repo, historial)
        elif opcion == 4:
            deshacer(historial)
        elif opcion == 5:
            cola_certificados(repo)
        elif opcion == 6:
            reporte_general(repo)
        elif opcion == 7:
            repo.guardar_cambios_alumnos()
            print("Datos guardados. Hasta pronto.")
            break
        else:
            print("Error: elija una opción entre 1 y 7.")


if __name__ == "__main__":
    main()