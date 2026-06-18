import http from 'k6/http';
import { check } from 'k6';

// 가설(ADR-0002 D5-a): 노필터 + 인기순(likes_desc, GROUP BY+count)에 트래픽이 몰리면
// 가장 무거운 쿼리라 부하 상승 시 P99 와 DB Threads_running 이 먼저 튄다.
// → "왜 인기순부터 캐싱하나"의 근거 데이터를 만든다.

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
// 측정 대상 정렬: likes_desc(무거움) ↔ latest(가벼움) 를 SORT 로 바꿔가며 비교.
const SORT = __ENV.SORT || 'likes_desc';
const SIZE = __ENV.SIZE || '20';

export const options = {
  scenarios: {
    hot_list: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        // TODO(직접): VU 단계는 환경(로컬 DB 사양)에 맞춰 조정. 꺾이는 지점을 찾는 게 목적.
        { duration: '30s', target: 50 },
        { duration: '1m', target: 200 },
        { duration: '1m', target: 500 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    // TODO(직접): 목표 P99(SLO). 베이스라인 측정 후 현실적인 값으로.
    http_req_duration: ['p(99)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  // 핫셋: 노필터 + 인기순 1페이지 (ADR D4 — 트래픽이 몰리는 곳만 캐싱 후보)
  const res = http.get(`${BASE}/api/v1/products?sort=${SORT}&page=0&size=${SIZE}`);
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}