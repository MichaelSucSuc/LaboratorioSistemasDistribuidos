# Casos de prueba base

| ID | Objetivo | Datos de entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|
| CP-01 | Registrar pedido correcto sin promoción | P001, cantidad 1, correo válido | Pedido creado con estado CONFIRMED | Pendiente |
| CP-02 | Registrar pedido con PROMO10 | P001, cantidad 2, PROMO10 | Pedido creado con descuento de 10% | Pendiente |
| CP-03 | Registrar pedido con PROMO20 | P002, cantidad 1, PROMO20 | Pedido creado con descuento de 20% | Pendiente |
| CP-04 | Registrar pedido con inventario insuficiente | P002, cantidad 9999 | Error 409 inventario insuficiente | Pendiente |
| CP-05 | Registrar pedido con producto inexistente | PX99, cantidad 1 | Error 404 producto no existe | Pendiente |
| CP-06 | Registrar pedido con cantidad 0 | P001, cantidad 0 | Error 400 datos inválidos | Pendiente |
| CP-07 | Cancelar pedido existente | ID de pedido creado | Pedido con estado CANCELLED y stock liberado | Pendiente |
| CP-08 | Cancelar pedido inexistente | ID falso | Error 404 pedido no encontrado | Pendiente |
| CP-09 | Verificar generación de factura | Pedido correcto | Factura creada una sola vez | Pendiente |
| CP-10 | Verificar envío de notificación | Pedido correcto | Notificación registrada con estado SENT | Pendiente |
