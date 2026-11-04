#!/usr/bin/env bash
set -e

BASE_URL="${API_BASE_URL:-http://localhost:8080}"

echo "=========================================="
echo "Iniciando Smoke Test contra ${BASE_URL}"
echo "=========================================="

echo -e "\n1. Verificando /actuator/health..."
HEALTH=$(curl -s "${BASE_URL}/actuator/health")
STATUS=$(echo "$HEALTH" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

if [ "$STATUS" != "UP" ]; then
    echo "❌ Error en healthcheck: respuesta $HEALTH"
    exit 1
fi
echo "✔ Healthcheck OK: status = UP"

echo -e "\n2. Autenticando con admin semilla (admin@camisastienda.com)..."
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@camisastienda.com","password":"admin123"}')

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Error al iniciar sesión. Respuesta: $LOGIN_RESPONSE"
    exit 1
fi
echo "✔ Login exitoso. Token JWT obtenido."

echo -e "\n3. Consultando catálogo de productos (/api/productos)..."
PRODUCTOS_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $TOKEN" "${BASE_URL}/api/productos")

if [ "$PRODUCTOS_STATUS" -ne 200 ]; then
    echo "❌ Error al listar productos. Código HTTP: $PRODUCTOS_STATUS"
    exit 1
fi
echo "✔ Catálogo consultado con éxito (HTTP 200)."

echo -e "\n=========================================="
echo "✔ Todos los Smoke Tests pasaron exitosamente"
echo "=========================================="

