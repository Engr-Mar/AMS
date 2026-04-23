@echo off
setlocal

echo Searching for ams-0.0.1.jar... please wait

set "JAR_FILE="

REM 1. Check fixed C: path first
if exist "C:\Project\AMSystem\AMS\build\libs\ams-0.0.1.jar" (
    set "JAR_FILE=C:\Project\AMSystem\AMS\build\libs\ams-0.0.1.jar"
    goto RUN
)

REM 2. Search entire user profile using dir
for /f "delims=" %%F in ('dir "%USERPROFILE%\*" /s /b ^| findstr /i "\\ams-0.0.1.jar$"') do (
    set "JAR_FILE=%%F"
    goto RUN
)

REM 3. Not found
echo ERROR: ams-0.0.1.jar not found!
pause
exit /b

:RUN
echo Found JAR at: "%JAR_FILE%"

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo Java not found. Installing JDK 25...
    winget install EclipseAdoptium.Temurin.25.JDK -e --silent
)

REM Run the JAR
if exist "%JAR_FILE%" (
    echo Running application...
    java -jar "%JAR_FILE%"
) else (
    echo ERROR: JAR file not found!
)

pause