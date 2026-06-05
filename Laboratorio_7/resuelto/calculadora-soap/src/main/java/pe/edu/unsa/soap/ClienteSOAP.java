package pe.edu.unsa.soap;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;

@WebService(name = "CalculadoraSOAP")
interface ICalculadora {
    @WebMethod
    int sumar(int a, int b);
}

public class ClienteSOAP {
    public static void main(String[] args) throws Exception {

        URL url = new URL("http://localhost:9090/calculadora?wsdl");

        QName qname = new QName(
            "http://soap.unsa.edu.pe/",
            "CalculadoraSOAPService"
        );

        Service service   = Service.create(url, qname);
        ICalculadora calc = service.getPort(ICalculadora.class);

        System.out.println("Resultado: 10 + 20 = " + calc.sumar(10, 20));
    }
}