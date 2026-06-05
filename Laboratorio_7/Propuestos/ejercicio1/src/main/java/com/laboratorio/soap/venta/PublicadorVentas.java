package com.laboratorio.soap.venta;

import javax.xml.ws.Endpoint;

/**
 * Publicador del servicio web VentaSOAP en un servidor local embebido.
 */
public class PublicadorVentas {

    public static void main(String[] args) {
        String url = "http://localhost:8081/ventas";
        System.out.println("[INFO] Iniciando el Publicador para VentaSOAP...");
        
        // Publica el servicio en la dirección especificada
        Endpoint.publish(url, new VentaSOAPImpl());
        
        System.out.println("[INFO] Servicio de ventas publicado de manera exitosa.");
        System.out.println("[INFO] Dirección del Endpoint: " + url);
        System.out.println("[INFO] WSDL disponible en: " + url + "?wsdl");
        System.out.println("[INFO] Presione Ctrl+C en la consola para detener el servicio.");
    }
}
