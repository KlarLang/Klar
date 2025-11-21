@echo off
setlocal enabledelayedexpansion

set "set "PROJECT_DIR=%~dp0"

set ARGS=%*
for %%A in (%*) do (
    if exist "%%~fA" (
        set "ABS=%%~fA"
    ) else (
        set "ABS=%%A"
    )
    set "ARGS=!ARGS! !ABS!"
)

cd "%PROJECT_DIR%" || exit /b 1

.\gradlew.bat :cli:run --args="%ARGS%"
