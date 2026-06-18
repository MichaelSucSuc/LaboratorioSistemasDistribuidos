# Evidencias sugeridas

Capturar pantalla de:

1. `docker compose up --build` mostrando los 5 servicios levantados.
2. `GET http://localhost:3000/health`
3. `GET http://localhost:3001/inventory`
4. `POST http://localhost:3000/orders` con promoción aplicada.
5. `POST http://localhost:3000/orders` con inventario insuficiente.
6. `POST http://localhost:3000/orders/:id/cancel`
7. Resultado de `k6 run tests/performance/order-load-test.js`
8. Métricas de k6:
   - `http_req_duration avg`
   - `http_req_duration max`
   - `http_req_failed`
   - `http_reqs`
   - iteraciones o throughput aproximado
