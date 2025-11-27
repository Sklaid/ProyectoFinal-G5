@echo off
REM Setup Maven Settings for Nexus
REM This script helps configure Maven to use Nexus Repository Manager

setlocal enabledelayedexpansion

set MAVEN_HOME_DIR=%USERPROFILE%\.m2
set SETTINGS_FILE=%MAVEN_HOME_DIR%\settings.xml
set TEMPLATE_FILE=backend\settings.xml.template

echo =========================================
echo Maven Nexus Configuration Setup
echo =========================================
echo.

REM Check if .m2 directory exists
if not exist "%MAVEN_HOME_DIR%" (
    echo Creating Maven home directory: %MAVEN_HOME_DIR%
    mkdir "%MAVEN_HOME_DIR%"
)

REM Check if settings.xml already exists
if exist "%SETTINGS_FILE%" (
    echo [33m⚠ Warning: settings.xml already exists at %SETTINGS_FILE%[0m
    echo.
    set /p BACKUP="Do you want to backup the existing file? (Y/N): "
    if /i "!BACKUP!"=="Y" (
        set BACKUP_FILE=%SETTINGS_FILE%.backup.%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
        set BACKUP_FILE=!BACKUP_FILE: =0!
        copy "%SETTINGS_FILE%" "!BACKUP_FILE!" >nul
        echo [32m✓ Backup created: !BACKUP_FILE![0m
    )
    echo.
    set /p OVERWRITE="Do you want to overwrite the existing settings.xml? (Y/N): "
    if /i not "!OVERWRITE!"=="Y" (
        echo [33mSetup cancelled. No changes made.[0m
        goto :end
    )
)

REM Copy template to settings.xml
echo Copying settings template...
copy "%TEMPLATE_FILE%" "%SETTINGS_FILE%" >nul
if %errorlevel% equ 0 (
    echo [32m✓ Settings file created: %SETTINGS_FILE%[0m
) else (
    echo [31m✗ Failed to create settings file[0m
    goto :end
)

echo.
echo =========================================
echo Configuration Complete!
echo =========================================
echo.
echo Maven settings file location: %SETTINGS_FILE%
echo.
echo [33mIMPORTANT: Set environment variables for Nexus credentials:[0m
echo.
echo   Windows (PowerShell):
echo     $env:NEXUS_USERNAME = "admin"
echo     $env:NEXUS_PASSWORD = "admin123"
echo.
echo   Windows (CMD):
echo     set NEXUS_USERNAME=admin
echo     set NEXUS_PASSWORD=admin123
echo.
echo   Linux/Mac:
echo     export NEXUS_USERNAME=admin
echo     export NEXUS_PASSWORD=admin123
echo.
echo For permanent configuration, add these to your system environment variables.
echo.
echo =========================================
echo Testing Maven Configuration
echo =========================================
echo.
echo To test the configuration, run:
echo   cd backend
echo   mvn deploy -DskipTests
echo.

:end
endlocal
