package Medicinas;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.Scanner;

public class ClienteSide {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        StockInterface pharm = (StockInterface) Naming.lookup("rmi://localhost:1099/PHARMACY");
        
        System.out.println("\n=== FARMACIA ONLINE - SISTEMA RMI ===\n");
        
        int selection = 0;
        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1: Listar productos");
            System.out.println("2: Comprar producto");
            System.out.println("3: Salir");
            System.out.print("Ingresa la opcion: ");

            String line = sc.nextLine();
            try {
                selection = Integer.parseInt(line.trim());
            } catch (NumberFormatException nfe) {
                System.out.println("\nSeleccione una opcion valida (1, 2 o 3)");
                continue;
            }

            if (selection == 1) {
                System.out.println("\n--- LISTA DE PRODUCTOS ---");
                HashMap<String, MedicineInterface> aux = (HashMap<String, MedicineInterface>) pharm.getStockProducts();
                for (String key : aux.keySet()) {
                    MedicineInterface e = aux.get(key);
                    System.out.println(e.print());
                    System.out.println("*--------------*");
                }
            }
            else if (selection == 2) {
                System.out.println("\n--- REALIZAR COMPRA ---");
                System.out.print("Ingrese nombre de la medicina: ");
                String medicine = sc.nextLine().trim();
                if (medicine.isEmpty()) {
                    System.out.println("Nombre de medicina no puede estar vacío");
                    continue;
                }
                System.out.print("Ingrese cantidad a comprar: ");
                String amtLine = sc.nextLine();
                int amount;
                try {
                    amount = Integer.parseInt(amtLine.trim());
                } catch (NumberFormatException nfe) {
                    System.out.println("\nIngrese una cantidad válida (número entero)");
                    continue;
                }

                try {
                    MedicineInterface aux = pharm.buyMedicine(medicine, amount);
                    System.out.println("\n==========================================");
                    System.out.println("COMPRA REALIZADA CON EXITO");
                    System.out.println("==========================================");
                    System.out.println("Usted acaba de comprar:");
                    System.out.println(aux.print());
                    System.out.println("==========================================");
                } catch (StockException e) {
                    System.out.println("\n==========================================");
                    System.out.println("ERROR: " + e.getMessage());
                    System.out.println("==========================================");
                } catch (Exception e) {
                    System.out.println("\n==========================================");
                    System.out.println("ERROR: " + e.getMessage());
                    System.out.println("==========================================");
                }
            }
            else if (selection == 3) {
                System.out.println("\nGracias por su compra");
            }
            else {
                System.out.println("\nSeleccione una opcion valida (1, 2 o 3)");
            }
        } while (selection != 3);
        
        sc.close();
    }
}