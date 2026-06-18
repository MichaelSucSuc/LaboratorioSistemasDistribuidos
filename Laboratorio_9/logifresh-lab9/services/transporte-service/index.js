const express = require("express");
const morgan = require("morgan");

const app = express();
app.use(express.json());
app.use(morgan("dev"));

const PORT = process.env.PORT || 3003;

const shipmentsByOrder = new Map();

const vehicles = ["TRUCK-01", "TRUCK-02", "TRUCK-03"];

app.get("/health", (req, res) => {
  res.json({
    service: "transporte-service",
    status: "UP",
    timestamp: new Date().toISOString()
  });
});

app.get("/shipments", (req, res) => {
  res.json(Array.from(shipmentsByOrder.values()));
});

app.post("/shipments", (req, res) => {
  const { orderId, customerEmail } = req.body;

  if (!orderId || !customerEmail) {
    return res.status(400).json({
      error: "Datos inválidos para asignar transporte"
    });
  }

  if (shipmentsByOrder.has(orderId)) {
    return res.json({
      ...shipmentsByOrder.get(orderId),
      status: "ALREADY_ASSIGNED"
    });
  }

  const vehicle = vehicles[Math.floor(Math.random() * vehicles.length)];

  const shipment = {
    shipmentId: `SHP-${Date.now()}-${Math.floor(Math.random() * 10000)}`,
    orderId,
    customerEmail,
    vehicle,
    status: "ASSIGNED",
    estimatedDeliveryHours: 24,
    createdAt: new Date().toISOString()
  };

  shipmentsByOrder.set(orderId, shipment);

  res.status(201).json(shipment);
});

app.listen(PORT, () => {
  console.log(`transporte-service corriendo en puerto ${PORT}`);
});
