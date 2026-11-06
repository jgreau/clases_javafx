package cl.duoc.capacitacion.repository;

import cl.duoc.capacitacion.dao.CursoJsonDao;
import cl.duoc.capacitacion.model.Curso;
import cl.duoc.capacitacion.model.CursoCertificado;
import cl.duoc.capacitacion.model.CursoLibre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de cursos y delega en el DAO
 * la lectura y escritura. Cada cambio se guarda de inmediato, de modo que la memoria
 * y los archivos siempre coinciden.
 */
public class CursoRepository {

    private final CursoJsonDao dao;
    private final List<Curso> cursos = new ArrayList<>();

    public CursoRepository(CursoJsonDao dao) {
        this.dao = dao;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        cursos.clear();
        cursos.addAll(dao.cargarCursosCertificados());
        cursos.addAll(dao.cargarCursosLibres());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<Curso> listar() {
        // Vista de solo lectura: quien la reciba puede mirar la lista pero no modificarla; los cambios pasan por agregar/eliminar.
        return Collections.unmodifiableList(cursos);
    }

    /**
     * Agrega un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void agregar(Curso nuevo) {
        cursos.add(nuevo);
        try {
            guardar();
        // Si no se pudo guardar, se quita de la memoria lo recién agregado para que memoria y archivos sigan iguales.
        } catch (RuntimeException e) {
            cursos.remove(nuevo);
            // Se relanza el mismo error para que el controlador lo muestre en la ventana.
            throw e;
        }
    }

    /**
     * Elimina un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void eliminar(Curso existente) {
        // indexOf devuelve -1 si el elemento no está en la lista; se guarda la posición para poder deshacer.
        int posicion = cursos.indexOf(existente);
        if (posicion < 0) {
            return;
        }
        cursos.remove(posicion);
        try {
            guardar();
        } catch (RuntimeException e) {
            cursos.add(posicion, existente);
            throw e;
        }
    }

    private void guardar() {
        List<CursoCertificado> lista1 = new ArrayList<>();
        List<CursoLibre> lista2 = new ArrayList<>();
        // Se separan los elementos por subtipo porque cada subtipo se guarda en su propio archivo.
        for (Curso elemento : cursos) {
            // "instanceof Tipo x" pregunta si el elemento es de ese subtipo y, si lo es, lo deja disponible en x.
            if (elemento instanceof CursoCertificado a) {
                lista1.add(a);
            } else if (elemento instanceof CursoLibre b) {
                lista2.add(b);
            }
        }
        dao.guardarCursosCertificados(lista1);
        dao.guardarCursosLibres(lista2);
    }
}
