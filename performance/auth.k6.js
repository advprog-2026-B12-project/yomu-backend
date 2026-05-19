import http from 'k6/http';
import { check, sleep } from 'k6';

/*
Local commands:
  k6 run performance/auth.k6.js
  BASE_URL=http://localhost:8080 k6 run performance/auth.k6.js

This is a lightweight smoke test, not a final benchmark. Run serious
before/after profiling on local or staging with stable infrastructure.
*/

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PASSWORD = __ENV.E2E_TEST_PASSWORD || 'Password123!';

http.setResponseCallback(
  http.expectedStatuses({ min: 200, max: 399 }, 400, 401),
);

export const options = {
  vus: 2,
  iterations: 4,
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'],
    checks: ['rate>0.95'],
  },
};

function uniqueUser() {
  const suffix = `${Date.now()}-${__VU}-${__ITER}-${Math.floor(Math.random() * 1e6)}`;
  const username = `k6${suffix}`.replace(/[^a-zA-Z0-9]/g, '').slice(0, 32);

  return {
    displayName: `k6 Auth ${suffix}`,
    username,
    email: `${username}@example.com`,
    password: TEST_PASSWORD,
  };
}

function parseJson(response) {
  try {
    return response.json();
  } catch {
    return null;
  }
}

export default function () {
  const user = uniqueUser();
  const headers = { 'Content-Type': 'application/json' };

  const registerResponse = http.post(
    `${BASE_URL}/api/auth/register`,
    JSON.stringify(user),
    { headers, tags: { name: 'POST /api/auth/register' } },
  );

  check(registerResponse, {
    'register status is 200 or 201': (response) =>
      response.status === 200 || response.status === 201,
  });

  const loginResponse = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ username: user.email, password: user.password }),
    { headers, tags: { name: 'POST /api/auth/login valid' } },
  );
  const loginBody = parseJson(loginResponse);

  check(loginResponse, {
    'login status is 200': (response) => response.status === 200,
    'login response contains token': () => Boolean(loginBody && loginBody.token),
  });

  const invalidLoginResponse = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ username: user.email, password: `${user.password}-wrong` }),
    { headers, tags: { name: 'POST /api/auth/login invalid' } },
  );

  check(invalidLoginResponse, {
    'invalid password status is 400 or 401': (response) =>
      response.status === 400 || response.status === 401,
  });

  sleep(1);
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data),
    'k6-summary.json': JSON.stringify(data, null, 2),
  };
}

function textSummary(data) {
  const metrics = data.metrics;
  const duration = metrics.http_req_duration?.values;
  const requestRate = metrics.http_reqs?.values?.rate;
  const failedRate = metrics.http_req_failed?.values?.rate;
  const checksRate = metrics.checks?.values?.rate;

  return [
    'Auth k6 smoke summary',
    `avg response time: ${duration?.avg?.toFixed(2) ?? 'n/a'} ms`,
    `p95 response time: ${duration?.['p(95)']?.toFixed(2) ?? 'n/a'} ms`,
    `request rate: ${requestRate?.toFixed(2) ?? 'n/a'} req/s`,
    `error rate: ${failedRate !== undefined ? (failedRate * 100).toFixed(2) : 'n/a'}%`,
    `failed checks: ${checksRate !== undefined ? ((1 - checksRate) * 100).toFixed(2) : 'n/a'}%`,
    '',
  ].join('\n');
}
