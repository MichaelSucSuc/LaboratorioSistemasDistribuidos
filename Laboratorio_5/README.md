Ejercicios resueltos: Java RMI y gRPC (Python)

Ejercicio 1 — Servicio RPC Tradicional (Java RMI)

Archivos relevantes:
- [src/rmi/Calculator.java](src/rmi/Calculator.java)
- [src/rmi/CalculatorImpl.java](src/rmi/CalculatorImpl.java)
- [src/rmi/Server.java](src/rmi/Server.java)
- [src/rmi/Client.java](src/rmi/Client.java)

Compilar y ejecutar (desde la raíz del repo):

Windows / PowerShell / cmd:

javac -d out src/rmi/*.java
cd out
java rmi.Server

En otra terminal:
java rmi.Client

Nota: el servidor crea un registro RMI en el mismo proceso (puerto 1099).

Ejercicio 2 — Sistema de Conversión con gRPC (Python)

Archivos relevantes:
- [proto/converter.proto](proto/converter.proto)
- [grpc_server/converter_server.py](grpc_server/converter_server.py)
- [grpc_server/converter_client.py](grpc_server/converter_client.py)
- [grpc_server/requirements.txt](grpc_server/requirements.txt)

Pasos para poner en marcha:

1. Crear un entorno virtual e instalar dependencias:

python -m venv venv
venv\Scripts\activate
pip install -r grpc_server/requirements.txt

2. Generar los stubs Python con protoc (desde la raíz del repo):

python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. proto/converter.proto

Esto generará `converter_pb2.py` y `converter_pb2_grpc.py` en la raíz.

3. Ejecutar el servidor:

python grpc_server/converter_server.py

4. En otra terminal, ejecutar el cliente:

python grpc_server/converter_client.py

Notas:
- El servidor valida entradas y registra logs por cada petición.
- Ajusta la tasa de cambio `exchange_rate` en `grpc_server/converter_server.py` según convenga.
