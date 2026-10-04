from programas import crear_programa


class Persona:
    def __init__(self, cedula, nombre, correo):
        self._cedula = cedula
        self._nombre = nombre
        self._correo = correo

    @property
    def cedula(self):
        return self._cedula

    @property
    def nombre(self):
        return self._nombre

    @property
    def correo(self):
        return self._correo


class Alumno(Persona):
    MAX_NOTAS = 3

    def __init__(self, cedula, nombre, correo, tipo_programa, notas=None):
        super().__init__(cedula, nombre, correo)
        self._programa = crear_programa(tipo_programa)
        self._notas = list(notas) if notas else []

    @property
    def programa(self):
        return self._programa

    @property
    def notas(self):
        return list(self._notas)  # copia, para proteger la lista interna

    def agregar_nota(self, nota):
        self._notas.append(nota)

    def quitar_ultima_nota(self):
        """La usa la Pila (LIFO) del deshacer."""
        if self._notas:
            return self._notas.pop()

    def promedio(self):
        return sum(self._notas) / len(self._notas) if self._notas else 0.0

    def tiene_notas_completas(self):
        return len(self._notas) >= self.MAX_NOTAS

    def esta_aprobado(self):
        # Polimorfismo: cada programa aplica su propia regla
        return (self.tiene_notas_completas()
                and self._programa.evaluar_aprobacion(self._notas))

    # --- Persistencia: formato Cedula,Nombre,Correo,Tipo,N1,N2,N3 ---
    def a_linea(self):
        guardadas = self._notas[:self.MAX_NOTAS]
        notas = guardadas + [0] * (self.MAX_NOTAS - len(guardadas))
        notas_txt = ",".join(f"{n:g}" for n in notas)
        return f"{self._cedula},{self._nombre},{self._correo},{self._programa.nombre},{notas_txt}"
    @staticmethod
    def desde_linea(linea):
        c, nom, cor, tipo, n1, n2, n3 = linea.strip().split(",")
        notas = [float(n1), float(n2), float(n3)]
        # En el archivo, un 0 sin completar significa "sin nota":
        # quitamos los ceros finales (ver nota abajo).
        while notas and notas[-1] == 0:
            notas.pop()
        return Alumno(c, nom, cor, tipo, notas)


class Profesor(Persona):
    def __init__(self, cedula, nombre, correo, especialidad, materia):
        super().__init__(cedula, nombre, correo)
        self._especialidad = especialidad
        self._materia = materia

    @property
    def especialidad(self):
        return self._especialidad

    @property
    def materia(self):
        return self._materia

    def a_linea(self):
        return f"{self._cedula},{self._nombre},{self._correo},{self._especialidad},{self._materia}"

    @staticmethod
    def desde_linea(linea):
        c, nom, cor, esp, mat = linea.strip().split(",")
        return Profesor(c, nom, cor, esp, mat)