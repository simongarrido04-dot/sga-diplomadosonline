import os
from personas import Alumno, Profesor

# Los archivos se guardan junto al código, sin importar desde dónde se ejecute
CARPETA = os.path.dirname(os.path.abspath(__file__))
ARCHIVO_ALUMNOS = os.path.join(CARPETA, "alumnos.txt")
ARCHIVO_PROFESORES = os.path.join(CARPETA, "profesores.txt")
ARCHIVO_CERTIFICADOS = os.path.join(CARPETA, "certificados_pendientes.txt")


class Repositorio:
    """Guarda y lee alumnos y profesores en archivos .txt."""

    def __init__(self):
        self._alumnos = {}      # cedula -> Alumno
        self._profesores = {}   # cedula -> Profesor
        self._cargar()

    # ---------- Carga inicial ----------
    def _cargar(self):
        self._alumnos = self._leer(ARCHIVO_ALUMNOS, Alumno.desde_linea)
        self._profesores = self._leer(ARCHIVO_PROFESORES, Profesor.desde_linea)

    @staticmethod
    def _leer(ruta, constructor):
        datos = {}
        if not os.path.exists(ruta):
            return datos
        with open(ruta, "r", encoding="utf-8") as f:
            for linea in f:
                if not linea.strip():
                    continue
                try:
                    obj = constructor(linea)
                    datos[obj.cedula] = obj
                except (ValueError, TypeError):
                    print(f"Aviso: línea ignorada por formato inválido: {linea.strip()}")
        return datos

    # ---------- Escritura ----------
    def _guardar_alumnos(self):
        with open(ARCHIVO_ALUMNOS, "w", encoding="utf-8") as f:
            for a in self._alumnos.values():
                f.write(a.a_linea() + "\n")

    def _guardar_profesores(self):
        with open(ARCHIVO_PROFESORES, "w", encoding="utf-8") as f:
            for p in self._profesores.values():
                f.write(p.a_linea() + "\n")

    # ---------- Operaciones ----------
    def existe_cedula(self, cedula):
        return cedula in self._alumnos or cedula in self._profesores

    def registrar_alumno(self, alumno):
        if self.existe_cedula(alumno.cedula):
            raise ValueError("Ya existe una persona con esa cédula")
        self._alumnos[alumno.cedula] = alumno
        self._guardar_alumnos()          # se escribe de inmediato

    def registrar_profesor(self, profesor):
        if self.existe_cedula(profesor.cedula):
            raise ValueError("Ya existe una persona con esa cédula")
        self._profesores[profesor.cedula] = profesor
        self._guardar_profesores()       # se escribe de inmediato

    def buscar_alumno(self, cedula):
        return self._alumnos.get(cedula)

    def guardar_cambios_alumnos(self):
        """Llamar después de agregar o quitar una nota."""
        self._guardar_alumnos()

    def todos_los_alumnos(self):
        return list(self._alumnos.values())

    def todos_los_profesores(self):
        return list(self._profesores.values())