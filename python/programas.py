class ProgramaAcademico:
    """Clase base. Las hijas deben sobrescribir evaluar_aprobacion()."""

    def __init__(self, nombre):
        self._nombre = nombre

    @property
    def nombre(self):
        return self._nombre

    def evaluar_aprobacion(self, notas):
        raise NotImplementedError("Cada programa define su regla")

    def descripcion_regla(self):
        return ""


class Curso(ProgramaAcademico):
    def __init__(self):
        super().__init__("Curso")

    def evaluar_aprobacion(self, notas):
        return sum(notas) / len(notas) >= 10


class Diplomado(ProgramaAcademico):
    def __init__(self):
        super().__init__("Diplomado")

    def evaluar_aprobacion(self, notas):
        return sum(notas) / len(notas) >= 14


class Bootcamp(ProgramaAcademico):
    def __init__(self):
        super().__init__("Bootcamp")

    def evaluar_aprobacion(self, notas):
        return all(n >= 14 for n in notas)

    def descripcion_regla(self):
        return " (Cumple regla de ninguna nota < 14)"


# Fábrica: convierte el texto del archivo en un objeto, sin cadenas de if/elif
_TIPOS = {"curso": Curso, "diplomado": Diplomado, "bootcamp": Bootcamp}


def crear_programa(tipo):
    clase = _TIPOS.get(tipo.strip().lower())
    if clase is None:
        raise ValueError("Tipo de programa inválido (Curso, Diplomado o Bootcamp)")
    return clase()