package Laboratorio_4.Propuestos.Ejercicio_03;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConversorMonedaInterface extends Remote {

    double convertirADolares(double montoSoles) throws RemoteException;

    double convertirAEuros(double montoSoles) throws RemoteException;
}