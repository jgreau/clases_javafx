package cl.duoc.gamezone.dao;

import java.util.List;
import cl.duoc.gamezone.model.JuegoDigital;
import cl.duoc.gamezone.model.JuegoFisico;

/**
 * Operaciones de lectura y escritura de videojuegos que necesita el repositorio.
 * Cada subtipo se almacena por separado.
 */
public interface VideojuegoDao {

    List<JuegoDigital> cargarJuegosDigitales();
    List<JuegoFisico> cargarJuegosFisicos();

    void guardarJuegosDigitales(List<JuegoDigital> datos);
    void guardarJuegosFisicos(List<JuegoFisico> datos);
}
