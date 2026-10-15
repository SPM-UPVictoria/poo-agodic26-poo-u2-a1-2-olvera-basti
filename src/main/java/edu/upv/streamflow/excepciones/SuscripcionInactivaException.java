package edu.upv.streamflow.excepciones;

/**
 * Excepción propia no verificada (unchecked) para señalar violaciones graves
 * en el ciclo de vida de una suscripción (e.g. intentar facturar un servicio cancelado).
 */
public class SuscripcionInactivaException extends RuntimeException {

    public SuscripcionInactivaException(String mensaje) {
        super(mensaje);
    }
}
