@echo off
setlocal

cd /d "%~dp0"

echo [Travels-Tours] Apagando Docker Compose...
docker compose down

endlocal
