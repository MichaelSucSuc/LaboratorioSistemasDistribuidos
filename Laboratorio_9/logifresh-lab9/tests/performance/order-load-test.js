import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 100,
  duration: "5m",
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["avg<8000"]
  }
};

const BASE_URL = "http://localhost:3000";

export default function () {
  const products = ["P001", "P002", "P003"];
  const productId = products[Math.floor(Math.random() * products.length)];

  const payload = JSON.stringify({
    productId,
    quantity: 1,
    customerEmail: `cliente${Math.floor(Math.random() * 100000)}@demo.com`,
    promoCode: "PROMO10"
  });

  const params = {
    headers: {
      "Content-Type": "application/json"
    }
  };

  const response = http.post(`${BASE_URL}/orders`, payload, params);

  check(response, {
    "pedido creado o error controlado": (r) =>
      r.status === 201 || r.status === 409 || r.status === 500,
    "respuesta menor a 8s": (r) => r.timings.duration < 8000
  });

  sleep(1);
}
