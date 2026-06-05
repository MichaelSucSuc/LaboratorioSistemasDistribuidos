package com.laboratorio.soap.venta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.jws.WebService;

/**
 * Clase de implementación (SIB) para el servicio web VentaSOAP.
 * Vincula esta clase con la interfaz del endpoint mediante la propiedad endpointInterface.
 */
@WebService(
    endpointInterface = "com.laboratorio.soap.venta.VentaSOAP",
    targetNamespace = "http://venta.soap.laboratorio.com/",
    serviceName = "VentaSOAPService",
    portName = "VentaSOAPPort"
)
public class VentaSOAPImpl implements VentaSOAP {

    // Almacén de productos en memoria
    private static final Map<Integer, Producto> inventario = new ConcurrentHashMap<>();

    // Inicialización del inventario con datos de prueba
    static {
        inventario.put(101, new Producto(101, "Laptop Dell XPS 15", 1899.99, 10));
        inventario.put(102, new Producto(102, "Mouse Inalámbrico Logitech MX Master 3S", 99.90, 25));
        inventario.put(103, new Producto(103, "Teclado Mecánico Keychron K2", 89.00, 15));
        inventario.put(104, new Producto(104, "Monitor ASUS ProArt 27\"", 349.50, 8));
        inventario.put(105, new Producto(105, "Auriculares Sony WH-1000XM5", 399.00, 12));
    }

    @Override
    public List<Producto> listarProductos() {
        return new ArrayList<>(inventario.values());
    }

    @Override
    public Producto buscarProducto(int id) {
        return inventario.get(id);
    }

    @Override
    public String realizarVenta(int productoId, int cantidad) {
        Producto producto = inventario.get(productoId);
        
        if (producto == null) {
            return "Error: El producto con ID " + productoId + " no existe.";
        }
        
        if (cantidad <= 0) {
            return "Error: La cantidad solicitada debe ser mayor a cero.";
        }

        // Sincronización sobre la instancia del producto para evitar condiciones de carrera
        synchronized (producto) {
            int stockActual = producto.getStock();
            if (stockActual < cantidad) {
                return "Error: Stock insuficiente. Stock actual disponible: " + stockActual;
            }
            
            // Descontar del stock
            producto.setStock(stockActual - cantidad);
            double totalAPagar = producto.getPrecio() * cantidad;
            
            return String.format("Venta exitosa. Producto: %s. Cantidad: %d. Total a pagar: $%.2f. Stock restante: %d.",
                    producto.getNombre(), cantidad, totalAPagar, producto.getStock());
        }
    }
}
