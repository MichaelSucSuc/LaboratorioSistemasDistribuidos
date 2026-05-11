package Laboratorio_2.Cristian;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServidorTiempo {
    public static void main(String[] args) {
        int puerto = 5000;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de tiempo listo...");
            System.out.println("Escuchando en el puerto " + puerto);

            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());

                OutputStream os = cliente.getOutputStream();
                PrintWriter out = new PrintWriter(os, true);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    System.err.println("Error en el retardo simulado: " + e.getMessage());
                }

                LocalTime horaActual = LocalTime.now();
                String hora = horaActual.format(formato);

                out.println(hora);
                System.out.println("Hora enviada al cliente: " + hora);

                cliente.close();
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}