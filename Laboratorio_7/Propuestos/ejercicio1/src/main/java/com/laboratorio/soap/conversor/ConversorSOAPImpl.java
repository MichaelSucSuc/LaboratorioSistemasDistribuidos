package com.laboratorio.soap.conversor;

import javax.jws.WebService;

/**
 * Clase de implementación (SIB) para el servicio web ConversorSOAP.
 * Vincula esta clase con la interfaz del endpoint mediante la propiedad endpointInterface.
 */
@WebService(
    endpointInterface = "com.laboratorio.soap.conversor.ConversorSOAP",
    targetNamespace = "http://conversor.soap.laboratorio.com/",
    serviceName = "ConversorSOAPService",
    portName = "ConversorSOAPPort"
)
public class ConversorSOAPImpl implements ConversorSOAP {

    @Override
    public double cToF(double c) {
        return (c * 9.0 / 5.0) + 32.0;
    }

    @Override
    public double fToC(double f) {
        return (f - 32.0) * 5.0 / 9.0;
    }
}
