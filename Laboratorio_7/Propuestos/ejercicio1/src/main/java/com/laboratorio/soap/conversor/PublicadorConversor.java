package com.laboratorio.soap.conversor;

import javax.xml.ws.Endpoint;

/**
 * Publicador del servicio web ConversorSOAP en un servidor local embebido.
 */
public class PublicadorConversor {

    public static void main(String[] args) {
        String url = "http://localhost:8080/conversor";
        System.out.println("[INFO] Iniciando el Publicador para ConversorSOAP...");
        
        // Publica el servicio en la dirección especificada
        Endpoint.publish(url, new ConversorSOAPImpl());
        
        System.out.println("[INFO] Servicio publicado de manera exitosa.");
        System.out.println("[INFO] Dirección del Endpoint: " + url);
        System.out.println("[INFO] WSDL disponible en: " + url + "?wsdl");
        System.out.println("[INFO] Presione Ctrl+C en la consola para detener el servicio.");
    }
}
