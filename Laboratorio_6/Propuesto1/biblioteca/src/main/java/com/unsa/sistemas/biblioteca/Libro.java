package com.unsa.sistemas.biblioteca;

public class Libro {
    private int id;
    private String titulo;

    // Constructor vacío obligatorio
    public Libro() {
    }

    // Constructor con parámetros
    public Libro(int id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
}