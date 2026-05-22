package ejercicios_propuestos.ejercicio02.converter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ConverterClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ConverterGrpc.ConverterBlockingStub stub = ConverterGrpc.newBlockingStub(channel);

        realizarConversion(stub, 25, "C_F");
        realizarConversion(stub, 100, "SOLES_DOLARES");
        realizarConversion(stub, 10, "KM_MILLAS");

        // Prueba de validación
        realizarConversion(stub, -50, "SOLES_DOLARES");

        // Prueba de tipo inválido
        realizarConversion(stub, 20, "METROS_CM");

        channel.shutdown();
    }

    private static void realizarConversion(ConverterGrpc.ConverterBlockingStub stub, double value, String type) {
        ConvertRequest request = ConvertRequest.newBuilder()
                .setValue(value)
                .setType(type)
                .build();

        ConvertResponse response = stub.convert(request);

        System.out.println("--------------------------------");
        System.out.println("Conversión solicitada: " + type);
        System.out.println("Valor enviado: " + value);
        System.out.println("Resultado: " + response.getResult());
        System.out.println("Mensaje: " + response.getMessage());
    }
}