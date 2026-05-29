from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

estudiantes = [
    {
        "id": 1,
        "nombre": "Jeremy Perez",
        "correo": "jperezhua@unsa.edu.pe",
        "carrera": "Ingeniería de Sistemas",
        "semestre": 8
    }
]

siguiente_id = 2


def buscar_estudiante(id):
    for estudiante in estudiantes:
        if estudiante["id"] == id:
            return estudiante
    return None


@app.route("/", methods=["GET"])
def inicio():
    return jsonify({
        "mensaje": "API RESTful de Estudiantes",
        "endpoints": {
            "listar": "GET /estudiantes",
            "buscar": "GET /estudiantes/<id>",
            "registrar": "POST /estudiantes",
            "actualizar": "PUT /estudiantes/<id>",
            "eliminar": "DELETE /estudiantes/<id>"
        }
    })


@app.route("/estudiantes", methods=["GET"])
def listar_estudiantes():
    return jsonify(estudiantes), 200


@app.route("/estudiantes/<int:id>", methods=["GET"])
def obtener_estudiante(id):
    estudiante = buscar_estudiante(id)

    if estudiante is None:
        return jsonify({"mensaje": "Estudiante no encontrado"}), 404

    return jsonify(estudiante), 200


@app.route("/estudiantes", methods=["POST"])
def registrar_estudiante():
    global siguiente_id

    data = request.get_json()

    if not data:
        return jsonify({"mensaje": "No se enviaron datos"}), 400

    if "nombre" not in data or "correo" not in data:
        return jsonify({"mensaje": "El nombre y el correo son obligatorios"}), 400

    nuevo_estudiante = {
        "id": siguiente_id,
        "nombre": data["nombre"],
        "correo": data["correo"],
        "carrera": data.get("carrera", "No especificada"),
        "semestre": data.get("semestre", 0)
    }

    estudiantes.append(nuevo_estudiante)
    siguiente_id += 1

    return jsonify({
        "mensaje": "Estudiante registrado correctamente",
        "estudiante": nuevo_estudiante
    }), 201


@app.route("/estudiantes/<int:id>", methods=["PUT"])
def actualizar_estudiante(id):
    estudiante = buscar_estudiante(id)

    if estudiante is None:
        return jsonify({"mensaje": "Estudiante no encontrado"}), 404

    data = request.get_json()

    if not data:
        return jsonify({"mensaje": "No se enviaron datos"}), 400

    estudiante["nombre"] = data.get("nombre", estudiante["nombre"])
    estudiante["correo"] = data.get("correo", estudiante["correo"])
    estudiante["carrera"] = data.get("carrera", estudiante["carrera"])
    estudiante["semestre"] = data.get("semestre", estudiante["semestre"])

    return jsonify({
        "mensaje": "Estudiante actualizado correctamente",
        "estudiante": estudiante
    }), 200


@app.route("/estudiantes/<int:id>", methods=["DELETE"])
def eliminar_estudiante(id):
    estudiante = buscar_estudiante(id)

    if estudiante is None:
        return jsonify({"mensaje": "Estudiante no encontrado"}), 404

    estudiantes.remove(estudiante)

    return jsonify({
        "mensaje": "Estudiante eliminado correctamente"
    }), 200


if __name__ == "__main__":
    app.run(debug=True, port=5000)