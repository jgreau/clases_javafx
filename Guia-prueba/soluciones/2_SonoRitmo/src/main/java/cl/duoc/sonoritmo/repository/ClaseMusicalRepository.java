package cl.duoc.sonoritmo.repository;

import cl.duoc.sonoritmo.dao.Dao;
import cl.duoc.sonoritmo.model.ClaseMusical;
import cl.duoc.sonoritmo.model.ClaseIndividual;
import cl.duoc.sonoritmo.model.ClaseGrupal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de clases y delega en los DAO
 * la lectura y escritura. Cada cambio se guarda de inmediato, de modo que la memoria
 * y los archivos siempre coinciden.
 */
public class ClaseMusicalRepository {

    private final Dao<ClaseIndividual> daoClaseIndividual;
    private final Dao<ClaseGrupal> daoClaseGrupal;
    private final List<ClaseMusical> clases = new ArrayList<>();

    public ClaseMusicalRepository(Dao<ClaseIndividual> daoClaseIndividual, Dao<ClaseGrupal> daoClaseGrupal) {
        this.daoClaseIndividual = daoClaseIndividual;
        this.daoClaseGrupal = daoClaseGrupal;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        clases.clear();
        clases.addAll(daoClaseIndividual.cargar());
        clases.addAll(daoClaseGrupal.cargar());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<ClaseMusical> listar() {
        return Collections.unmodifiableList(clases);
    }

    /**
     * Agrega un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void agregar(ClaseMusical nuevo) {
        clases.add(nuevo);
        try {
            guardar();
        } catch (RuntimeException e) {
            clases.remove(nuevo);
            throw e;
        }
    }

    /**
     * Elimina un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void eliminar(ClaseMusical existente) {
        int posicion = clases.indexOf(existente);
        if (posicion < 0) {
            return;
        }
        clases.remove(posicion);
        try {
            guardar();
        } catch (RuntimeException e) {
            clases.add(posicion, existente);
            throw e;
        }
    }

    private void guardar() {
        List<ClaseIndividual> lista1 = new ArrayList<>();
        List<ClaseGrupal> lista2 = new ArrayList<>();
        for (ClaseMusical elemento : clases) {
            if (elemento instanceof ClaseIndividual a) {
                lista1.add(a);
            } else if (elemento instanceof ClaseGrupal b) {
                lista2.add(b);
            }
        }
        daoClaseIndividual.guardar(lista1);
        daoClaseGrupal.guardar(lista2);
    }
}
