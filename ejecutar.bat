@echo off
if not exist bin\Main.class (
    echo [AVISO] Compilando fuentes primero...
    call compilar.bat
)
echo ============================================================
echo   INICIANDO SISTEMA BIBLIOTECARIO (UTL - MVC)
echo ============================================================
start javaw -cp "bin;lib\mysql-connector-j-8.4.0.jar" Main
