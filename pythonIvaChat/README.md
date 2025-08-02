# IvaChat AI Agent - Python Version

## Overview
This is the Python implementation of the IvaChat AI Agent, a financial Q&A system that uses OpenAI's GPT models to provide intelligent financial advice.

## Features
- **Question Matching**: Uses AI to map user questions to canonical financial questions
- **Interactive Data Collection**: Asks follow-up questions to gather user-specific financial data
- **Template Processing**: Updates financial templates with user inputs
- **General Financial Advice**: Provides AI-generated advice for non-FAQ questions

## Prerequisites
- Python 3.7 or higher
- OpenAI API key

## Installation

1. **Install Python dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Set up your OpenAI API key**:
   
   **Option 1: Environment Variable (Recommended)**
   ```bash
   # Copy the example file
   cp .env.example .env
   
   # Edit .env file and add your API key
   OPENAI_API_KEY=your-actual-api-key-here
   ```
   
   **Option 2: System Environment Variable**
   ```bash
   # Windows PowerShell
   $env:OPENAI_API_KEY = "your-actual-api-key-here"
   
   # Windows Command Prompt
   set OPENAI_API_KEY=your-actual-api-key-here
   
   # Linux/Mac
   export OPENAI_API_KEY="your-actual-api-key-here"
   ```

## Usage

Run the application:
```bash
python ivachat_cleaned.py
```

## Supported Financial Topics

### Canonical Questions:
- **"Rent Vs Buy"**: Helps users decide between renting and buying a home
- **"Buy now and refinance later"**: Advises on purchasing with high interest rates and refinancing later

### General Financial Questions:
The system can also provide general financial advice on topics like:
- Personal finance
- Investing
- Banking and loans
- Mortgages and insurance
- Budgeting and savings
- Retirement planning
- Taxes

## Example Usage

```
Welcome to IvaChat AI Agent!
==================================================
We can answer most frequently asked financial questions. How could we help? Should I rent or buy a house?

Matched FAQ: Rent Vs Buy
Processing your question about: Should I rent or buy a house?
--------------------------------------------------
Current Saving Balance: 5000
Interest Rate: 3.5
...
```

## Dependencies
- `openai>=1.0.0` - OpenAI API client
- `pandas>=1.5.0` - Data manipulation and analysis

## File Structure
```
pythonIvaChat/
├── ivachat_cleaned.py    # Main Python application
├── requirements.txt      # Python dependencies
├── .env.example         # Environment variable template
└── README.md           # This file
```

## Security Notes
- Never commit your actual API key to version control
- Use environment variables to store sensitive information
- The `.env.example` file shows the format but contains placeholder values

## Troubleshooting
1. **API Key Issues**: Ensure your OpenAI API key is valid and has sufficient credits
2. **Import Errors**: Make sure all dependencies are installed with `pip install -r requirements.txt`
3. **Network Issues**: Ensure you have internet connectivity for API calls
