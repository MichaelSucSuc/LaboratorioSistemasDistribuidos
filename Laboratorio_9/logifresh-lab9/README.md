# LogiFresh Lab 9 - Microservicios con Docker

Proyecto base para la práctica de Sistemas Distribuidos:
**Software Quality y Software Testing aplicados a entornos distribuidos**.

## Servicios

- `pedidos-service`: registra y cancela pedidos.
- `inventario-service`: valida y reserva stock.
- `facturacion-service`: genera facturas.
- `transporte-service`: asigna despacho.
- `notificaciones-service`: simula envío de correo.

## Requisitos

- Docker Desktop
- Docker Compose
- k6 o JMeter para pruebas de rendimiento
- Postman o Insomnia para pruebas manuales

## Levantar el proyecto

```bash
docker compose up --build
```

## Probar salud de servicios

```bash
curl http://localhost:3000/health
curl http://localhost:3001/health
curl http://localhost:3002/health
curl http://localhost:3003/health
curl http://localhost:3004/health
```

## Registrar pedido

```bash
curl -X POST http://localhost:3000/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":\"P001\",\"quantity\":2,\"customerEmail\":\"cliente@demo.com\",\"promoCode\":\"PROMO10\"}"
```

En PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:3000/orders `
  -ContentType "application/json" `
  -Body '{"productId":"P001","quantity":2,"customerEmail":"cliente@demo.com","promoCode":"PROMO10"}'
```

## Cancelar pedido

```bash
curl -X POST http://localhost:3000/orders/ORDEN_ID/cancel
```

## Ver pedidos

```bash
curl http://localhost:3000/orders
```

## Prueba de rendimiento con k6

Desde la carpeta raíz:

```bash
k6 run tests/performance/order-load-test.js
```

El script ya está configurado para:
- 100 usuarios concurrentes.
- 5 minutos de ejecución.
- Métricas: tiempo promedio, tiempo máximo, errores y throughput.
## Frontend

Ahora el proyecto incluye una interfaz web simple servida con Nginx.

URL:

```bash
http://localhost:8080
```

Desde el frontend puedes:

- Verificar estado de los 5 servicios.
- Registrar pedidos.
- Aplicar promociones.
- Consultar inventario.
- Consultar pedidos.
- Consultar facturas.
- Consultar transporte.
- Consultar notificaciones.

El frontend usa rutas `/api/...` y Nginx redirige las peticiones a los microservicios dentro de Docker.
