$ErrorActionPreference = "Stop"

$baseUrl = if ($env:API_BASE_URL) { $env:API_BASE_URL } else { "http://localhost:8080" }
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Iniciando Smoke Test contra $baseUrl" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# 1. Healthcheck
Write-Host "`n1. Verificando /actuator/health..." -ForegroundColor Yellow
$health = Invoke-RestMethod -Uri "$baseUrl/actuator/health" -Method Get
if ($health.status -ne "UP") {
    Write-Error "Fallo en healthcheck: estado $($health.status)"
}
Write-Host "✔ Healthcheck OK: status = $($health.status)" -ForegroundColor Green

# 2. Login con admin semilla
Write-Host "`n2. Autenticando con admin semilla (admin@camisastienda.com)..." -ForegroundColor Yellow
$loginBody = @{
    email = "admin@camisastienda.com"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token
if (-not $token) {
    Write-Error "No se recibio token JWT en la respuesta de login"
}
Write-Host "✔ Login exitoso. Token recibido correctamente." -ForegroundColor Green

# 3. Listar productos
Write-Host "`n3. Consultando catalogo de productos (/api/productos)..." -ForegroundColor Yellow
$headers = @{
    Authorization = "Bearer $token"
}
$productos = Invoke-RestMethod -Uri "$baseUrl/api/productos" -Method Get -Headers $headers
Write-Host "✔ Catalogo consultado con exito. Total de productos recibidos: $($productos.Count)" -ForegroundColor Green

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "✔ Todos los Smoke Tests pasaron exitosamente" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan

