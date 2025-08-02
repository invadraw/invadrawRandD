@echo off
echo Starting IvaChat AI Agent...
echo.

REM Check if environment variable is set
if "%OPENAI_API_KEY%"=="" (
    echo Warning: OPENAI_API_KEY environment variable not set
    echo You can set it with: set OPENAI_API_KEY=your-actual-api-key-here
    echo.
)

REM Try to run Maven-built JAR first
if exist "target\ivachat-agent-1.0.0.jar" (
    echo Running Maven-built JAR...
    java -jar target\ivachat-agent-1.0.0.jar
) else if exist "IvaChatAgent.class" (
    echo Running compiled classes...
    java -cp .;gson-2.10.1.jar IvaChatAgent
) else (
    echo No compiled version found. Please run build.bat first.
    pause
    exit /b 1
)

echo.
pause
