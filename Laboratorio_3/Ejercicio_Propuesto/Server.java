package Laboratorio_3.Ejercicio_Propuesto;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    private static int uniqueId;
    private ArrayList<ClientThread> al;
    private SimpleDateFormat sdf;
    private int port;
    private boolean keepGoing;
    private String notif = " *** ";

    public Server(int port) {
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        al = new ArrayList<ClientThread>();
    }

    public void start() {
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(keepGoing) {
                display("Servidor esperando clientes en el puerto " + port + ".");
                Socket socket = serverSocket.accept();
                if(!keepGoing) break;
                ClientThread t = new ClientThread(socket);
                al.add(t);
                t.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            display(sdf.format(new Date()) + " Excepción en ServerSocket: " + e);
        }
    }

    private void display(String msg) {
        System.out.println(sdf.format(new Date()) + " " + msg);
    }

    private synchronized boolean broadcast(String message) {
        String time = sdf.format(new Date());
        String[] w = message.split(" ", 3);
        boolean isPrivate = w.length > 1 && w[1].startsWith("@");

        if(isPrivate) {
            String tocheck = w[1].substring(1);
            String messageLf = time + " [Privado] " + w[0] + w[2] + "\n";
            for(int y = al.size(); --y >= 0;) {
                ClientThread ct1 = al.get(y);
                if(ct1.username.equals(tocheck)) {
                    if(!ct1.writeMsg(messageLf)) {
                        al.remove(y);
                        display("Cliente desconectado " + ct1.username + " removido.");
                    }
                    return true;
                }
            }
            return false;
        } else {
            String messageLf = time + " " + message + "\n";
            System.out.print(messageLf);
            for(int i = al.size(); --i >= 0;) {
                ClientThread ct = al.get(i);
                if(!ct.writeMsg(messageLf)) {
                    al.remove(i);
                    display("Cliente desconectado " + ct.username + " removido.");
                }
            }
        }
        return true;
    }

    synchronized void remove(int id) {
        for(int i = 0; i < al.size(); ++i) {
            if(al.get(i).id == id) {
                String user = al.get(i).username;
                al.remove(i);
                broadcast(notif + user + " ha dejado el chat." + notif);
                break;
            }
        }
    }

    public static void main(String[] args) {
        int portNumber = 1500;
        Server server = new Server(portNumber);
        server.start();
    }

    class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        String date;

        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                broadcast(notif + username + " se ha unido al chat." + notif);
            } catch (IOException | ClassNotFoundException e) {
                display("Excepción creando flujos: " + e);
            }
            date = new Date().toString() + "\n";
        }

        public void run() {
            boolean keepGoing = true;
            while(keepGoing) {
                try {
                    ChatMessage cm = (ChatMessage) sInput.readObject();
                    String message = cm.getMessage();
                    switch(cm.getType()) {
                        case ChatMessage.MESSAGE:
                            broadcast(username + ": " + message);
                            break;
                        case ChatMessage.LOGOUT:
                            keepGoing = false;
                            break;
                        case ChatMessage.WHOISIN:
                            writeMsg("Lista de usuarios conectados a las " + sdf.format(new Date()) + "\n");
                            for(int i = 0; i < al.size(); ++i) {
                                ClientThread ct = al.get(i);
                                writeMsg((i+1) + ") " + ct.username + " desde " + ct.date);
                            }
                            break;
                    }
                } catch (IOException | ClassNotFoundException e) { break; }
            }
            remove(id);
            close();
        }

        private void close() {
            try { if(sOutput != null) sOutput.close(); } catch(Exception e) {}
            try { if(sInput != null) sInput.close(); } catch(Exception e) {}
            try { if(socket != null) socket.close(); } catch(Exception e) {}
        }

        private boolean writeMsg(String msg) {
            if(!socket.isConnected()) { close(); return false; }
            try { sOutput.writeObject(msg); } catch(IOException e) { return false; }
            return true;
        }
    }
}