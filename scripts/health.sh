#!/usr/bin/env bash
# Check application health endpoints
# Usage: ./scripts/health.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

check_endpoint() {
    local name=$1
    local url=$2
    local expected_status=${3:-200}
    
    echo -n "${name}: "
    RESPONSE=$(curl -s -w "\n%{http_code}" -o /tmp/health_response.json "${url}" 2>/dev/null || echo "000")
    HTTP_CODE=$(echo "${RESPONSE}" | tail -1)
    
    if [ "${HTTP_CODE}" = "${expected_status}" ]; then
        echo "OK (HTTP ${HTTP_CODE})"
        cat /tmp/health_response.json | jq . 2>/dev/null || cat /tmp/health_response.json
    else
        echo "FAILED (HTTP ${HTTP_CODE})"
        return 1
    fi
}

echo "=== Application Health Checks ==="
echo ""

ALL_OK=true

check_endpoint "Backend /health" "http://localhost:8080/health" || ALL_OK=false
echo ""
check_endpoint "Backend /ready" "http://localhost:8080/ready" || ALL_OK=false
echo ""
check_endpoint "Frontend /health" "http://localhost:3000/health" || ALL_OK=false
echo ""
check_endpoint "Frontend → Backend /health" "http://localhost:3000/api/health" || ALL_OK=false
echo ""
check_endpoint "Frontend → Backend /ready" "http://localhost:3000/api/ready" || ALL_OK=false

echo ""
echo "=== API Connectivity ==="
curl -sf "http://localhost:3000/api/events?page=0&size=1" > /dev/null && echo "✓ /api/events: OK" || echo "✗ /api/events: FAILED"
curl -sf "http://localhost:3000/api/camps?page=0&size=1" > /dev/null && echo "✓ /api/camps: OK" || echo "✗ /api/camps: FAILED"

echo ""
if [ "${ALL_OK}" = true ]; then
    echo "All health checks PASSED"
    exit 0
else
    echo "Some health checks FAILED"
    exit 1
fi