package edu.upv.streamflow.interfaces;

import edu.upv.streamflow.excepciones.TransaccionRechazadaException;
import edu.upv.streamflow.modelo.Cliente;

/**
 * Define la abstracción del comportamiento financiero para servicios facturables.
 */
public interface Facturable {

    /**
     * Calcula el importe total a facturar en el ciclo actual.
     *
     * @return monto total facturable
     */
    double calcularMontoFacturable();

    /**
     * Procesa el cobro del monto facturable al cliente especificado.
     *
     * @param cliente cliente al que se le debitará el monto
     * @throws TransaccionRechazadaException si el saldo es insuficiente u ocurre una anomalía
     */
    void procesarCobro(Cliente cliente) throws TransaccionRechazadaException;
}
