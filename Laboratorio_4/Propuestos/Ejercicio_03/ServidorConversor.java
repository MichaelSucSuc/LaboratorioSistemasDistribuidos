package Laboratorio_4.Propuestos.Ejercicio_03;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorConversor {

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            ConversorMonedaInterface conversor = new ConversorMonedaImpl();

            Naming.rebind("rmi://localhost:1099/ConversorMonedaService", conversor);

            System.out.println("Servidor de conversión de moneda listo...");
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}