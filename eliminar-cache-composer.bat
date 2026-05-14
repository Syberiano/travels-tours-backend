@echo off
setlocal

cd /d "%~dp0"

echo [Travels-Tours] Eliminando cache en Docker Compose...
docker compose build --no-cache

endlocal
