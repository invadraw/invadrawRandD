@echo off
echo ================================================
echo       IvaChat AI Agent - Python Version
echo ================================================
echo.

cd pythonIvaChat

if not exist ivachat_cleaned.py (
    echo Error: Python files not found in pythonIvaChat folder
    pause
    exit /b 1
)

echo Checking Python installation...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Python is not installed or not in PATH
    echo Please install Python 3.7 or higher
    pause
    exit /b 1
)

echo Checking dependencies...
pip show openai >nul 2>&1
if %errorlevel% neq 0 (
    echo Installing Python dependencies...
    pip install -r requirements.txt
)

if "%OPENAI_API_KEY%"=="" (
    echo Warning: OPENAI_API_KEY environment variable not set
    echo You can set it with: $env:OPENAI_API_KEY = "your-api-key-here"
    echo.
)

echo Starting Python IvaChat AI Agent...
echo.
python ivachat_cleaned.py

cd ..
pause
