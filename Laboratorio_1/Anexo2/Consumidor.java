public class Consumidor extends Thread {
    private CubbyHole cubbyhole;
    private int numero;

    public Consumidor(CubbyHole c, int numero) {
        cubbyhole = c;
        this.numero = numero;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                int value = cubbyhole.get();
                System.out.println("Consumidor #" + this.numero + " obtiene: " + value);
            } catch (IllegalStateException e) {
                return;
            }
        }
    }
}
