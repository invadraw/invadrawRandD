# IvaChat AI Agent - Java Version

## Build and Run Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher (for Maven build)
- OpenAI API key

### Option 1: Build with Maven

1. **Install Maven** (if not already installed):
   - Download from: https://maven.apache.org/download.cgi
   - Follow installation instructions for your OS

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Create executable JAR**:
   ```bash
   mvn clean package
   ```

4. **Run the executable JAR**:
   ```bash
   java -jar target/ivachat-agent-1.0.0.jar
   ```

### Option 2: Compile and Run Manually

1. **Download Gson dependency**:
   ```bash
   curl -o gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
   ```

2. **Compile the Java file**:
   ```bash
   javac -cp gson-2.10.1.jar IvaChatAgent.java
   ```

3. **Run the program**:
   ```bash
   java -cp .:gson-2.10.1.jar IvaChatAgent
   ```

   On Windows:
   ```cmd
   java -cp .;gson-2.10.1.jar IvaChatAgent
   ```

### Setting up the API Key

#### Option 1: Environment Variable (Recommended)
```bash
export OPENAI_API_KEY="your-actual-api-key-here"
```

On Windows:
```cmd
set OPENAI_API_KEY=your-actual-api-key-here
```

#### Option 2: Modify the Code
Edit the `IvaChatAgent.java` file and replace `"your-api-key-here"` with your actual API key.

### Dependencies
- **Gson 2.10.1**: For JSON processing
- **Java HTTP Client**: Built-in (Java 11+)

### Project Structure
```
IvaChatAIAgent/
├── IvaChatAgent.java     # Main Java source file
├── pom.xml              # Maven build configuration
├── README-Java.md       # This file
└── target/              # Build output (created by Maven)
```

### Features
- **Question Matching**: Uses OpenAI API to match user questions to canonical financial questions
- **Interactive Data Collection**: Asks follow-up questions to gather user-specific financial data
- **Template Processing**: Updates financial templates with user inputs
- **General Financial Advice**: Provides AI-generated advice for non-FAQ questions
- **Environment Variable Support**: Secure API key handling

### Troubleshooting
1. **Java Version**: Ensure you're using Java 11 or higher
2. **API Key**: Make sure your OpenAI API key is valid and has sufficient credits
3. **Network**: Ensure you have internet connectivity for API calls
4. **Dependencies**: If using manual compilation, ensure Gson JAR is in the classpath

### Example Usage
```
Welcome to IvaChat AI Agent!
==================================================
We can answer most frequently asked financial questions. How could we help? Should I rent or buy a house?

Matched FAQ: Rent Vs Buy
Processing your question about: Should I rent or buy a house?
--------------------------------------------------
Current Saving Balance: 5000
Interest Rate: 3.5
```
