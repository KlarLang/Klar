@echo off
setlocal enabledelayedexpansion

set "PROJECT_DIR=%USERPROFILE%\Klang"

set ARGS=
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
