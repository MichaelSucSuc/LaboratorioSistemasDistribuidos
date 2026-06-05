#!/usr/bin/env python3
"""
Cliente SOAP usando zeep - invoca el servicio público de ejemplo
http://www.dneonline.com/calculator.asmx?WSDL

Uso:
    python cliente_soap.py 5 8
"""
import sys
from zeep import Client

WSDL = 'http://www.dneonline.com/calculator.asmx?WSDL'

def main(a, b):
    client = Client(WSDL)
    resultado = client.service.Add(int(a), int(b))
    print(resultado)

if __name__ == '__main__':
    if len(sys.argv) >= 3:
        main(sys.argv[1], sys.argv[2])
    else:
        print('Uso: python cliente_soap.py <a> <b>')