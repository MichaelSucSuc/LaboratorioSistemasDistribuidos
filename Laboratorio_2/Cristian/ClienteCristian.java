package Laboratorio_2.Cristian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClienteCristian {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 5000;

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        try {
            long t0 = System.currentTimeMillis();
            System.out.println("Tiempo t0 (inicio): " + t0 + " ms");

            Socket socket = new Socket(host, puerto);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            String horaServidor = in.readLine();

            long t1 = System.currentTimeMillis();
            System.out.println("Tiempo t1 (recepción): " + t1 + " ms");

            long RTT = t1 - t0;

            System.out.println("→ Hora recibida del servidor: " + horaServidor);
            System.out.println("→ RTT calculado: " + RTT + " ms");

            LocalTime horaSync = LocalTime.parse(horaServidor, formato);

            horaSync = horaSync.plusNanos((RTT / 2) * 1_000_000);

            System.out.println("→ Hora estimada sincronizada del cliente: "
                    + horaSync.format(formato));

            socket.close();

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}