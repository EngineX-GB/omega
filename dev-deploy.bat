@echo off

call setenv.bat

call mvn clean package -Pprod

if %ERRORLEVEL% neq 0 (
    echo Maven command failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
) else (
    powershell.exe -executionpolicy bypass -File ./dev-deploy.ps1 %CD%\release %CD%\dev 
)
SET PROJECT_DIR=%CD%
echo Deleting %PROJECT_DIR%\dev
rmdir /s /q %PROJECT_DIR%\dev
call powershell.exe -executionpolicy bypass -File ./dev-deploy.ps1 %PROJECT_DIR%\release %PROJECT_DIR%\dev\install
cd %PROJECT_DIR%\dev\install
call powershell.exe -executionpolicy bypass -File installer.ps1 "%USERPROFILE%\Documents\Development\Tools\graalvm-ce-java11-22.2.0" "%PROJECT_DIR%\dev\local"
cd %PROJECT_DIR%
@echo on
