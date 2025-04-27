import http from "k6/http";
import { check, sleep } from "k6";
import { Rate } from "k6/metrics";

const errorRate = new Rate("errors");

export const options = {
  stages: [
    { duration: "30s", target: 20 }, // Ramp-up
    { duration: "1m", target: 20 }, // Stay at peak
    { duration: "30s", target: 0 }, // Ramp-down
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"], // 95% of requests should be below 500ms
    errors: ["rate<0.1"], // Error rate should be below 10%
  },
};

const BASE_URL = "http://localhost:5001";

export default function () {
  const productIds = ["1", "2", "3", "4", "5"];
  const randomProductId =
    productIds[Math.floor(Math.random() * productIds.length)];

  const response = http.get(`${BASE_URL}/product/${randomProductId}/similar`);

  check(response, {
    "is status 200": (r) => r.status === 200,
    "has correct content type": (r) =>
      r.headers["Content-Type"] === "application/json",
    "has valid response structure": (r) => {
      try {
        const data = JSON.parse(r.body);
        return (
          Array.isArray(data) &&
          data.every(
            (product) =>
              product.id &&
              product.name &&
              typeof product.price === "number" &&
              typeof product.availability === "boolean"
          )
        );
      } catch (e) {
        return false;
      }
    },
  });

  errorRate.add(response.status !== 200);

  sleep(1);
}
