package Laboratorio_4.Propuestos.Ejercicio_02;
import java.rmi.Naming;
import java.util.Scanner;

public class ClienteTarjeta {

    public static void main(String[] args) {
        try {
            TarjetaCreditoInterface tarjeta =
                    (TarjetaCreditoInterface) Naming.lookup("rmi://localhost:1099/TarjetaCreditoService");

            Scanner sc = new Scanner(System.in);
            int opcion;

            do {
                System.out.println("\n--- SISTEMA SIMPLE DE TARJETA DE CRÉDITO ---");
                System.out.println("1. Consultar estado");
                System.out.println("2. Realizar compra");
                System.out.println("3. Realizar pago");
                System.out.println("4. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = sc.nextInt();

                switch (opcion) {
                    case 1:
                        System.out.println(tarjeta.obtenerEstado());
                        break;

                    case 2:
                        System.out.print("Ingrese monto de compra: S/ ");
                        double compra = sc.nextDouble();

                        if (tarjeta.realizarCompra(compra)) {
                            System.out.println("Compra realizada correctamente.");
                        } else {
                            System.out.println("Compra rechazada. Verifique el monto o el límite disponible.");
                        }
                        break;

                    case 3:
                        System.out.print("Ingrese monto de pago: S/ ");
                        double pago = sc.nextDouble();

                        if (tarjeta.realizarPago(pago)) {
                            System.out.println("Pago realizado correctamente.");
                        } else {
                            System.out.println("Pago inválido.");
                        }
                        break;

                    case 4:
                        System.out.println("Saliendo del sistema...");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }

            } while (opcion != 4);

            sc.close();

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}