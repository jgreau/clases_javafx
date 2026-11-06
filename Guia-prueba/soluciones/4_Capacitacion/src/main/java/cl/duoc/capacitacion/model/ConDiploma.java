package cl.duoc.capacitacion.model;

/**
 * Contrato que cumplen los cursos que participan en el proceso de
 * emisión de diplomas de la empresa.
 */
public interface ConDiploma {

    /**
     * Indica si el curso tiene un diploma emitido.
     *
     * @return true si el diploma fue emitido
     */
    boolean tieneDiplomaEmitido();

    /**
     * Registra la emisión del diploma del curso.
     */
    void emitirDiploma();
}
