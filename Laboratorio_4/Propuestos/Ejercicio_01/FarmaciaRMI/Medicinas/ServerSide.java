package Medicinas;

import java.rmi.Naming;

public class ServerSide {
    public static void main(String[] args) throws Exception {
        Stock pharmacy = new Stock();
        pharmacy.addMedicine("Paracetamol", 3.2f, 10);
        pharmacy.addMedicine("Mejoral", 2.0f, 20);
        pharmacy.addMedicine("Amoxilina", 1.0f, 30);
        pharmacy.addMedicine("Aspirina", 5.0f, 40);
        
        Naming.rebind("rmi://localhost:1099/PHARMACY", pharmacy);
        
        System.out.println("Server ready en localhost:1099");
        System.out.println("Medicinas disponibles:");
        System.out.println("  - Paracetamol (10 unidades) - $3.2 c/u");
        System.out.println("  - Mejoral (20 unidades) - $2.0 c/u");
        System.out.println("  - Amoxilina (30 unidades) - $1.0 c/u");
        System.out.println("  - Aspirina (40 unidades) - $5.0 c/u");
        System.out.println("\nEsperando conexiones de clientes...");
    }
}