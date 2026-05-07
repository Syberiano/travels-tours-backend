@echo off
setlocal

cd /d "%~dp0"

echo [Travels-Tours] Iniciando Docker Compose...
docker compose up --build

endlocal
