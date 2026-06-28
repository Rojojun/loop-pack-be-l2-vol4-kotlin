import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SCENARIO = __ENV.SCENARIO || 'ramping';

// 시드해둔 orderId 범위 (START ~ START+COUNT-1). 멱등 때문에 요청마다 다른 orderId 사용.
const ORDER_START = Number(__ENV.ORDER_START || 1);
const ORDER_COUNT = Number(__ENV.ORDER_COUNT || 100000);

export const options = {
    scenarios: SCENARIO === 'constant'
        ? {
            constant_high: {
                executor: 'constant-arrival-rate',
                rate: Number(__ENV.RATE || 100),   // 초당 요청 수 (포화점 근처로 조정)
                timeUnit: '1s',
                duration: __ENV.DURATION || '3m',
                preAllocatedVUs: 100,
                maxVUs: 1000,
            },
        }
        : {
            ramping: {
                executor: 'ramping-vus',
                startVUs: 0,
                stages: [
                    { duration: '1m', target: 50 },
                    { duration: '1m', target: 150 },
                    { duration: '1m', target: 300 },
                    { duration: '30s', target: 0 },
                ],
            },
        },
    thresholds: {
        http_req_duration: ['p(95)<2000', 'p(99)<5000'],
        http_req_failed: ['rate<0.05'],
    },
};

export default function () {
    // 매 iteration 마다 다른 orderId (멱등 우회). 풀 소진 시 wrap-around.
    const orderId = ORDER_START + (__ITER + __VU * 100000) % ORDER_COUNT;
    const payload = JSON.stringify({
        orderId: orderId,
        cardType: 'SAMSUNG',
        cardNo: '1234-1234-1234-1234',
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Loopers-LoginId': __ENV.LOGIN_ID || 'loadtest',
            'X-Loopers-LoginPw': __ENV.LOGIN_PW || 'pw',
        },
    };
    const res = http.post(`${BASE_URL}/api/v1/payments`, payload, params);
    check(res, { 'status is 2xx': (r) => r.status >= 200 && r.status < 300 });
}
