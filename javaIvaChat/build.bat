@echo off
echo Building IvaChat AI Agent Java Application...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 11 or higher
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo Maven found. Building with Maven...
    call mvn clean package
    if %errorlevel% equ 0 (
        echo.
        echo Build successful! 
        echo Run with: java -jar target/ivachat-agent-1.0.0.jar
    ) else (
        echo Maven build failed
    )
) else (
    echo Maven not found. Attempting manual build...
    
    REM Download Gson if not present
    if not exist gson-2.10.1.jar (
        echo Downloading Gson dependency...
        powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar' -OutFile 'gson-2.10.1.jar'"
    )
    
    REM Compile
    echo Compiling Java source...
    javac -cp gson-2.10.1.jar IvaChatAgent.java
    
    if %errorlevel% equ 0 (
        echo.
        echo Build successful!
        echo Run with: java -cp .;gson-2.10.1.jar IvaChatAgent
    ) else (
        echo Compilation failed
    )
)

echo.
echo Don't forget to set your OpenAI API key:
echo set OPENAI_API_KEY=your-actual-api-key-here
echo.
pause
