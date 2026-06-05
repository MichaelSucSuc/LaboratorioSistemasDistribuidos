package pe.edu.unsa.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public class CalculadoraSOAP {

    @WebMethod
    public int sumar(int a, int b) {
        return a + b;
    }
}