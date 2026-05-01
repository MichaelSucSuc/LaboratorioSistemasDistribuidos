import java.util.Arrays;

public class BerkeleyAlgorithm {
    public static void main(String[] args) {
        // Horas actuales en diferentes nodos (simulación)
        // El índice 0 es el Maestro, los demás son Esclavos
        long[] nodeTimes = {1000, 1020, 980, 1010}; 
        int n = nodeTimes.length;

        System.out.println("Horas iniciales: " + Arrays.toString(nodeTimes));

        // 1. El maestro calcula la diferencia respecto a su propio reloj
        long masterTime = nodeTimes[0];
        long[] differences = new long[n];
        long sumDifferences = 0;

        for (int i = 0; i < n; i++) {
            differences[i] = nodeTimes[i] - masterTime;
            sumDifferences += differences[i];
        }

        // 2. Calcular el promedio de las diferencias
        long averageDifference = sumDifferences / n;
        System.out.println("Ajuste promedio calculado: " + averageDifference);

        // 3. Sincronizar todos los relojes
        System.out.println("\nRelojes Sincronizados:");
        for (int i = 0; i < n; i++) {
            // Cada nodo ajusta su hora sumando (Promedio - Su Diferencia)
            long adjustment = averageDifference - differences[i];
            nodeTimes[i] += adjustment;
            System.out.println("Nodo " + i + ": " + nodeTimes[i] + " (Ajuste: " + adjustment + ")");
        }
    }
}