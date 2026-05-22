
package ejercicios_propuestos.ejercicio02.converter;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ConverterServer {

    private Server server;

    public static void main(String[] args) throws Exception {
        ConverterServer converterServer = new ConverterServer();
        converterServer.start();
        converterServer.blockUntilShutdown();
    }

    private void start() throws Exception {
        int port = 50051;

        server = ServerBuilder
                .forPort(port)
                .addService(new ConverterService())
                .build()
                .start();

        System.out.println("Servidor gRPC iniciado en el puerto " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor...");
            ConverterServer.this.stop();
            System.out.println("Servidor detenido.");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class ConverterService extends ConverterGrpc.ConverterImplBase {

        @Override
        public void convert(ConvertRequest request, StreamObserver<ConvertResponse> responseObserver) {
            double value = request.getValue();
            String type = request.getType().toUpperCase();

            System.out.println("Solicitud recibida:");
            System.out.println("Valor: " + value);
            System.out.println("Tipo de conversión: " + type);

            ConvertResponse response;

            try {
                if (value < 0 && (type.equals("KM_MILLAS") || type.equals("SOLES_DOLARES"))) {
                    throw new IllegalArgumentException("El valor no puede ser negativo para esta conversión.");
                }

                double result;
                String message;

                switch (type) {
                    case "C_F":
                        result = (value * 1.8) + 32;
                        message = value + " °C equivalen a " + result + " °F";
                        break;

                    case "SOLES_DOLARES":
                        double tipoCambio = 3.75;
                        result = value / tipoCambio;
                        message = value + " soles equivalen aproximadamente a " + result + " dólares";
                        break;

                    case "KM_MILLAS":
                        result = value * 0.621371;
                        message = value + " kilómetros equivalen a " + result + " millas";
                        break;

                    default:
                        throw new IllegalArgumentException(
                                "Tipo de conversión no válido. Use: C_F, SOLES_DOLARES o KM_MILLAS."
                        );
                }

                System.out.println("Resultado generado: " + result);

                response = ConvertResponse.newBuilder()
                        .setResult(result)
                        .setMessage(message)
                        .build();

            } catch (Exception e) {
                System.out.println("Error en la conversión: " + e.getMessage());

                response = ConvertResponse.newBuilder()
                        .setResult(0)
                        .setMessage("Error: " + e.getMessage())
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}