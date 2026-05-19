package ejercicios_propuestos.rmi;

import java.rmi.Naming;

public class Client {
    public static void main(String[] args) {
        try {
            Calculator calc = (Calculator) Naming.lookup("rmi://localhost/CalculatorService");
            System.out.println("2 * 3 = " + calc.multiply(2, 3));
            System.out.println("10 / 2 = " + calc.divide(10, 2));
            System.out.println("2 ^ 8 = " + calc.power(2, 8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
