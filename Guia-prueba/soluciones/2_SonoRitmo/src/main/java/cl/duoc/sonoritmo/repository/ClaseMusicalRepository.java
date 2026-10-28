package cl.duoc.sonoritmo.repository;

import cl.duoc.sonoritmo.dao.ClaseMusicalJsonDao;
import cl.duoc.sonoritmo.model.ClaseMusical;
import cl.duoc.sonoritmo.model.ClaseIndividual;
import cl.duoc.sonoritmo.model.ClaseGrupal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de clases y delega en el DAO
 * la lectura y escritura. Cada cambio se guarda de inmediato, de modo que la memoria
 * y los archivos siempre coinciden.
 */
public class ClaseMusicalRepository {

    private final ClaseMusicalJsonDao dao;
    private final List<ClaseMusical> clases = new ArrayList<>();

    public ClaseMusicalRepository(ClaseMusicalJsonDao dao) {
        this.dao = dao;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        clases.clear();
        clases.addAll(dao.cargarClasesIndividuales());
        clases.addAll(dao.cargarClasesGrupales());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<ClaseMusical> listar() {
        // Vista de solo lectura: quien la reciba puede mirar la lista pero no modificarla; los cambios pasan por agregar/eliminar.
        return Collections.unmodifiableList(clases);
    }

    /**
     * Agrega un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void agregar(ClaseMusical nuevo) {
        clases.add(nuevo);
        try {
            guardar();
        // Si no se pudo guardar, se quita de la memoria lo recién agregado para que memoria y archivos sigan iguales.
        } catch (RuntimeException e) {
            clases.remove(nuevo);
            // Se relanza el mismo error para que el controlador lo muestre en la ventana.
            throw e;
        }
    }

    /**
     * Elimina un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void eliminar(ClaseMusical existente) {
        // indexOf devuelve -1 si el elemento no está en la lista; se guarda la posición para poder deshacer.
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
        // Se separan los elementos por subtipo porque cada subtipo se guarda en su propio archivo.
        for (ClaseMusical elemento : clases) {
            // "instanceof Tipo x" pregunta si el elemento es de ese subtipo y, si lo es, lo deja disponible en x.
            if (elemento instanceof ClaseIndividual a) {
                lista1.add(a);
            } else if (elemento instanceof ClaseGrupal b) {
                lista2.add(b);
            }
        }
        dao.guardarClasesIndividuales(lista1);
        dao.guardarClasesGrupales(lista2);
    }
}
