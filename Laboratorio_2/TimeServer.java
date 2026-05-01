import java.io.*;
import java.net.*;
import java.util.Date;

public class TimeServer {
    public static void main(String[] args) {
        final int PORT = 12345;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de tiempo escuchando en el puerto " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    // Enviamos la hora actual del sistema en milisegundos
                    long currentTime = System.currentTimeMillis();
                    out.println(currentTime);
                    System.out.println("Hora enviada: " + new Date(currentTime));
                } catch (IOException e) {
                    System.err.println("Error al atender cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}