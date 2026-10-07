#!/usr/bin/env bash
# View container logs
# Usage: ./scripts/logs.sh [service_name] [--follow] [--tail N]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

SERVICE="${1:-}"
FOLLOW=false
TAIL_LINES=100

shift || true
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--follow)
            FOLLOW=true
            shift
            ;;
        -t|--tail)
            TAIL_LINES="${2:-100}"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

if ! command -v docker &> /dev/null; then
    echo "ERROR: docker not found in PATH"
    exit 1
fi

if [ -n "${SERVICE}" ]; then
    echo "=== Logs for ${SERVICE} ==="
    if [ "${FOLLOW}" = true ]; then
        docker compose logs -f --tail "${TAIL_LINES}" "${SERVICE}"
    else
        docker compose logs --tail "${TAIL_LINES}" "${SERVICE}"
    fi
else
    echo "=== Logs for all services ==="
    if [ "${FOLLOW}" = true ]; then
        docker compose logs -f --tail "${TAIL_LINES}"
    else
        docker compose logs --tail "${TAIL_LINES}"
    fi
fi