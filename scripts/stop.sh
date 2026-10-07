#!/usr/bin/env bash
# Stop the local development stack
# Usage: ./scripts/stop.sh [--remove-volumes]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

REMOVE_VOLUMES=false
if [[ "${1:-}" == "--remove-volumes" ]]; then
    REMOVE_VOLUMES=true
fi

echo "Stopping local stack..."

if ! command -v docker &> /dev/null; then
    echo "ERROR: docker not found in PATH"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "ERROR: Docker daemon not running"
    exit 1
fi

if [ "${REMOVE_VOLUMES}" = true ]; then
    echo "Stopping containers and removing volumes (DATA WILL BE LOST)..."
    read -p "Are you sure? This will delete all MongoDB data. (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Aborted."
        exit 0
    fi
    docker compose down -v
else
    echo "Stopping containers (volumes preserved)..."
    docker compose down
fi

echo "Local stack stopped."