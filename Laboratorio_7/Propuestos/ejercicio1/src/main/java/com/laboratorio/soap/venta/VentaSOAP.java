package com.laboratorio.soap.venta;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Interfaz de Endpoint de Servicio (SEI) para la gestión de ventas de productos.
 * Define el contrato del servicio SOAP.
 */
@WebService(
    targetNamespace = "http://venta.soap.laboratorio.com/"
)
public interface VentaSOAP {

    /**
     * Obtiene la lista completa de productos disponibles en el inventario.
     *
     * @return Lista de objetos Producto.
     */
    @WebMethod(operationName = "listarProductos")
    List<Producto> listarProductos();

    /**
     * Busca un producto específico por su identificador único.
     *
     * @param id Identificador del producto a buscar.
     * @return El objeto Producto encontrado o null si no existe.
     */
    @WebMethod(operationName = "buscarProducto")
    Producto buscarProducto(@WebParam(name = "productoId") int id);

    /**
     * Realiza la simulación de una venta descontando el stock del producto.
     *
     * @param productoId Identificador único del producto.
     * @param cantidad   Cantidad de unidades a comprar.
     * @return Mensaje informativo indicando el éxito de la venta o el motivo del error.
     */
    @WebMethod(operationName = "realizarVenta")
    String realizarVenta(
            @WebParam(name = "productoId") int productoId,
            @WebParam(name = "cantidad") int cantidad);
}
