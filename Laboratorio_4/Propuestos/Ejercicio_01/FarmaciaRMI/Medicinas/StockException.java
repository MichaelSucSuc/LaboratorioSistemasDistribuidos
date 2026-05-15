package Medicinas;

public class StockException extends Exception {
    
    // Constructor basico
    public StockException(String msg) {
        super(msg);
    }
    
    // Constructor con tipo de error especifico
    public StockException(String msg, String tipoError) {
        super(msg + " - Tipo: " + tipoError);
    }
    
    // Metodo estatico para cantidad negativa o cero
    public static StockException cantidadNegativa() {
        return new StockException("ERROR: La cantidad ingresada es negativa o cero", "CANTIDAD_INVALIDA");
    }
    
    // Metodo estatico para stock insuficiente
    public static StockException stockInsuficiente(int disponible, int solicitado) {
        return new StockException(
            String.format("ERROR: Stock insuficiente. Disponible: %d, Solicitado: %d", disponible, solicitado),
            "STOCK_INSUFICIENTE"
        );
    }
    
    // Metodo estatico para sin stock
    public static StockException sinStock(String medicina) {
        return new StockException("ERROR: La medicina '" + medicina + "' no tiene stock disponible", "SIN_STOCK");
    }
    
    // Metodo estatico para producto no encontrado
    public static StockException productoNoEncontrado(String medicina) {
        return new StockException("ERROR: La medicina '" + medicina + "' no existe en el inventario", "PRODUCTO_NO_ENCONTRADO");
    }
}