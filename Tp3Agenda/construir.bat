@echo off
REM Compila la agenda, arma el .jar y genera el ejecutable portable (.exe)
REM Requiere JDK 17 o superior (incluye jpackage)
cd /d "%~dp0"

if exist build rmdir /s /q build
if exist dist rmdir /s /q dist
mkdir build\classes
mkdir build\jar

javac -encoding UTF-8 -d build\classes src\agenda\*.java || goto error
jar --create --file build\jar\Agenda.jar --main-class agenda.AgendaGUI -C build\classes . || goto error

REM app-image = carpeta portable con Agenda.exe + Java incluido (no hace falta instalar Java)
jpackage --type app-image --name Agenda --input build\jar --main-jar Agenda.jar --main-class agenda.AgendaGUI --add-modules java.desktop --dest dist || goto error

echo.
echo Listo: dist\Agenda\Agenda.exe  (copiar la carpeta dist\Agenda completa)
goto :eof

:error
echo Hubo un error al construir.
exit /b 1
