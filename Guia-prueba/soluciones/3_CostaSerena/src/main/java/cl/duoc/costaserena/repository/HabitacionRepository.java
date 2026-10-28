package cl.duoc.costaserena.repository;

import cl.duoc.costaserena.dao.Dao;
import cl.duoc.costaserena.model.Habitacion;
import cl.duoc.costaserena.model.HabitacionSuite;
import cl.duoc.costaserena.model.HabitacionEstandar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de habitaciones y delega en los DAO
 * la lectura y escritura. Cada cambio se guarda de inmediato, de modo que la memoria
 * y los archivos siempre coinciden.
 */
public class HabitacionRepository {

    private final Dao<HabitacionSuite> daoHabitacionSuite;
    private final Dao<HabitacionEstandar> daoHabitacionEstandar;
    private final List<Habitacion> habitaciones = new ArrayList<>();

    public HabitacionRepository(Dao<HabitacionSuite> daoHabitacionSuite, Dao<HabitacionEstandar> daoHabitacionEstandar) {
        this.daoHabitacionSuite = daoHabitacionSuite;
        this.daoHabitacionEstandar = daoHabitacionEstandar;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        habitaciones.clear();
        habitaciones.addAll(daoHabitacionSuite.cargar());
        habitaciones.addAll(daoHabitacionEstandar.cargar());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<Habitacion> listar() {
        return Collections.unmodifiableList(habitaciones);
    }

    /**
     * Agrega un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void agregar(Habitacion nuevo) {
        habitaciones.add(nuevo);
        try {
            guardar();
        } catch (RuntimeException e) {
            habitaciones.remove(nuevo);
            throw e;
        }
    }

    /**
     * Elimina un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void eliminar(Habitacion existente) {
        int posicion = habitaciones.indexOf(existente);
        if (posicion < 0) {
            return;
        }
        habitaciones.remove(posicion);
        try {
            guardar();
        } catch (RuntimeException e) {
            habitaciones.add(posicion, existente);
            throw e;
        }
    }

    private void guardar() {
        List<HabitacionSuite> lista1 = new ArrayList<>();
        List<HabitacionEstandar> lista2 = new ArrayList<>();
        for (Habitacion elemento : habitaciones) {
            if (elemento instanceof HabitacionSuite a) {
                lista1.add(a);
            } else if (elemento instanceof HabitacionEstandar b) {
                lista2.add(b);
            }
        }
        daoHabitacionSuite.guardar(lista1);
        daoHabitacionEstandar.guardar(lista2);
    }
}
