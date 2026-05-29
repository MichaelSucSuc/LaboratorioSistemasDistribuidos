const API_URL = "/api/estudiantes";

document.addEventListener("DOMContentLoaded", () => {
    listarEstudiantes();
});

async function listarEstudiantes() {
    const respuesta = await fetch(API_URL);
    const estudiantes = await respuesta.json();

    const tabla = document.getElementById("tablaEstudiantes");
    tabla.innerHTML = "";

    if (estudiantes.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="6">No hay estudiantes registrados.</td>
            </tr>
        `;
        return;
    }

    estudiantes.forEach(estudiante => {
        tabla.innerHTML += `
            <tr>
                <td>${estudiante.id}</td>
                <td>${estudiante.nombre}</td>
                <td>${estudiante.correo}</td>
                <td>${estudiante.carrera}</td>
                <td>${estudiante.semestre}</td>
                <td>
                    <button class="editar" onclick="cargarParaEditar(${estudiante.id})">Editar</button>
                    <button class="eliminar" onclick="eliminarEstudiante(${estudiante.id})">Eliminar</button>
                </td>
            </tr>
        `;
    });
}

async function guardarEstudiante() {
    const id = document.getElementById("idEstudiante").value;

    const estudiante = {
        nombre: document.getElementById("nombre").value.trim(),
        correo: document.getElementById("correo").value.trim(),
        carrera: document.getElementById("carrera").value.trim(),
        semestre: parseInt(document.getElementById("semestre").value)
    };

    if (!estudiante.nombre || !estudiante.correo || !estudiante.carrera || !estudiante.semestre) {
        mostrarMensaje("Completa todos los campos.", true);
        return;
    }

    let respuesta;

    if (id) {
        respuesta = await fetch(`${API_URL}/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(estudiante)
        });
    } else {
        respuesta = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(estudiante)
        });
    }

    const resultado = await respuesta.json();

    if (!respuesta.ok) {
        mostrarMensaje(resultado.mensaje, true);
        return;
    }

    mostrarMensaje(resultado.mensaje, false);
    limpiarFormulario();
    listarEstudiantes();
}

async function cargarParaEditar(id) {
    const respuesta = await fetch(`${API_URL}/${id}`);
    const estudiante = await respuesta.json();

    if (!respuesta.ok) {
        mostrarMensaje(estudiante.mensaje, true);
        return;
    }

    document.getElementById("idEstudiante").value = estudiante.id;
    document.getElementById("nombre").value = estudiante.nombre;
    document.getElementById("correo").value = estudiante.correo;
    document.getElementById("carrera").value = estudiante.carrera;
    document.getElementById("semestre").value = estudiante.semestre;

    document.getElementById("tituloFormulario").textContent = "Actualizar estudiante";
    document.getElementById("btnGuardar").textContent = "Actualizar";
}

async function eliminarEstudiante(id) {
    const confirmar = confirm("¿Seguro que deseas eliminar este estudiante?");

    if (!confirmar) {
        return;
    }

    const respuesta = await fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    });

    const resultado = await respuesta.json();

    if (!respuesta.ok) {
        mostrarMensaje(resultado.mensaje, true);
        return;
    }

    mostrarMensaje(resultado.mensaje, false);
    listarEstudiantes();
}

function limpiarFormulario() {
    document.getElementById("idEstudiante").value = "";
    document.getElementById("nombre").value = "";
    document.getElementById("correo").value = "";
    document.getElementById("carrera").value = "";
    document.getElementById("semestre").value = "";

    document.getElementById("tituloFormulario").textContent = "Registrar estudiante";
    document.getElementById("btnGuardar").textContent = "Registrar";
}

function mostrarMensaje(texto, error) {
    const mensaje = document.getElementById("mensaje");
    mensaje.textContent = texto;
    mensaje.style.color = error ? "#e63946" : "#2a9d8f";
}