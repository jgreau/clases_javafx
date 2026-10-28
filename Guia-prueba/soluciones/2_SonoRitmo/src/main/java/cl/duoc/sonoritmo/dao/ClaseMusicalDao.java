package cl.duoc.sonoritmo.dao;

import java.util.List;
import cl.duoc.sonoritmo.model.ClaseIndividual;
import cl.duoc.sonoritmo.model.ClaseGrupal;

/**
 * Operaciones de lectura y escritura de clases que necesita el repositorio.
 * Cada subtipo se almacena por separado.
 */
public interface ClaseMusicalDao {

    List<ClaseIndividual> cargarClasesIndividuales();
    List<ClaseGrupal> cargarClasesGrupales();

    void guardarClasesIndividuales(List<ClaseIndividual> datos);
    void guardarClasesGrupales(List<ClaseGrupal> datos);
}
