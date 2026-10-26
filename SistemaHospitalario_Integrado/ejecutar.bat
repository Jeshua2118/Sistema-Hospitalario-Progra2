@echo off
setlocal
cd /d "%~dp0"
if not exist "build\classes" mkdir "build\classes"
(for /r "src\main\java" %%F in (*.java) do @echo "%%F") > "build\fuentes.txt"
javac -encoding UTF-8 -d "build\classes" @"build\fuentes.txt"
if errorlevel 1 (
  echo ERROR: No se pudo compilar el proyecto.
  pause
  exit /b 1
)
java -cp "build\classes" com.mycompany.sistemahospitalario.SistemaHospitalario
