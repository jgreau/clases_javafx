package cl.duoc.costaserena.repository;

import cl.duoc.costaserena.dao.HabitacionJsonDao;
import cl.duoc.costaserena.model.Habitacion;
import cl.duoc.costaserena.model.HabitacionSuite;
import cl.duoc.costaserena.model.HabitacionEstandar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de habitaciones y delega en el DAO
 * la lectura y escritura. Cada cambio se guarda de inmediato, de modo que la memoria
 * y los archivos siempre coinciden.
 */
public class HabitacionRepository {

    private final HabitacionJsonDao dao;
    private final List<Habitacion> habitaciones = new ArrayList<>();

    public HabitacionRepository(HabitacionJsonDao dao) {
        this.dao = dao;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        habitaciones.clear();
        habitaciones.addAll(dao.cargarHabitacionesSuite());
        habitaciones.addAll(dao.cargarHabitacionesEstandar());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<Habitacion> listar() {
        // Vista de solo lectura: quien la reciba puede mirar la lista pero no modificarla; los cambios pasan por agregar/eliminar.
        return Collections.unmodifiableList(habitaciones);
    }

    /**
     * Agrega un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void agregar(Habitacion nuevo) {
        habitaciones.add(nuevo);
        try {
            guardar();
        // Si no se pudo guardar, se quita de la memoria lo recién agregado para que memoria y archivos sigan iguales.
        } catch (RuntimeException e) {
            habitaciones.remove(nuevo);
            // Se relanza el mismo error para que el controlador lo muestre en la ventana.
            throw e;
        }
    }

    /**
     * Elimina un elemento y guarda los archivos. Si no se puede guardar, deja la colección como estaba.
     */
    public void eliminar(Habitacion existente) {
        // indexOf devuelve -1 si el elemento no está en la lista; se guarda la posición para poder deshacer.
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
        // Se separan los elementos por subtipo porque cada subtipo se guarda en su propio archivo.
        for (Habitacion elemento : habitaciones) {
            // "instanceof Tipo x" pregunta si el elemento es de ese subtipo y, si lo es, lo deja disponible en x.
            if (elemento instanceof HabitacionSuite a) {
                lista1.add(a);
            } else if (elemento instanceof HabitacionEstandar b) {
                lista2.add(b);
            }
        }
        dao.guardarHabitacionesSuite(lista1);
        dao.guardarHabitacionesEstandar(lista2);
    }
}
