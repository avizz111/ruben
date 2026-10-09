@echo off
if not exist bin mkdir bin
echo ============================================================
echo   COMPILANDO SISTEMA BIBLIOTECARIO (UTL - MVC)
echo ============================================================
echo Compilando archivos Java...
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.4.0.jar;src" -d bin src\model\*.java src\controller\*.java src\view\*.java src\Main.java
if %ERRORLEVEL% equ 0 (
    echo [EXITO] Compilacion completada correctamente en la carpeta 'bin'.
) else (
    echo [ERROR] Ocurrio un error durante la compilacion.
)
echo.
pause
