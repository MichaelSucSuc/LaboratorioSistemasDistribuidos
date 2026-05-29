from flask import Flask, request, jsonify, render_template
import sqlite3

app = Flask(__name__)

DATABASE = "estudiantes.db"


def obtener_conexion():
    conexion = sqlite3.connect(DATABASE)
    conexion.row_factory = sqlite3.Row
    return conexion


def inicializar_bd():
    conexion = obtener_conexion()
    cursor = conexion.cursor()

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS estudiantes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            correo TEXT NOT NULL,
            carrera TEXT NOT NULL,
            semestre INTEGER NOT NULL
        )
    """)

    conexion.commit()
    conexion.close()


@app.route("/")
def index():
    return render_template("index.html")


@app.route("/api/estudiantes", methods=["GET"])
def listar_estudiantes():
    conexion = obtener_conexion()
    estudiantes = conexion.execute("SELECT * FROM estudiantes").fetchall()
    conexion.close()

    resultado = [dict(estudiante) for estudiante in estudiantes]
    return jsonify(resultado), 200


@app.route("/api/estudiantes/<int:id>", methods=["GET"])
def buscar_estudiante(id):
    conexion = obtener_conexion()
    estudiante = conexion.execute(
        "SELECT * FROM estudiantes WHERE id = ?",
        (id,)
    ).fetchone()
    conexion.close()

    if estudiante is None:
        return jsonify({"mensaje": "Estudiante no encontrado"}), 404

    return jsonify(dict(estudiante)), 200


@app.route("/api/estudiantes", methods=["POST"])
def registrar_estudiante():
    data = request.get_json()

    if not data:
        return jsonify({"mensaje": "No se enviaron datos"}), 400

    nombre = data.get("nombre")
    correo = data.get("correo")
    carrera = data.get("carrera")
    semestre = data.get("semestre")

    if not nombre or not correo or not carrera or semestre is None:
        return jsonify({"mensaje": "Todos los campos son obligatorios"}), 400

    conexion = obtener_conexion()
    cursor = conexion.cursor()

    cursor.execute("""
        INSERT INTO estudiantes (nombre, correo, carrera, semestre)
        VALUES (?, ?, ?, ?)
    """, (nombre, correo, carrera, semestre))

    conexion.commit()
    nuevo_id = cursor.lastrowid
    conexion.close()

    return jsonify({
        "mensaje": "Estudiante registrado correctamente",
        "estudiante": {
            "id": nuevo_id,
            "nombre": nombre,
            "correo": correo,
            "carrera": carrera,
            "semestre": semestre
        }
    }), 201


@app.route("/api/estudiantes/<int:id>", methods=["PUT"])
def actualizar_estudiante(id):
    data = request.get_json()

    if not data:
        return jsonify({"mensaje": "No se enviaron datos"}), 400

    conexion = obtener_conexion()
    estudiante = conexion.execute(
        "SELECT * FROM estudiantes WHERE id = ?",
        (id,)
    ).fetchone()

    if estudiante is None:
        conexion.close()
        return jsonify({"mensaje": "Estudiante no encontrado"}), 404

    nombre = data.get("nombre", estudiante["nombre"])
    correo = data.get("correo", estudiante["correo"])
    carrera = data.get("carrera", estudiante["carrera"])
    semestre = data.get("semestre", estudiante["semestre"])

    conexion.execute("""
        UPDATE estudiantes
        SET nombre = ?, correo = ?, carrera = ?, semestre = ?
        WHERE id = ?
    """, (nombre, correo, carrera, semestre, id))

    conexion.commit()
    conexion.close()

    return jsonify({
        "mensaje": "Estudiante actualizado correctamente",
        "estudiante": {
            "id": id,
            "nombre": nombre,
            "correo": correo,
            "carrera": carrera,
            "semestre": semestre
        }
    }), 200


@app.route("/api/estudiantes/<int:id>", methods=["DELETE"])
def eliminar_estudiante(id):
    conexion = obtener_conexion()
    estudiante = conexion.execute(
        "SELECT * FROM estudiantes WHERE id = ?",
        (id,)
    ).fetchone()

    if estudiante is None:
        conexion.close()
        return jsonify({"mensaje": "Estudiante no encontrado"}), 404

    conexion.execute("DELETE FROM estudiantes WHERE id = ?", (id,))
    conexion.commit()
    conexion.close()

    return jsonify({"mensaje": "Estudiante eliminado correctamente"}), 200


if __name__ == "__main__":
    inicializar_bd()
    app.run(debug=True, port=5000)