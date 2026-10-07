#!/usr/bin/env bash
# Basic troubleshooting for the local stack
# Usage: ./scripts/troubleshoot.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

echo "=== Troubleshooting Report ==="
echo "Generated: $(date)"
echo ""

echo "--- Docker ---"
docker --version
docker compose version
echo ""

echo "--- Docker Daemon ---"
docker info 2>/dev/null | head -20 || echo "Docker daemon not accessible"
echo ""

echo "--- Container Status ---"
docker compose ps
echo ""

echo "--- Container Health ---"
for service in mongodb backend frontend; do
    CONTAINER=$(docker compose ps -q "${service}" 2>/dev/null)
    if [ -n "${CONTAINER}" ]; then
        HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "${CONTAINER}" 2>/dev/null || echo "no-healthcheck")
        RESTARTS=$(docker inspect --format='{{.RestartCount}}' "${CONTAINER}" 2>/dev/null)
        echo "${service}: health=${HEALTH}, restarts=${RESTARTS}"
    else
        echo "${service}: not running"
    fi
done
echo ""

echo "--- Network ---"
docker network ls | grep event-platform || echo "Network not found"
echo ""

echo "--- Volumes ---"
docker volume ls | grep event-platform || echo "Volumes not found"
echo ""

echo "--- Port Bindings ---"
for port in 3000 8080 27017; do
    if ss -tlnp 2>/dev/null | grep -q ":${port} "; then
        echo "Port ${port}: LISTENING"
    else
        echo "Port ${port}: NOT LISTENING"
    fi
done
echo ""

echo "--- Health Endpoints ---"
for endpoint in "http://localhost:8080/health" "http://localhost:8080/ready" "http://localhost:3000/health" "http://localhost:3000/api/health"; do
    if curl -sf "${endpoint}" > /dev/null 2>&1; then
        echo "✓ ${endpoint}"
    else
        echo "✗ ${endpoint}"
    fi
done
echo ""

echo "--- Recent Backend Logs (last 30 lines) ---"
docker compose logs --tail 30 backend 2>/dev/null || echo "No backend logs"
echo ""

echo "--- Recent Frontend Logs (last 30 lines) ---"
docker compose logs --tail 30 frontend 2>/dev/null || echo "No frontend logs"
echo ""

echo "--- Recent MongoDB Logs (last 20 lines) ---"
docker compose logs --tail 20 mongodb 2>/dev/null || echo "No mongodb logs"
echo ""

echo "--- Disk Usage ---"
docker system df
echo ""

echo "=== End of Report ==="