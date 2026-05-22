package com.calculadora;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.calculadora.CalculatorProto.Request;
import com.calculadora.CalculatorProto.Response;

public class CalculatorServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorServiceImpl())
                .build()
                .start();
        
        System.out.println("Servidor gRPC iniciado en puerto 50051");
        System.out.println("Esperando peticiones...");
        
        server.awaitTermination();
    }
    
    static class CalculatorServiceImpl extends CalculatorGrpc.CalculatorImplBase {
        @Override
        public void sum(Request req, StreamObserver<Response> responseObserver) {
            int result = req.getA() + req.getB();
            System.out.println("Sumando: " + req.getA() + " + " + req.getB() + " = " + result);
            
            Response response = Response.newBuilder()
                    .setResult(result)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}