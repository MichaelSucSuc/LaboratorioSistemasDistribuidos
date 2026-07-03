import http.server
import ssl

HOST = "localhost"
PORT = 4443

handler = http.server.SimpleHTTPRequestHandler
server = http.server.HTTPServer((HOST, PORT), handler)

context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
context.load_cert_chain(certfile="cert.pem", keyfile="key.pem")

server.socket = context.wrap_socket(server.socket, server_side=True)

print(f"Servidor HTTPS ejecutándose en https://{HOST}:{PORT}")
server.serve_forever()