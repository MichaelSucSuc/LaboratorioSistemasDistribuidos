package pe.edu.unsa.soap;

import jakarta.xml.ws.Endpoint;

public class Publicador {
    public static void main(String[] args) {
        String url = "http://localhost:9090/calculadora";
        Endpoint.publish(url, new CalculadoraSOAP());
        System.out.println("Servicio SOAP activo");
        System.out.println("WSDL: " + url + "?wsdl");
    }
}