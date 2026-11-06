package cl.duoc.capacitacion.dao;

import java.util.List;
import cl.duoc.capacitacion.model.CursoCertificado;
import cl.duoc.capacitacion.model.CursoLibre;

/**
 * Operaciones de lectura y escritura de cursos que necesita el repositorio.
 * Cada subtipo se almacena por separado.
 */
public interface CursoDao {

    List<CursoCertificado> cargarCursosCertificados();
    List<CursoLibre> cargarCursosLibres();

    void guardarCursosCertificados(List<CursoCertificado> datos);
    void guardarCursosLibres(List<CursoLibre> datos);
}
