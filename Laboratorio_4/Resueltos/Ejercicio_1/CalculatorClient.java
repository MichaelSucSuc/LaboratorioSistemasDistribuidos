import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

public class CalculatorClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java CalculatorClient <num1> <num2>");
            return;
        }
        
        int num1 = Integer.parseInt(args[0]);
        int num2 = Integer.parseInt(args[1]);
        
        try {
            Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/CalculatorService");
            
            System.out.println("La resta de " + num1 + " y " + num2 + " es: " + c.sub(num1, num2));
            System.out.println("La suma de " + num1 + " y " + num2 + " es: " + c.add(num1, num2));
            System.out.println("La multiplicación de " + num1 + " y " + num2 + " es: " + c.mul(num1, num2));
            System.out.println("La división de " + num1 + " y " + num2 + " es: " + c.div(num1, num2));
            
        } catch (MalformedURLException mrule) {
            System.out.println("MalformedURLException: " + mrule);
        } catch (RemoteException re) {
            System.out.println("RemoteException: " + re);
        } catch (NotBoundException nbe) {
            System.out.println("NotBoundException: " + nbe);
        } catch (ArithmeticException ae) {
            System.out.println("ArithmeticException: " + ae);
        }
    }
}