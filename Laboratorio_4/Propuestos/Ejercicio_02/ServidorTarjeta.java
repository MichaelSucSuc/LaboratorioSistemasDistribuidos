package Laboratorio_4.Propuestos.Ejercicio_02;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorTarjeta {

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            TarjetaCreditoInterface tarjeta = new TarjetaCreditoImpl(3000.00);

            Naming.rebind("rmi://localhost:1099/TarjetaCreditoService", tarjeta);

            System.out.println("Servidor de tarjeta de credito listo...");
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}