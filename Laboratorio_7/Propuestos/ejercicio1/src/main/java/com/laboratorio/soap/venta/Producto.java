package com.laboratorio.soap.venta;

import java.io.Serializable;

/**
 * POJO que representa un Producto en el catálogo de ventas.
 * Incluye atributos para identificación, nombre, precio y disponibilidad de inventario (stock).
 * Es serializable para su correcta transmisión y compatible con JAXB.
 */
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private double precio;
    private int stock;

    /**
     * Constructor por defecto (necesario para la serialización/deserialización JAXB).
     */
    public Producto() {
    }

    /**
     * Constructor con todos los campos.
     *
     * @param id     Identificador del producto.
     * @param nombre Nombre descriptivo del producto.
     * @param precio Precio unitario.
     * @param stock  Cantidad disponible en inventario.
     */
    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // --- GETTERS Y SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                '}';
    }
}
