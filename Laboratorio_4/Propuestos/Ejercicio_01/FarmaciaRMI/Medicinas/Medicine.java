package Medicinas;

import java.rmi.server.UnicastRemoteObject;

public class Medicine extends UnicastRemoteObject implements MedicineInterface {
    private String name;
    private float unitPrice;
    private int stock;
    
    public Medicine() throws Exception {
        super();
    }
    
    public Medicine(String name, float price, int stock) throws Exception {
        super();
        this.name = name;
        unitPrice = price;
        this.stock = stock;
    }
    
    @Override
    public Medicine getMedicine(int amount) throws Exception {
        // Validar cantidad negativa o cero
        if (amount <= 0) {
            throw StockException.cantidadNegativa();
        }
        
        // Validar stock vacio
        if (this.stock <= 0) {
            throw StockException.sinStock(this.name);
        }
        
        // Validar stock insuficiente
        if (this.stock - amount < 0) {
            throw StockException.stockInsuficiente(this.stock, amount);
        }
        
        // Realizar la compra
        this.stock -= amount;
        Medicine aux = new Medicine(name, unitPrice * amount, stock);
        return aux;
    }
    
    @Override
    public int getStock() throws Exception {
        return this.stock;
    }
    
    public String print() throws Exception {
        return this.name + "\nPrice: $" + this.unitPrice + "\nStock: " + this.stock;
    }
}