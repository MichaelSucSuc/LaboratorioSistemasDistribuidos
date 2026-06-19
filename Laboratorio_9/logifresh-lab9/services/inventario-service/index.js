const express = require("express");
const morgan = require("morgan");

const app = express();
app.use(express.json());
app.use(morgan("dev"));

const PORT = process.env.PORT || 3001;

const inventory = {
  P001: { productName: "Yogurt refrigerado", stock: 10000 },
  P002: { productName: "Queso fresco", stock: 10000 },
  P003: { productName: "Leche pasteurizada", stock: 10000 }
};

const reservations = new Map();

app.get("/health", (req, res) => {
  res.json({
    service: "inventario-service",
    status: "UP",
    timestamp: new Date().toISOString()
  });
});

app.get("/inventory", (req, res) => {
  res.json(inventory);
});

app.post("/inventory/reserve", (req, res) => {
  const { orderId, productId, quantity } = req.body;

  if (!orderId || !productId || !quantity || quantity <= 0) {
    return res.status(400).json({
      error: "Datos inválidos para reserva de inventario"
    });
  }

  const item = inventory[productId];

  if (!item) {
    return res.status(404).json({
      error: "Producto no encontrado en inventario"
    });
  }

  if (item.stock < quantity) {
    return res.status(409).json({
      error: "Inventario insuficiente",
      available: item.stock,
      requested: quantity
    });
  }

  // Idempotencia básica: si el pedido ya reservó, no duplicamos la resta.
  if (reservations.has(orderId)) {
    return res.json({
      reservationId: reservations.get(orderId).reservationId,
      status: "ALREADY_RESERVED",
      productId,
      quantity,
      remainingStock: item.stock
    });
  }

  item.stock -= quantity;

  const reservation = {
    reservationId: `RES-${Date.now()}-${Math.floor(Math.random() * 10000)}`,
    orderId,
    productId,
    quantity,
    status: "RESERVED",
    createdAt: new Date().toISOString()
  };

  reservations.set(orderId, reservation);

  res.json({
    ...reservation,
    remainingStock: item.stock
  });
});

app.post("/inventory/release", (req, res) => {
  const { orderId, productId, quantity } = req.body;

  if (!orderId || !productId || !quantity || quantity <= 0) {
    return res.status(400).json({
      error: "Datos inválidos para liberar inventario"
    });
  }

  const item = inventory[productId];

  if (!item) {
    return res.status(404).json({
      error: "Producto no encontrado en inventario"
    });
  }

  // Idempotencia: solo liberamos si existía una reserva de ese pedido.
  if (!reservations.has(orderId)) {
    return res.json({
      status: "NO_RESERVATION_FOUND",
      productId,
      quantity,
      currentStock: item.stock
    });
  }

  item.stock += quantity;
  reservations.delete(orderId);

  res.json({
    status: "RELEASED",
    productId,
    quantity,
    currentStock: item.stock
  });
});

app.listen(PORT, () => {
  console.log(`inventario-service corriendo en puerto ${PORT}`);
});
