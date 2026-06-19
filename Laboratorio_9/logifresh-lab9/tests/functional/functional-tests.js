const BASE_URL = process.env.BASE_URL || "http://localhost:3000";
const FACTURACION_URL = process.env.FACTURACION_URL || "http://localhost:3002";
const NOTIFICACIONES_URL = process.env.NOTIFICACIONES_URL || "http://localhost:3004";

let passed = 0;
let failed = 0;

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function request(url, options = {}) {
  const response = await fetch(url, options);
  const data = await response.json().catch(() => ({}));
  return { status: response.status, data };
}

async function test(id, name, fn) {
  try {
    await fn();
    passed++;
    console.log(` ${id} - ${name}`);
  } catch (error) {
    failed++;
    console.log(` ${id} - ${name}`);
    console.log(`   ${error.message}`);
  }
}

function expect(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

async function createOrder(payload) {
  return request(`${BASE_URL}/orders`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

async function run() {
  console.log("Ejecutando pruebas funcionales de LogiFresh...\n");

  await test("CP-01", "Registrar pedido correcto sin promoción", async () => {
    const result = await createOrder({
      productId: "P001",
      quantity: 1,
      customerEmail: "cliente01@demo.com"
    });

    expect(result.status === 201, `Se esperaba 201 y llegó ${result.status}`);
    expect(result.data.status === "CONFIRMED", "El pedido no quedó confirmado");
    expect(result.data.invoice, "No se generó factura");
    expect(result.data.shipment, "No se asignó transporte");
  });

  await test("CP-02", "Registrar pedido con PROMO10", async () => {
    const result = await createOrder({
      productId: "P001",
      quantity: 2,
      customerEmail: "cliente02@demo.com",
      promoCode: "PROMO10"
    });

    expect(result.status === 201, `Se esperaba 201 y llegó ${result.status}`);
    expect(result.data.discount > 0, "No se aplicó descuento");
    expect(result.data.promoCode === "PROMO10", "No se registró la promoción PROMO10");
  });

  await test("CP-03", "Registrar pedido con PROMO20", async () => {
    const result = await createOrder({
      productId: "P002",
      quantity: 1,
      customerEmail: "cliente03@demo.com",
      promoCode: "PROMO20"
    });

    expect(result.status === 201, `Se esperaba 201 y llegó ${result.status}`);
    expect(result.data.discount > 0, "No se aplicó descuento");
    expect(result.data.promoCode === "PROMO20", "No se registró la promoción PROMO20");
  });

  await test("CP-04", "Pedido con inventario insuficiente", async () => {
    const result = await createOrder({
      productId: "P002",
      quantity: 9999,
      customerEmail: "cliente04@demo.com",
      promoCode: "PROMO10"
    });

    expect(result.status === 409, `Se esperaba 409 y llegó ${result.status}`);
    expect(JSON.stringify(result.data).includes("Inventario insuficiente"), "No se mostró error de inventario");
  });

  await test("CP-05", "Pedido con producto inexistente", async () => {
    const result = await createOrder({
      productId: "PX99",
      quantity: 1,
      customerEmail: "cliente05@demo.com"
    });

    expect(result.status === 404, `Se esperaba 404 y llegó ${result.status}`);
  });

  await test("CP-06", "Pedido con cantidad cero", async () => {
    const result = await createOrder({
      productId: "P001",
      quantity: 0,
      customerEmail: "cliente06@demo.com"
    });

    expect(result.status === 400, `Se esperaba 400 y llegó ${result.status}`);
  });

  await test("CP-07", "Cancelar pedido existente", async () => {
    const created = await createOrder({
      productId: "P003",
      quantity: 1,
      customerEmail: "cliente07@demo.com"
    });

    expect(created.status === 201, "No se pudo crear el pedido previo para cancelarlo");

    const cancelled = await request(`${BASE_URL}/orders/${created.data.id}/cancel`, {
      method: "POST"
    });

    expect(cancelled.status === 200, `Se esperaba 200 y llegó ${cancelled.status}`);
    expect(cancelled.data.status === "CANCELLED", "El pedido no quedó cancelado");
  });

  await test("CP-08", "Cancelar pedido inexistente", async () => {
    const result = await request(`${BASE_URL}/orders/ORD-INVALIDO/cancel`, {
      method: "POST"
    });

    expect(result.status === 404, `Se esperaba 404 y llegó ${result.status}`);
  });

  await test("CP-09", "Evitar factura duplicada", async () => {
    const orderId = `TEST-${Date.now()}`;

    const first = await request(`${FACTURACION_URL}/invoices`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        orderId,
        customerEmail: "cliente09@demo.com",
        subtotal: 100,
        discount: 10,
        total: 90
      })
    });

    const second = await request(`${FACTURACION_URL}/invoices`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        orderId,
        customerEmail: "cliente09@demo.com",
        subtotal: 100,
        discount: 10,
        total: 90
      })
    });

    expect(first.status === 201, `Primera factura: se esperaba 201 y llegó ${first.status}`);
    expect(second.status === 200, `Segunda factura: se esperaba 200 y llegó ${second.status}`);
    expect(second.data.status === "ALREADY_CREATED", "No se controló la factura duplicada");
    expect(first.data.invoiceId === second.data.invoiceId, "Se generó una factura duplicada");
  });

  await test("CP-10", "Enviar notificación luego de registrar pedido", async () => {
    const created = await createOrder({
      productId: "P003",
      quantity: 1,
      customerEmail: "cliente10@demo.com",
      promoCode: "PROMO10"
    });

    expect(created.status === 201, "No se pudo crear el pedido para validar notificación");

    await sleep(800);

    const notifications = await request(`${NOTIFICACIONES_URL}/notifications`);

    expect(notifications.status === 200, `Se esperaba 200 y llegó ${notifications.status}`);
    const found = notifications.data.some(notification =>
      notification.orderId === created.data.id &&
      notification.status === "SENT"
    );

    expect(found, "No se encontró la notificación del pedido creado");
  });

  console.log("\nResumen:");
  console.log(`Pruebas correctas: ${passed}`);
  console.log(`Pruebas fallidas: ${failed}`);

  if (failed > 0) {
    process.exit(1);
  }
}

run();