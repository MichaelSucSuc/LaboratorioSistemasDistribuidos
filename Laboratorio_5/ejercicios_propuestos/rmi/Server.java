package ejercicios_propuestos.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            CalculatorImpl obj = new CalculatorImpl();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("CalculatorService", obj);
            System.out.println("CalculatorService bound. Server ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
