#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PORT=8080

echo "=================================================="
echo " CORELY GYM TRACKER - CLOUD BRIDGE LAUNCHER"
echo "=================================================="

# 1. Verify MySQL
echo "[1/3] Checking MySQL database on port 3306..."
if nc -z 127.0.0.1 3306 2>/dev/null; then
    echo "  ✓ MySQL is active and reachable."
else
    echo "  ✗ MySQL is NOT running on port 3306. Please start MySQL first."
    exit 1
fi

# 2. Verify Backend
echo "[2/3] Checking Spring Boot backend on port ${PORT}..."
if curl -s "http://127.0.0.1:${PORT}/actuator/health" | grep -q "UP"; then
    echo "  ✓ Spring Boot backend is active and healthy."
else
    echo "  → Starting Spring Boot backend in background..."
    cd "${DIR}/backend/gym-tracker-backend"
    if [ -f "target/gym-tracker-backend-0.0.1-SNAPSHOT.jar" ]; then
        nohup java -jar target/gym-tracker-backend-0.0.1-SNAPSHOT.jar > "${DIR}/backend.log" 2>&1 &
    else
        nohup ./mvnw spring-boot:run > "${DIR}/backend.log" 2>&1 &
    fi
    echo "  Waiting for backend startup..."
    for i in {1..20}; do
        sleep 2
        if curl -s "http://127.0.0.1:${PORT}/actuator/health" 2>/dev/null | grep -q "UP"; then
            echo "  ✓ Spring Boot started successfully."
            break
        fi
        if [ $i -eq 20 ]; then
            echo "  ✗ Backend failed to start. See ${DIR}/backend.log"
            exit 1
        fi
    done
fi

# 3. Launch Persistent Cloud Bridge
echo "[3/3] Establishing resilient HTTPS Cloud Bridge on port ${PORT}..."
echo "--------------------------------------------------"
echo "Tunneling local backend to public internet..."
echo "Keep this terminal open to maintain the bridge."
echo "--------------------------------------------------"

while true; do
    npx --yes localtunnel --port ${PORT}
    echo "Tunnel interrupted. Reconnecting in 3 seconds..."
    sleep 3
done
