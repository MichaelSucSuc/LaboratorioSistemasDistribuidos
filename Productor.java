public class Productor extends Thread {
    private CubbyHole cubbyhole;
    private int numero;

    public Productor(CubbyHole c, int numero) {
        cubbyhole = c;
        this.numero = numero;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                cubbyhole.put(i);
            } catch (IllegalStateException e) {
                return;
            }
            System.out.println("Productor #" + this.numero + " pone: " + i);
            try {
                sleep((int) (Math.random() * 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
