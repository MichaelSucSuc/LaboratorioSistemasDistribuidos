package LaboratorioSistemasDistribuidos.Laboratorio_2;
import java.util.Arrays;

public class BerkeleyAsistencia {

    public static void main(String[] args) {

        // Dispositivos del sistema de asistencia
        String[] nodos = {"Servidor", "Entrada", "Aula A", "Aula B"};

        // Tiempos simulados (en milisegundos)
        long[] tiempos = {1000, 995, 1020, 980};

        int n = tiempos.length;

        System.out.println("=== Sincronización de Asistencia (Berkeley) ===\n");

        // Mostrar tiempos iniciales
        System.out.println("Tiempos iniciales:");
        for (int i = 0; i < n; i++) {
            System.out.println(nodos[i] + ": " + tiempos[i] + " ms");
        }

        // 1. El servidor recolecta los tiempos
        long suma = 0;
        for (long t : tiempos) {
            suma += t;
        }

        // 2. Calcula el promedio
        long promedio = suma / n;
        System.out.println("\nPromedio calculado por el servidor: " + promedio + " ms");

        // 3. Ajuste de cada nodo
        System.out.println("\nAplicando ajustes:");
        for (int i = 0; i < n; i++) {

            long ajuste = promedio - tiempos[i];
            tiempos[i] += ajuste;

            System.out.println(nodos[i] +
                    " ajuste: " + ajuste +
                    " ms -> Nueva hora: " + tiempos[i] + " ms");
        }

        // 4. Resultado final
        System.out.println("\nTiempos sincronizados:");
        for (int i = 0; i < n; i++) {
            System.out.println(nodos[i] + ": " + tiempos[i] + " ms");
        }
    }
}