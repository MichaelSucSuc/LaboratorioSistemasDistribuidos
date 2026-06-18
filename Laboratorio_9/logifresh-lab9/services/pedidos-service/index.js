const express = require("express");
const axios = require("axios");
const morgan = require("morgan");

const app = express();
app.use(express.json());
app.use(morgan("dev"));

const PORT = process.env.PORT || 3000;

const INVENTARIO_URL = process.env.INVENTARIO_URL;
const FACTURACION_URL = process.env.FACTURACION_URL;
const TRANSPORTE_URL = process.env.TRANSPORTE_URL;
const NOTIFICACIONES_URL = process.env.NOTIFICACIONES_URL;

const orders = new Map();

const products = {
  P001: { name: "Yogurt refrigerado", price: 8.5 },
  P002: { name: "Queso fresco", price: 15.0 },
  P003: { name: "Leche pasteurizada", price: 5.2 }
};

function createId(prefix) {
  return `${prefix}-${Date.now()}-${Math.floor(Math.random() * 10000)}`;
}

function applyPromotion(subtotal, promoCode) {
  const promotions = {
    PROMO10: 0.10,
    PROMO20: 0.20,
    CAMPANIA: 0.15
  };

  const discountRate = promotions[promoCode] || 0;
  const discount = Number((subtotal * discountRate).toFixed(2));
  const total = Number((subtotal - discount).toFixed(2));

  return { discountRate, discount, total };
}

app.get("/health", (req, res) => {
  res.json({
    service: "pedidos-service",
    status: "UP",
    timestamp: new Date().toISOString()
  });
});

app.get("/orders", (req, res) => {
  res.json(Array.from(orders.values()));
});

app.get("/orders/:id", (req, res) => {
  const order = orders.get(req.params.id);

  if (!order) {
    return res.status(404).json({
      error: "Pedido no encontrado"
    });
  }

  res.json(order);
});

app.post("/orders", async (req, res) => {
  const { productId, quantity, customerEmail, promoCode } = req.body;

  if (!productId || !quantity || quantity <= 0 || !customerEmail) {
    return res.status(400).json({
      error: "Datos inválidos. Se requiere productId, quantity mayor a 0 y customerEmail."
    });
  }

  const product = products[productId];

  if (!product) {
    return res.status(404).json({
      error: "Producto no existe"
    });
  }

  const orderId = createId("ORD");
  const subtotal = Number((product.price * quantity).toFixed(2));
  const promotion = applyPromotion(subtotal, promoCode);

  try {
    // 1. Reserva de inventario
    const inventoryResponse = await axios.post(`${INVENTARIO_URL}/inventory/reserve`, {
      orderId,
      productId,
      quantity
    }, { timeout: 3000 });

    // 2. Creación del pedido
    const order = {
      id: orderId,
      productId,
      productName: product.name,
      quantity,
      customerEmail,
      promoCode: promoCode || null,
      subtotal,
      discount: promotion.discount,
      total: promotion.total,
      status: "CREATED",
      inventoryReservation: inventoryResponse.data,
      createdAt: new Date().toISOString()
    };

    orders.set(orderId, order);

    // 3. Generación de factura
    const invoiceResponse = await axios.post(`${FACTURACION_URL}/invoices`, {
      orderId,
      customerEmail,
      subtotal,
      discount: promotion.discount,
      total: promotion.total
    }, { timeout: 3000 });

    order.invoice = invoiceResponse.data;

    // 4. Asignación de transporte
    const transportResponse = await axios.post(`${TRANSPORTE_URL}/shipments`, {
      orderId,
      customerEmail
    }, { timeout: 3000 });

    order.shipment = transportResponse.data;

    // 5. Notificación asíncrona. No bloquea el registro del pedido.
    axios.post(`${NOTIFICACIONES_URL}/notifications`, {
      orderId,
      customerEmail,
      message: `Pedido ${orderId} registrado correctamente. Total: S/ ${promotion.total}`
    }).catch((error) => {
      console.error("Fallo al enviar notificación:", error.message);
    });

    order.status = "CONFIRMED";
    orders.set(orderId, order);

    return res.status(201).json(order);
  } catch (error) {
    const detail = error.response?.data || error.message;

    // Si el pedido falló después de una reserva parcial, intentamos compensar.
    try {
      await axios.post(`${INVENTARIO_URL}/inventory/release`, {
        orderId,
        productId,
        quantity
      }, { timeout: 1500 });
    } catch (releaseError) {
      console.error("No se pudo liberar inventario:", releaseError.message);
    }

    return res.status(error.response?.status || 500).json({
      error: "No se pudo registrar el pedido",
      detail
    });
  }
});

app.post("/orders/:id/cancel", async (req, res) => {
  const order = orders.get(req.params.id);

  if (!order) {
    return res.status(404).json({
      error: "Pedido no encontrado"
    });
  }

  if (order.status === "CANCELLED") {
    return res.status(409).json({
      error: "El pedido ya está cancelado"
    });
  }

  try {
    await axios.post(`${INVENTARIO_URL}/inventory/release`, {
      orderId: order.id,
      productId: order.productId,
      quantity: order.quantity
    }, { timeout: 3000 });

    order.status = "CANCELLED";
    order.cancelledAt = new Date().toISOString();

    await axios.post(`${NOTIFICACIONES_URL}/notifications`, {
      orderId: order.id,
      customerEmail: order.customerEmail,
      message: `Pedido ${order.id} cancelado correctamente.`
    }).catch(() => {});

    orders.set(order.id, order);

    res.json(order);
  } catch (error) {
    res.status(500).json({
      error: "No se pudo cancelar el pedido",
      detail: error.response?.data || error.message
    });
  }
});

app.listen(PORT, () => {
  console.log(`pedidos-service corriendo en puerto ${PORT}`);
});
