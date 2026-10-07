#!/usr/bin/env bash
# Clean the local development environment
# Usage: ./scripts/clean.sh [--all] [--images] [--volumes]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

CLEAN_ALL=false
CLEAN_IMAGES=false
CLEAN_VOLUMES=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --all)
            CLEAN_ALL=true
            shift
            ;;
        --images)
            CLEAN_IMAGES=true
            shift
            ;;
        --volumes)
            CLEAN_VOLUMES=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--all] [--images] [--volumes]"
            exit 1
            ;;
    esac
done

if [ "${CLEAN_ALL}" = true ]; then
    CLEAN_IMAGES=true
    CLEAN_VOLUMES=true
fi

echo "=== Cleaning local environment ==="

# Stop containers first
echo "Stopping containers..."
docker compose down

if [ "${CLEAN_VOLUMES}" = true ]; then
    echo ""
    echo "WARNING: This will DELETE all MongoDB data!"
    read -p "Are you sure? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Skipping volume removal."
        CLEAN_VOLUMES=false
    fi
fi

if [ "${CLEAN_VOLUMES}" = true ]; then
    echo "Removing volumes..."
    docker compose down -v
    docker volume prune -f
fi

if [ "${CLEAN_IMAGES}" = true ]; then
    echo "Removing project images..."
    docker rmi flm-cloud-native-platform-backend:latest 2>/dev/null || true
    docker rmi flm-cloud-native-platform-frontend:latest 2>/dev/null || true
    docker image prune -f
fi

# Clean build artifacts
echo "Cleaning build artifacts..."
rm -rf backend/target 2>/dev/null || true
rm -rf frontend/dist 2>/dev/null || true
rm -rf frontend/node_modules 2>/dev/null || true
rm -rf frontend/coverage 2>/dev/null || true

echo ""
echo "Clean complete."
echo ""
echo "To rebuild from scratch:"
echo "  ./scripts/start.sh"