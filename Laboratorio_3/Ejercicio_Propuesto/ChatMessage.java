package Laboratorio_3.Ejercicio_Propuesto;

import java.io.*;

/* Esta clase define los diferentes tipos de mensajes que se intercambiarán entre los Clientes y el Servidor. */
public class ChatMessage implements Serializable {
    // Tipos de mensajes:
    // WHOISIN: recibir lista de usuarios conectados
    // MESSAGE: mensaje de texto ordinario
    // LOGOUT: desconectarse del servidor
    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
    private int type;
    private String message;

    // Constructor
    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    int getType() { return type; }
    String getMessage() { return message; }
}