# Agenda Electrónica (Java + Swing)

## Requisitos cumplidos
| Requisito | Dónde |
|---|---|
| Cada registro es un objeto | `src/agenda/Contacto.java` (nombre, apellido, teléfono, email, dirección) |
| Lista dinámica | `ArrayList<Contacto>` en `src/agenda/Agenda.java` |
| Genera un txt con todos los registros | `agenda.txt`, se guarda solo en cada cambio y con el botón **Generar TXT** |
| Portable (ejecutable) | `dist/Agenda/Agenda.exe` (incluye Java, no hace falta instalarlo) |

## Funciones
Agregar, modificar, eliminar (seleccionando en la tabla), buscar por nombre/apellido/teléfono, ordenar A-Z y generar el TXT. Al abrir el programa se cargan los registros guardados en `agenda.txt`.

## Construir el ejecutable
Con JDK 17 o superior, ejecutar `construir.bat`. Genera la carpeta `dist/Agenda`; para llevarla a otra PC se copia **la carpeta completa** (Agenda.exe + app + runtime). El archivo `agenda.txt` se crea al lado de `Agenda.exe`.

## Tamaño reducido
Para poder subirlo a la plataforma, el Java incluido en `dist/Agenda/runtime` se achicó quitando lo que la agenda no usa (procesador XML, SSL/keytool, look & feel Motif y Nimbus, librerías de sonido, splash, JPEG y efectos de imagen, archivos `.lib` de desarrollo y las DLL de compatibilidad con Windows 7/8). Requiere **Windows 10 u 11 de 64 bits**. La carpeta `build` no se incluye porque `construir.bat` la vuelve a generar.
