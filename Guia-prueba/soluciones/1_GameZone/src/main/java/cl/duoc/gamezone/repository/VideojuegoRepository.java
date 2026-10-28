package cl.duoc.gamezone.repository;

import cl.duoc.gamezone.dao.VideojuegoJsonDao;
import cl.duoc.gamezone.model.Videojuego;
import cl.duoc.gamezone.model.JuegoDigital;
import cl.duoc.gamezone.model.JuegoFisico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de videojuegos y delega en el DAO
 * la lectura y escritura. Cada cambio se guarda de inmediato, de modo que la memoria
 * y los archivos siempre coinciden.
 */
public class VideojuegoRepository {

    private final VideojuegoJsonDao dao;
    private final List<Videojuego> videojuegos = new ArrayList<>();

    public VideojuegoRepository(VideojuegoJsonDao dao) {
        this.dao = dao;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        videojuegos.clear();
        videojuegos.addAll(dao.cargarJuegosDigitales());
        videojuegos.addAll(dao.cargarJuegosFisicos());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<Videojuego> listar() {
        // Vista de solo lectura: quien la reciba puede mirar la lista pero no modificarla; los cambios pasan por agregar/eliminar.
        return Collections.unmodifiableList(videojuegos);
    }

    /**
     * Agrega un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void agregar(Videojuego nuevo) {
        videojuegos.add(nuevo);
        try {
            guardar();
        // Si no se pudo guardar, se quita de la memoria lo recién agregado para que memoria y archivos sigan iguales.
        } catch (RuntimeException e) {
            videojuegos.remove(nuevo);
            // Se relanza el mismo error para que el controlador lo muestre en la ventana.
            throw e;
        }
    }

    /**
     * Elimina un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void eliminar(Videojuego existente) {
        // indexOf devuelve -1 si el elemento no está en la lista; se guarda la posición para poder deshacer.
        int posicion = videojuegos.indexOf(existente);
        if (posicion < 0) {
            return;
        }
        videojuegos.remove(posicion);
        try {
            guardar();
        } catch (RuntimeException e) {
            videojuegos.add(posicion, existente);
            throw e;
        }
    }

    private void guardar() {
        List<JuegoDigital> lista1 = new ArrayList<>();
        List<JuegoFisico> lista2 = new ArrayList<>();
        // Se separan los elementos por subtipo porque cada subtipo se guarda en su propio archivo.
        for (Videojuego elemento : videojuegos) {
            // "instanceof Tipo x" pregunta si el elemento es de ese subtipo y, si lo es, lo deja disponible en x.
            if (elemento instanceof JuegoDigital a) {
                lista1.add(a);
            } else if (elemento instanceof JuegoFisico b) {
                lista2.add(b);
            }
        }
        dao.guardarJuegosDigitales(lista1);
        dao.guardarJuegosFisicos(lista2);
    }
}
