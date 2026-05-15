package Laboratorio_4.Propuestos.Ejercicio_03;

import java.rmi.Naming;
import java.util.Scanner;

public class ClienteConversor {

    public static void main(String[] args) {
        try {
            ConversorMonedaInterface conversor =
                    (ConversorMonedaInterface) Naming.lookup("rmi://localhost:1099/ConversorMonedaService");

            Scanner sc = new Scanner(System.in);

            System.out.print("Ingrese monto en soles: S/ ");
            double monto = sc.nextDouble();

            double dolares = conversor.convertirADolares(monto);
            double euros = conversor.convertirAEuros(monto);

            System.out.println("\n--- RESULTADO DE CONVERSIÓN ---");
            System.out.println("Monto en soles: S/ " + monto);
            System.out.printf("Equivalente en dólares: $ %.2f%n", dolares);
            System.out.printf("Equivalente en euros: € %.2f%n", euros);

            sc.close();

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}