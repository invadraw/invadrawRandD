@echo off
echo ================================================
echo        IvaChat AI Agent - Java Version
echo ================================================
echo.

cd javaIvaChat

if not exist IvaChatAgent.java (
    echo Error: Java files not found in javaIvaChat folder
    pause
    exit /b 1
)

if "%OPENAI_API_KEY%"=="" (
    echo Warning: OPENAI_API_KEY environment variable not set
    echo You can set it with: set OPENAI_API_KEY=your-api-key-here
    echo.
)

echo Building and running Java IvaChat AI Agent...
echo.

REM Try to run the build script
if exist build.bat (
    call build.bat
) else (
    echo Build script not found, trying direct compilation...
    java --version >nul 2>&1
    if %errorlevel% neq 0 (
        echo Error: Java is not installed or not in PATH
        pause
        exit /b 1
    )
    javac IvaChatAgent.java
)

cd ..
pause
