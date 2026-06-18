const orderForm = document.getElementById("orderForm");
const orderResult = document.getElementById("orderResult");
const dashboardOutput = document.getElementById("dashboardOutput");
const btnHealth = document.getElementById("btnHealth");
const servicesGrid = document.getElementById("servicesGrid");

const endpoints = {
  inventory: "/api/inventario/inventory",
  orders: "/api/orders",
  invoices: "/api/facturacion/invoices",
  shipments: "/api/transporte/shipments",
  notifications: "/api/notificaciones/notifications"
};

const healthServices = [
  { name: "Pedidos", url: "/api/pedidos/health" },
  { name: "Inventario", url: "/api/inventario/health" },
  { name: "Facturación", url: "/api/facturacion/health" },
  { name: "Transporte", url: "/api/transporte/health" },
  { name: "Notificaciones", url: "/api/notificaciones/health" }
];

function formatJson(data) {
  return JSON.stringify(data, null, 2);
}

async function requestJson(url, options = {}) {
  const response = await fetch(url, options);
  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw {
      status: response.status,
      data
    };
  }

  return data;
}

orderForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const payload = {
    productId: document.getElementById("productId").value,
    quantity: Number(document.getElementById("quantity").value),
    customerEmail: document.getElementById("customerEmail").value,
    promoCode: document.getElementById("promoCode").value || undefined
  };

  orderResult.className = "result";
  orderResult.textContent = "Registrando pedido...";

  try {
    const data = await requestJson("/api/orders", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    orderResult.textContent = formatJson(data);
    loadData("orders");
  } catch (error) {
    orderResult.textContent = formatJson({
      error: "No se pudo registrar el pedido",
      status: error.status,
      detail: error.data
    });
  }
});

async function loadData(type) {
  dashboardOutput.textContent = "Cargando datos...";

  try {
    const data = await requestJson(endpoints[type]);
    dashboardOutput.textContent = formatJson(data);
  } catch (error) {
    dashboardOutput.textContent = formatJson({
      error: "No se pudo cargar la información",
      status: error.status,
      detail: error.data
    });
  }
}

document.querySelectorAll("[data-load]").forEach(button => {
  button.addEventListener("click", () => {
    loadData(button.dataset.load);
  });
});

btnHealth.addEventListener("click", checkHealth);

async function checkHealth() {
  servicesGrid.innerHTML = "";

  for (const service of healthServices) {
    const card = document.createElement("div");
    card.className = "service pending";
    card.innerHTML = `${service.name}<span>verificando...</span>`;
    servicesGrid.appendChild(card);

    try {
      const data = await requestJson(service.url);
      card.className = "service ok";
      card.innerHTML = `${service.name}<span>${data.status}</span>`;
    } catch (error) {
      card.className = "service error";
      card.innerHTML = `${service.name}<span>ERROR</span>`;
    }
  }
}

checkHealth();
