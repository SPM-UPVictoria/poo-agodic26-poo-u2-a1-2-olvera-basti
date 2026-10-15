package edu.upv.streamflow.excepciones;

/**
 * Excepción propia verificada (checked) para notificar anomalías de negocio,
 * como saldo insuficiente para procesar una transacción.
 */
public class TransaccionRechazadaException extends Exception {

    public TransaccionRechazadaException(String mensaje) {
        super(mensaje);
    }

    public TransaccionRechazadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
