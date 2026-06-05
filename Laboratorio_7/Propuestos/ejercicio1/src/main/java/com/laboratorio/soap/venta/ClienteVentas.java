package com.laboratorio.soap.venta;

import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * Cliente consumidor Java para el servicio web VentaSOAP.
 * Realiza un descubrimiento dinámico del WSDL en tiempo de ejecución.
 * Ejecuta pruebas funcionales completas del servicio de ventas en línea.
 */
public class ClienteVentas {

    public static void main(String[] args) {
        try {
            // URL del WSDL del servicio web de ventas publicado
            URL url = new URL("http://localhost:8081/ventas?wsdl");

            // QName del servicio (Namespace URI y Local Part)
            // Coincide exactamente con los parámetros definidos en la anotación @WebService de VentaSOAPImpl
            QName serviceName = new QName("http://venta.soap.laboratorio.com/", "VentaSOAPService");
            QName portName = new QName("http://venta.soap.laboratorio.com/", "VentaSOAPPort");

            System.out.println("[CLIENTE VENTAS] Conectando al servicio en: " + url);
            
            // Creación de la instancia del servicio usando descubrimiento del WSDL
            Service service = Service.create(url, serviceName);

            // Obtención dinámica del puerto utilizando la interfaz del servicio VentaSOAP
            VentaSOAP ventaService = service.getPort(portName, VentaSOAP.class);
            
            System.out.println("[CLIENTE VENTAS] Puerto obtenido exitosamente. Iniciando flujo de pruebas:");

            // 1. Listar Productos iniciales
            System.out.println("\n============================================================================");
            System.out.println("  1. LISTADO DE PRODUCTOS EN EL INVENTARIO                                  ");
            System.out.println("============================================================================");
            List<Producto> productos = ventaService.listarProductos();
            for (Producto p : productos) {
                System.out.printf("  ID: %d | Nombre: %-40s | Precio: $%7.2f | Stock: %d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
            System.out.println("============================================================================");

            // 2. Buscar un producto específico
            int idBusqueda = 102;
            System.out.printf("\n[CLIENTE VENTAS] Buscando producto con ID: %d...%n", idBusqueda);
            Producto prod = ventaService.buscarProducto(idBusqueda);
            if (prod != null) {
                System.out.printf("  -> Encontrado: %s | Precio: $%.2f | Stock actual: %d%n",
                        prod.getNombre(), prod.getPrecio(), prod.getStock());
            } else {
                System.out.println("  -> Producto no encontrado.");
            }

            // 3. Realizar una venta exitosa
            int cantidadCompra = 5;
            System.out.printf("\n[CLIENTE VENTAS] Solicitando compra de %d unidades del producto ID %d...%n", 
                    cantidadCompra, idBusqueda);
            String respuestaVenta = ventaService.realizarVenta(idBusqueda, cantidadCompra);
            System.out.println("  -> Respuesta Servidor: " + respuestaVenta);

            // 4. Buscar nuevamente para validar stock actualizado
            System.out.println("\n[CLIENTE VENTAS] Comprobando actualización de stock en el servidor...");
            prod = ventaService.buscarProducto(idBusqueda);
            if (prod != null) {
                System.out.printf("  -> Estado actual: %s | Stock restante: %d (Debería ser %d)%n",
                        prod.getNombre(), prod.getStock(), (25 - cantidadCompra));
            }

            // 5. Simular una venta fallida por stock insuficiente
            int cantidadExcesiva = 50;
            System.out.printf("\n[CLIENTE VENTAS] Solicitando compra excesiva de %d unidades del producto ID %d...%n", 
                    cantidadExcesiva, idBusqueda);
            String respuestaFalla = ventaService.realizarVenta(idBusqueda, cantidadExcesiva);
            System.out.println("  -> Respuesta Servidor: " + respuestaFalla);

            System.out.println("\n[CLIENTE VENTAS] Pruebas funcionales de ventas completadas con éxito.");
        } catch (Exception e) {
            System.err.println("[CLIENTE VENTAS] [ERROR] Ocurrió un fallo durante la ejecución:");
            e.printStackTrace();
        }
    }
}
