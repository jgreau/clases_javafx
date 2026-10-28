package cl.duoc.costaserena.dao;

import java.util.List;
import cl.duoc.costaserena.model.HabitacionSuite;
import cl.duoc.costaserena.model.HabitacionEstandar;

/**
 * Operaciones de lectura y escritura de habitaciones que necesita el repositorio.
 * Cada subtipo se almacena por separado.
 */
public interface HabitacionDao {

    List<HabitacionSuite> cargarHabitacionesSuite();
    List<HabitacionEstandar> cargarHabitacionesEstandar();

    void guardarHabitacionesSuite(List<HabitacionSuite> datos);
    void guardarHabitacionesEstandar(List<HabitacionEstandar> datos);
}
