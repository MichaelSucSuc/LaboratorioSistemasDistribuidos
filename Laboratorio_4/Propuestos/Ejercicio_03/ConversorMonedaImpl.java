package Laboratorio_4.Propuestos.Ejercicio_03;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConversorMonedaImpl extends UnicastRemoteObject implements ConversorMonedaInterface {

    private final double TASA_DOLAR = 3.75;
    private final double TASA_EURO = 4.05;

    public ConversorMonedaImpl() throws RemoteException {
        super();
    }

    @Override
    public double convertirADolares(double montoSoles) throws RemoteException {
        return montoSoles / TASA_DOLAR;
    }

    @Override
    public double convertirAEuros(double montoSoles) throws RemoteException {
        return montoSoles / TASA_EURO;
    }
}