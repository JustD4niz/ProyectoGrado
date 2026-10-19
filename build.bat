@echo off
setlocal

REM Compilar proyecto JSP+JSTL solo con javac (Maven no disponible)

set WORK=C:\xampp\tomcat\webapps\Proyecto_grado
set TOMCAT_LIB=C:\xampp\tomcat\lib

REM Classpath
set CP=%WORK%\WEB-INF\lib\*;%TOMCAT_LIB%\*;%WORK%\WEB-INF\classes

REM Asegurar directorio classes
if not exist "%WORK%\WEB-INF\classes" mkdir "%WORK%\WEB-INF\classes"

REM Copiar db.properties si existe
if exist "%WORK%\src\db.properties" copy "%WORK%\src\db.properties" "%WORK%\WEB-INF\classes\" > nul

REM Encontrar fuentes
echo Buscando fuentes Java...
dir /s /b "%WORK%\src\*.java" > "%TEMP%\fuentes.txt"

REM Compilar
echo Compilando...
javac -source 1.8 -target 1.8 -encoding UTF-8 -classpath "%CP%" -d "%WORK%\WEB-INF\classes" @%TEMP%\fuentes.txt 2>&1 > "%WORK%\build.log"

echo.
echo Compilacion finalizada. Revisar build.log.
endlocal
