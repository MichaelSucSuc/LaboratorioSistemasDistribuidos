Ejercicio resuelto por el docente: Calculadora distribuida usando gRPC (Java)

Archivos:
- calculator.proto
- CalculatorServer.java
- CalculatorClient.java

Pasos rápidos:
1. Generar las clases Java con protoc + plugin gRPC:

protoc --java_out=./java --grpc-java_out=./java calculator.proto

2. Compilar y ejecutar el servidor (desde el directorio con las clases generadas):

javac -cp .;path\to\grpc-all.jar CalculatorServer.java
java -cp .;path\to\grpc-all.jar CalculatorServer

3. Ejecutar el cliente en otra terminal:

javac -cp .;path\to\grpc-all.jar CalculatorClient.java
java -cp .;path\to\grpc-all.jar CalculatorClient

Resultado esperado:
Resultado: 12
