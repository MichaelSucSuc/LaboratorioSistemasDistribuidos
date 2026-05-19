// Ejemplo base de servidor gRPC (Java) para conversiones
// Requiere clases generadas por protoc: ConverterGrpc, ConvertRequest, ConvertResponse
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ConverterServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
                .addService(new ConverterService())
                .build()
                .start();
        System.out.println("Converter server started on 50052");
        server.awaitTermination();
    }

    static class ConverterService extends ConverterGrpc.ConverterImplBase {
        @Override
        public void convert(ConvertRequest req, StreamObserver<ConvertResponse> responseObserver) {
            double result = req.getValue() * 1.8 + 32; // ejemplo: Celsius->F
            ConvertResponse resp = ConvertResponse.newBuilder()
                    .setResult(result)
                    .setMessage("OK")
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }
}
