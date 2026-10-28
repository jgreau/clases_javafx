package cl.duoc.gamezone.repository;

import cl.duoc.gamezone.dao.Dao;
import cl.duoc.gamezone.model.Videojuego;
import cl.duoc.gamezone.model.JuegoDigital;
import cl.duoc.gamezone.model.JuegoFisico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de videojuegos y delega en los DAO
 * la lectura y escritura. Cada cambio se guarda de inmediato, de modo que la memoria
 * y los archivos siempre coinciden.
 */
public class VideojuegoRepository {

    private final Dao<JuegoDigital> daoJuegoDigital;
    private final Dao<JuegoFisico> daoJuegoFisico;
    private final List<Videojuego> videojuegos = new ArrayList<>();

    public VideojuegoRepository(Dao<JuegoDigital> daoJuegoDigital, Dao<JuegoFisico> daoJuegoFisico) {
        this.daoJuegoDigital = daoJuegoDigital;
        this.daoJuegoFisico = daoJuegoFisico;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        videojuegos.clear();
        videojuegos.addAll(daoJuegoDigital.cargar());
        videojuegos.addAll(daoJuegoFisico.cargar());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<Videojuego> listar() {
        return Collections.unmodifiableList(videojuegos);
    }

    /**
     * Agrega un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void agregar(Videojuego nuevo) {
        videojuegos.add(nuevo);
        try {
            guardar();
        } catch (RuntimeException e) {
            videojuegos.remove(nuevo);
            throw e;
        }
    }

    /**
     * Elimina un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void eliminar(Videojuego existente) {
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
        for (Videojuego elemento : videojuegos) {
            if (elemento instanceof JuegoDigital a) {
                lista1.add(a);
            } else if (elemento instanceof JuegoFisico b) {
                lista2.add(b);
            }
        }
        daoJuegoDigital.guardar(lista1);
        daoJuegoFisico.guardar(lista2);
    }
}
