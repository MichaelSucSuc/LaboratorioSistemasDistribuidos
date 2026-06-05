package com.laboratorio.soap.conversor;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Interfaz de Endpoint de Servicio (SEI) para el Conversor de Temperatura.
 * Define el contrato del servicio SOAP.
 */
@WebService(
    targetNamespace = "http://conversor.soap.laboratorio.com/"
)
public interface ConversorSOAP {

    /**
     * Convierte grados Celsius a Fahrenheit.
     *
     * @param c Temperatura en Celsius.
     * @return Temperatura en Fahrenheit.
     */
    @WebMethod(operationName = "cToF")
    double cToF(@WebParam(name = "celsius") double c);

    /**
     * Convierte grados Fahrenheit a Celsius.
     *
     * @param f Temperatura en Fahrenheit.
     * @return Temperatura en Celsius.
     */
    @WebMethod(operationName = "fToC")
    double fToC(@WebParam(name = "fahrenheit") double f);
}
