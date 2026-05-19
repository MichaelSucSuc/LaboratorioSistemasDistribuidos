// Ejemplo de servidor gRPC (Java) para la calculadora distribuida
// Código listo para copiar/pegar. Requiere clases generadas por protoc (CalculatorGrpc, Request, Response).
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class CalculatorServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorService())
                .build()
                .start();
        System.out.println("Server started on port 50051");
        server.awaitTermination();
    }

    static class CalculatorService extends CalculatorGrpc.CalculatorImplBase {
        @Override
        public void sum(Request req, StreamObserver<Response> responseObserver) {
            int result = req.getA() + req.getB();
            Response response = Response.newBuilder()
                    .setResult(result)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
