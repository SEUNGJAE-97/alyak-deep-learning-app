import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        // 첫 번째 API 테스트 (예: 캐시/기타 최적화 적용)
        autocomplete: {
            executor: 'constant-vus',
            exec: 'testAutocomplete', // 실행할 함수 이름
            vus: 10,
            duration: '30s',
        },
        // 두 번째 API 테스트 (RDB 직접 조회)
        autocomplete_rdb: {
            executor: 'constant-vus',
            exec: 'testAutocompleteRdb', // 실행할 함수 이름
            vus: 10,
            duration: '30s',
        },
    },
    thresholds: {
        'http_req_duration{scenario:autocomplete}': ['p(95)>=0'],
        'http_req_duration{scenario:autocomplete_rdb}': ['p(95)>=0'],
    },
};

const params = {
    headers: {
        'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImVtYWlsIjoiYWRtaW5AZXhhbXBsZS5jb20iLCJpZCI6MSwiaWF0IjoxNzc2MTUxNTAwLCJleHAiOjE3NzYxNTUxMDB9.2PEDqQYfCrayUdzaGXiYi__b8EHdBg1PBBWOZ4FSMS0',
    },
};

// 시나리오 1 함수
export function testAutocomplete() {
    http.get(encodeURI('http://localhost:8080/api/pill/autocomplete?keyword=타이'), params);
}

// 시나리오 2 함수
export function testAutocompleteRdb() {
    http.get(encodeURI('http://localhost:8080/api/pill/autocomplete/rdb?keyword=타이'), params);
}