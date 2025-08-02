# IvaChat AI Agent - Financial Q&A System

An intelligent financial advisory chatbot that helps users with frequently asked financial questions using OpenAI's GPT models. Available in both Python and Java implementations.

## 🌟 Features

- **Question Matching**: Uses AI to map user questions to canonical financial questions
- **Interactive Data Collection**: Asks follow-up questions to gather user-specific financial data
- **Template Processing**: Updates financial templates with user inputs
- **General Financial Advice**: Provides AI-generated advice for non-FAQ questions

## 📁 Project Structure

```
IvaChatAIAgent/
├── pythonIvaChat/           # Python implementation
│   ├── ivachat_cleaned.py   # Main Python application
│   ├── requirements.txt     # Python dependencies
│   ├── .env.example        # Environment variable template
│   └── README.md           # Python-specific documentation
│
├── javaIvaChat/            # Java implementation
│   ├── IvaChatAgent.java   # Main Java application
│   ├── pom.xml            # Maven build configuration
│   ├── build.bat          # Windows build script
│   ├── run.bat            # Windows run script
│   ├── .env.example       # Environment variable template
│   └── README.md          # Java-specific documentation
│
├── ivachatprompt.txt      # System documentation and prompts
├── .gitignore             # Git ignore file
└── README.md              # This file
```

## 🚀 Quick Start

### Python Version
```bash
cd pythonIvaChat
pip install -r requirements.txt
# Set your OpenAI API key
export OPENAI_API_KEY="your-api-key-here"  # Linux/Mac
# or
$env:OPENAI_API_KEY = "your-api-key-here"  # Windows PowerShell
python ivachat_cleaned.py
```

### Java Version
```bash
cd javaIvaChat
# Option 1: Use build script (Windows)
build.bat

# Option 2: Use Maven
mvn clean package
java -jar target/ivachat-agent-1.0.0.jar

# Set your OpenAI API key
set OPENAI_API_KEY=your-api-key-here  # Windows
export OPENAI_API_KEY="your-api-key-here"  # Linux/Mac
```

## 💡 Supported Financial Topics

### Canonical Questions:
- **Rent vs Buy**: Decision-making for home purchasing
- **Buy now and refinance later**: High interest rate scenarios

### General Financial Questions:
- Personal finance and budgeting
- Investing and portfolio management
- Banking, loans, and mortgages
- Insurance and risk management
- Retirement planning
- Tax planning and optimization

## 🔧 Prerequisites

### Python Version:
- Python 3.7 or higher
- OpenAI API key

### Java Version:
- Java 11 or higher
- Maven 3.6+ (optional, for easier building)
- OpenAI API key

## 📖 Documentation

- **Python Implementation**: See `pythonIvaChat/README.md`
- **Java Implementation**: See `javaIvaChat/README.md`
- **System Documentation**: See `ivachatprompt.txt`

## 🔒 Security

- API keys are loaded from environment variables
- Sensitive information is not committed to version control
- GitHub secret scanning protection enabled
- Both implementations follow security best practices

## 🤝 Contributing

1. Choose your preferred implementation (Python or Java)
2. Follow the setup instructions in the respective folder
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📝 License

This project is part of the invadraw R&D initiative for financial technology solutions.

---

**Choose your implementation:**
- 🐍 **Python**: Go to `pythonIvaChat/` folder
- ☕ **Java**: Go to `javaIvaChat/` folder

Both versions provide identical functionality with language-specific optimizations.