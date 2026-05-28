package com.unsa.sistemas.biblioteca;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {

    // Lista en memoria para simular el almacenamiento
    private final List<Libro> libros = new ArrayList<>();

    // Inicializamos con un par de libros por defecto
    public LibroController() {
        libros.add(new Libro(1, "Sistemas Distribuidos - Tanenbaum"));
        libros.add(new Libro(2, "Clean Code - Robert C. Martin"));
    }

    // 1. Listar libros (GET)
    @GetMapping
    public List<Libro> listar() {
        return libros;
    }

    // 2. Registrar libro (POST)
    @PostMapping
    public ResponseEntity<Libro> agregar(@RequestBody Libro nuevoLibro) {
        libros.add(nuevoLibro);
        return new ResponseEntity<>(nuevoLibro, HttpStatus.CREATED);
    }

    // 3. Buscar por ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Libro> buscarPorId(@PathVariable int id) {
        for (Libro libro : libros) {
            if (libro.getId() == id) {
                return ResponseEntity.ok(libro);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // 4. Eliminar libro (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        boolean eliminado = libros.removeIf(libro -> libro.getId() == id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}