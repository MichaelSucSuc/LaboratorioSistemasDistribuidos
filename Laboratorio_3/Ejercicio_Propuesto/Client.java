package Laboratorio_3.Ejercicio_Propuesto;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private String notif = "***";
    private ObjectInputStream sInput;   // Para leer del socket
    private ObjectOutputStream sOutput; // Para escribir en el socket
    private Socket socket;
    private String server, username;
    private int port;

    Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch(Exception ec) {
            display("Error conectando al servidor:" + ec);
            return false;
        }

        display("Conexión aceptada " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Excepción creando flujos E/S: " + eIO);
            return false;
        }

        new ListenFromServer().start();

        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            display("Excepción realizando login: " + eIO);
            disconnect();
            return false;
        }
        return true;
    }

    private void display(String msg) {
        System.out.println(msg);
    }

    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch(IOException e) {
            display("Excepción escribiendo al servidor: " + e);
        }
    }

    private void disconnect() {
        try { if(sInput != null) sInput.close(); } catch(Exception e) {}
        try { if(sOutput != null) sOutput.close(); } catch(Exception e) {}
        try { if(socket != null) socket.close(); } catch(Exception e) {}
    }

    public static void main(String[] args) {
        int portNumber = 1500;
        String serverAddress = "localhost";
        String userName = "Anonymous";
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Ingrese su nombre de usuario: ");
        userName = scan.nextLine();

        Client client = new Client(serverAddress, portNumber, userName);
        if(!client.start()) return;

        System.out.println("\nBienvenido a la sala de chat.");
        System.out.println("Instrucciones:");
        System.out.println("1. Escribe para enviar un mensaje a todos.");
        System.out.println("2. '@username mensaje' para mensaje privado.");
        System.out.println("3. 'WHOISIN' para ver conectados.");
        System.out.println("4. 'LOGOUT' para salir.");

        while(true) {
            System.out.print("> ");
            String msg = scan.nextLine();
            if(msg.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                break;
            } else if(msg.equalsIgnoreCase("WHOISIN")) {
                client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
            } else {
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
            }
        }
        scan.close();
        client.disconnect();
    }

    class ListenFromServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String msg = (String) sInput.readObject();
                    System.out.println(msg);
                    System.out.print("> ");
                } catch(IOException e) {
                    display(notif + "El servidor ha cerrado la conexión: " + e + notif);
                    break;
                } catch(ClassNotFoundException e2) {}
            }
        }
    }
}