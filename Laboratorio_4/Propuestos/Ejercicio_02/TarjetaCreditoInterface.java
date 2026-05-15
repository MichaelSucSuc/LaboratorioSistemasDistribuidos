package Laboratorio_4.Propuestos.Ejercicio_02;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TarjetaCreditoInterface extends Remote {

    double consultarSaldo() throws RemoteException;

    double consultarLimite() throws RemoteException;

    boolean realizarCompra(double monto) throws RemoteException;

    boolean realizarPago(double monto) throws RemoteException;

    String obtenerEstado() throws RemoteException;
}