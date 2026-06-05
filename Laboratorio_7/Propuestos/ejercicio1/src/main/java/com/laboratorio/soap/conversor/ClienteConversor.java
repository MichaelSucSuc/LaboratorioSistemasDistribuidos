package com.laboratorio.soap.conversor;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * Cliente consumidor Java para el servicio web ConversorSOAP.
 * Realiza un descubrimiento dinámico del WSDL en tiempo de ejecución.
 */
public class ClienteConversor {

    public static void main(String[] args) {
        try {
            // URL del WSDL del servicio web publicado
            URL url = new URL("http://localhost:8080/conversor?wsdl");

            // QName del servicio (Namespace URI y Local Part)
            // Coincide exactamente con los parámetros definidos en la anotación @WebService de ConversorSOAP
            QName serviceName = new QName("http://conversor.soap.laboratorio.com/", "ConversorSOAPService");
            QName portName = new QName("http://conversor.soap.laboratorio.com/", "ConversorSOAPPort");

            System.out.println("[CLIENTE] Conectando al servicio en: " + url);
            
            // Creación de la instancia del servicio usando descubrimiento del WSDL
            Service service = Service.create(url, serviceName);

            // Obtención dinámica del puerto utilizando la interfaz del servicio
            ConversorSOAP conversor = service.getPort(portName, ConversorSOAP.class);
            
            System.out.println("[CLIENTE] Puerto obtenido exitosamente. Iniciando pruebas de invocación:");

            // Invocación del método cToF con valor de prueba de 30 °C
            double cInput = 30.0;
            double fOutput = conversor.cToF(cInput);
            System.out.println("\n==================================================");
            System.out.printf("  Método cToF (Celsius a Fahrenheit):\n");
            System.out.printf("  Entrada:  %.2f °C\n", cInput);
            System.out.printf("  Salida:   %.2f °F (Esperado: 86.00 °F)\n", fOutput);
            System.out.println("==================================================");

            // Invocación del método fToC con valor de prueba de 86 °F
            double fInput = 86.0;
            double cOutput = conversor.fToC(fInput);
            System.out.println("==================================================");
            System.out.printf("  Método fToC (Fahrenheit a Celsius):\n");
            System.out.printf("  Entrada:  %.2f °F\n", fInput);
            System.out.printf("  Salida:   %.2f °C (Esperado: 30.00 °C)\n", cOutput);
            System.out.println("==================================================\n");

            System.out.println("[CLIENTE] Pruebas finalizadas con éxito.");
        } catch (Exception e) {
            System.err.println("[CLIENTE] [ERROR] Ocurrió un fallo durante la ejecución del cliente:");
            e.printStackTrace();
        }
    }
}
