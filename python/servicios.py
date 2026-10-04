from collections import deque
from repositorio import ARCHIVO_CERTIFICADOS


class HistorialNotas:
    """Registra notas y guarda cada acción en una PILA (LIFO) para poder deshacer."""

    def __init__(self, repo):
        self._repo = repo
        self._pila = []  # guarda la cédula de cada nota agregada

    def registrar_nota(self, cedula, nota):
        alumno = self._repo.buscar_alumno(cedula)
        if alumno is None:
            raise ValueError("No existe un alumno con esa cédula")
        alumno.agregar_nota(nota)               # valida rango y máximo de 3
        self._repo.guardar_cambios_alumnos()    # se escribe de inmediato
        self._pila.append(cedula)               # push

    def deshacer(self):
        """Quita la última nota ingresada (LIFO). Devuelve (cedula, nota) o None."""
        if not self._pila:
            return None
        cedula = self._pila.pop()               # pop: la última en entrar
        alumno = self._repo.buscar_alumno(cedula)
        nota = alumno.quitar_ultima_nota()
        self._repo.guardar_cambios_alumnos()
        return cedula, nota


def generar_certificados(repo):
    """Mete a los aprobados en una COLA (FIFO) y la exporta al .txt."""
    cola = deque()
    for alumno in repo.todos_los_alumnos():
        if alumno.esta_aprobado():
            cola.append(alumno)                 # enqueue

    total = len(cola)
    with open(ARCHIVO_CERTIFICADOS, "w", encoding="utf-8") as f:
        f.write("=" * 41 + "\n")
        f.write("REPORTE DE CERTIFICADOS PENDIENTES\n")
        f.write("=" * 41 + "\n")
        f.write(f"Total de graduandos en cola: {total}\n\n")

        posicion = 1
        while cola:
            a = cola.popleft()                  # dequeue: el primero en entrar
            regla = a.programa.descripcion_regla()
            f.write(f"{posicion}. [{a.cedula}] {a.nombre}\n")
            f.write(f"   - Programa: {a.programa.nombre}\n")
            f.write(f"   - Promedio Final: {a.promedio():.1f}\n")
            f.write(f"   - Estatus: APROBADO{regla}\n\n")
            posicion += 1

        f.write("=" * 41 + "\n")
        f.write("* Fin del reporte - Generado por SGA-DO *\n")
    return total