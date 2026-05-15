import java.rmi.Naming;

public class CalculatorServer {
    public CalculatorServer() {
        try {
            Calculator c = new CalculatorImpl();
            Naming.rebind("rmi://localhost:1099/CalculatorService", c);
            System.out.println("Servidor de calculadora listo y registrado.");
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }
    
    public static void main(String args[]) {
        new CalculatorServer();
    }
}