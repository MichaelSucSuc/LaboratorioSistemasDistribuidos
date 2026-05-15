package Laboratorio_4.Propuestos.Ejercicio_02;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TarjetaCreditoImpl extends UnicastRemoteObject implements TarjetaCreditoInterface {

    private double limiteCredito;
    private double saldoUsado;

    public TarjetaCreditoImpl(double limiteCredito) throws RemoteException {
        super();
        this.limiteCredito = limiteCredito;
        this.saldoUsado = 0.0;
    }

    @Override
    public double consultarSaldo() throws RemoteException {
        return saldoUsado;
    }

    @Override
    public double consultarLimite() throws RemoteException {
        return limiteCredito;
    }

    @Override
    public boolean realizarCompra(double monto) throws RemoteException {
        if (monto <= 0) {
            return false;
        }

        if (saldoUsado + monto <= limiteCredito) {
            saldoUsado += monto;
            return true;
        }

        return false;
    }

    @Override
    public boolean realizarPago(double monto) throws RemoteException {
        if (monto <= 0) {
            return false;
        }

        if (monto > saldoUsado) {
            saldoUsado = 0;
        } else {
            saldoUsado -= monto;
        }

        return true;
    }

    @Override
    public String obtenerEstado() throws RemoteException {
        double disponible = limiteCredito - saldoUsado;

        return "Estado de la tarjeta:\n"
                + "Límite de crédito: S/ " + limiteCredito + "\n"
                + "Saldo usado: S/ " + saldoUsado + "\n"
                + "Crédito disponible: S/ " + disponible;
    }
}