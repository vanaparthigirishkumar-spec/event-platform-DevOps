#!/usr/bin/env bash
# Start the local development stack
# Usage: ./scripts/start.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

echo "Starting local stack..."

if ! command -v docker &> /dev/null; then
    echo "ERROR: docker not found in PATH"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "ERROR: Docker daemon not running"
    exit 1
fi

echo "Building images..."
docker compose build

echo "Starting containers..."
docker compose up -d

echo "Waiting for services to become healthy..."
sleep 5

# Wait for health checks
MAX_WAIT=120
ELAPSED=0
while [ ${ELAPSED} -lt ${MAX_WAIT} ]; do
    HEALTHY=$(docker compose ps --format json | jq -r 'select(.Health == "healthy") | .Service' 2>/dev/null | wc -l)
    TOTAL=$(docker compose ps --format json | jq -r '.Service' 2>/dev/null | wc -l)
    
    if [ "${HEALTHY}" -eq "${TOTAL}" ] && [ "${TOTAL}" -gt 0 ]; then
        echo "All services healthy!"
        break
    fi
    
    echo "Waiting for health checks... (${HEALTHY}/${TOTAL} healthy)"
    sleep 5
    ELAPSED=$((ELAPSED + 5))
done

if [ ${ELAPSED} -ge ${MAX_WAIT} ]; then
    echo "WARNING: Timeout waiting for all services to become healthy"
    docker compose ps
fi

echo ""
echo "Service status:"
docker compose ps

echo ""
echo "Verifying health endpoints..."
sleep 2

curl -sf http://localhost:8080/health > /dev/null && echo "✓ Backend health: OK" || echo "✗ Backend health: FAILED"
curl -sf http://localhost:8080/ready > /dev/null && echo "✓ Backend ready: OK" || echo "✗ Backend ready: FAILED"
curl -sf http://localhost:3000/health > /dev/null && echo "✓ Frontend health: OK" || echo "✗ Frontend health: FAILED"

echo ""
echo "Local stack started successfully!"
echo "  Frontend: http://localhost:3000"
echo "  Backend API: http://localhost:8080"
echo "  MongoDB: localhost:27017"