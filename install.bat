::install_Klang.bat 
:: Run this script in CMD as Administrator 

@echo off 
SET PROJECT_DIR=%HOMEPATH%\Klang 
SET INSTALL_DIR=C:\Program Files\Klang 
SET BIN_DIR=C:\Klang\bin 

ECHO Navigating to %PROJECT_DIR% ... 
CD %PROJECT_DIR% 
IF ERRORLEVEL 1 ( 
    ECHO Error: Unable to enter directory %PROJECT_DIR%. Aborting. 
    EXIT /B 1 
) 

gradlew.bat :cli:shadowJar

ECHO Creating %INSTALL_DIR% ... 
MKDIR "%INSTALL_DIR%" 
:: MKDIR in batch does not give an error if the directory already exists, so we do not check it explicitly here. 
ECHO Directory %INSTALL_DIR% created/existing. 

ECHO Copying klang.jar to %INSTALL_DIR%\klang.jar ... 
COPY cli\build\libs\klang.jar "%INSTALL_DIR%\klang.jar" > NUL 
IF ERRORLEVEL 1 ( 
    ECHO Failed to copy klang.jar. 
    EXIT /B 1 
) 
ECHO klang.jar copied successfully. 

ECHO Creating binary directory %BIN_DIR% ... 
MKDIR "%BIN_DIR%" 

ECHO Copying script 'k' to %BIN_DIR%\k ... 
COPY %PROJECT_DIR%\cli\bin\k "%BIN_DIR%\k" > NUL 
IF ERRORLEVEL 1 ( 
    ECHO Failed to copy script k. 
    EXIT /B 1 
) 
ECHO k copied to %BIN_DIR%\k successfully. 

ECHO Verifying the installation by executing 'k -V'... 
:: This test will only work if C:\Klang\bin is in the system PATH. 
k -V 
IF ERRORLEVEL 1 ( 
    ECHO Error: The 'k -V' command failed. 
    ECHO The installation may not have been completed correctly. 
    EXIT /B 1 
) ELSE ( 
    ECHO Success: The 'k -V' command ran correctly. 
    ECHO Installation completed successfully! 
) 

EXIT /B 0