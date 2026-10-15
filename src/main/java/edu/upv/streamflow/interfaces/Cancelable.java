package edu.upv.streamflow.interfaces;

/**
 * Define el contrato de ciclo de vida para servicios que pueden ser dados de baja.
 */
public interface Cancelable {

    /**
     * Da de baja el servicio, cambiando su estado a inactivo.
     */
    void cancelarServicio();

    /**
     * Indica si el servicio se encuentra activo.
     *
     * @return true si el servicio está activo, false en caso contrario
     */
    boolean estaActivo();
}
