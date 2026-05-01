import java.io.*;
import java.net.*;
import java.util.Date;

public class CristianClockClient {
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 12345;

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Tiempo justo antes de enviar la petición (hora local del cliente)
            long t0 = System.currentTimeMillis();

            // Recibir la hora del servidor
            String serverTimeStr = in.readLine();
            long t1 = System.currentTimeMillis(); // Tiempo justo después de recibir la respuesta

            if (serverTimeStr != null) {
                long serverTime = Long.parseLong(serverTimeStr);
                // Calcular el retardo de ida y vuelta (RTT)
                long rtt = t1 - t0;
                // Estimar la hora real del servidor en el momento de la recepción
                long adjustedTime = serverTime + (rtt / 2);

                System.out.println("=== Algoritmo de Cristian ===");
                System.out.println("Hora local antes de la consulta: " + new Date(t0));
                System.out.println("Hora enviada por el servidor:   " + new Date(serverTime));
                System.out.println("Hora local después de recibir:  " + new Date(t1));
                System.out.println("RTT (ms): " + rtt);
                System.out.println("Mitad del RTT (ms): " + (rtt / 2));
                System.out.println("Hora ajustada (cliente):       " + new Date(adjustedTime));
            } else {
                System.err.println("El servidor no envió una hora válida.");
            }

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}