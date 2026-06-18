const express = require("express");
const morgan = require("morgan");

const app = express();
app.use(express.json());
app.use(morgan("dev"));

const PORT = process.env.PORT || 3002;

const invoicesByOrder = new Map();

app.get("/health", (req, res) => {
  res.json({
    service: "facturacion-service",
    status: "UP",
    timestamp: new Date().toISOString()
  });
});

app.get("/invoices", (req, res) => {
  res.json(Array.from(invoicesByOrder.values()));
});

app.post("/invoices", (req, res) => {
  const { orderId, customerEmail, subtotal, discount, total } = req.body;

  if (!orderId || !customerEmail || total === undefined) {
    return res.status(400).json({
      error: "Datos inválidos para generar factura"
    });
  }

  // Idempotencia: evita facturas duplicadas para el mismo pedido.
  if (invoicesByOrder.has(orderId)) {
    return res.json({
      ...invoicesByOrder.get(orderId),
      status: "ALREADY_CREATED"
    });
  }

  const invoice = {
    invoiceId: `FAC-${Date.now()}-${Math.floor(Math.random() * 10000)}`,
    orderId,
    customerEmail,
    subtotal,
    discount,
    total,
    status: "CREATED",
    createdAt: new Date().toISOString()
  };

  invoicesByOrder.set(orderId, invoice);

  res.status(201).json(invoice);
});

app.listen(PORT, () => {
  console.log(`facturacion-service corriendo en puerto ${PORT}`);
});
