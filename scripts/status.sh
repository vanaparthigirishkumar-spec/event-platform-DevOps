#!/usr/bin/env bash
# Check container status and health
# Usage: ./scripts/status.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

echo "=== Container Status ==="
docker compose ps

echo ""
echo "=== Health Check Details ==="
for service in mongodb backend frontend; do
    CONTAINER=$(docker compose ps -q "${service}" 2>/dev/null)
    if [ -n "${CONTAINER}" ]; then
        HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "${CONTAINER}" 2>/dev/null || echo "no-healthcheck")
        STARTED=$(docker inspect --format='{{.State.StartedAt}}' "${CONTAINER}" 2>/dev/null)
        echo "${service}: ${HEALTH} (started: ${STARTED})"
    else
        echo "${service}: not running"
    fi
done