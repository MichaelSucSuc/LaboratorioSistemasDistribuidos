const express = require("express");
const morgan = require("morgan");

const app = express();
app.use(express.json());
app.use(morgan("dev"));

const PORT = process.env.PORT || 3004;

const notifications = [];

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

app.get("/health", (req, res) => {
  res.json({
    service: "notificaciones-service",
    status: "UP",
    timestamp: new Date().toISOString()
  });
});

app.get("/notifications", (req, res) => {
  res.json(notifications);
});

app.post("/notifications", async (req, res) => {
  const { orderId, customerEmail, message } = req.body;

  if (!orderId || !customerEmail || !message) {
    return res.status(400).json({
      error: "Datos inválidos para notificación"
    });
  }

  // Simula un pequeño retraso realista del envío de correo.
  await sleep(200);

  const notification = {
    notificationId: `NOT-${Date.now()}-${Math.floor(Math.random() * 10000)}`,
    orderId,
    customerEmail,
    message,
    status: "SENT",
    createdAt: new Date().toISOString()
  };

  notifications.push(notification);

  res.status(201).json(notification);
});

app.listen(PORT, () => {
  console.log(`notificaciones-service corriendo en puerto ${PORT}`);
});
