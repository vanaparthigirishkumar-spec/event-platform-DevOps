#!/usr/bin/env bash
# Restart one or all services in the local stack
# Usage: ./scripts/restart.sh [service_name]
# If no service specified, restarts all services

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

SERVICE="${1:-}"

echo "Restarting local stack..."

if ! command -v docker &> /dev/null; then
    echo "ERROR: docker not found in PATH"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "ERROR: Docker daemon not running"
    exit 1
fi

if [ -n "${SERVICE}" ]; then
    echo "Restarting service: ${SERVICE}"
    docker compose restart "${SERVICE}"
else
    echo "Restarting all services..."
    docker compose restart
fi

echo "Waiting for services to become healthy..."
sleep 5

MAX_WAIT=60
ELAPSED=0
while [ ${ELAPSED} -lt ${MAX_WAIT} ]; do
    HEALTHY=$(docker compose ps --format json | jq -r 'select(.Health == "healthy") | .Service' 2>/dev/null | wc -l)
    TOTAL=$(docker compose ps --format json | jq -r '.Service' 2>/dev/null | wc -l)
    
    if [ "${HEALTHY}" -eq "${TOTAL}" ] && [ "${TOTAL}" -gt 0 ]; then
        echo "All services healthy!"
        break
    fi
    
    echo "Waiting for health checks... (${HEALTHY}/${TOTAL} healthy)"
    sleep 3
    ELAPSED=$((ELAPSED + 3))
done

echo ""
docker compose ps