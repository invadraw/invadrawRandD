# IvaChat AI Agent - Financial Q&A System

An intelligent financial advisory chatbot that helps users with frequently asked financial questions using OpenAI's GPT models.

## Features

- **Question Matching**: Uses AI to map user questions to canonical financial questions
- **Interactive Data Collection**: Asks follow-up questions to gather user-specific financial data
- **Template Processing**: Updates financial templates with user inputs
- **General Financial Advice**: Provides AI-generated advice for non-FAQ questions

## Setup

1. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Set up OpenAI API Key**:
   - Copy `.env.example` to `.env`
   - Add your OpenAI API key to the `.env` file:
     ```
     OPENAI_API_KEY=your-actual-api-key-here
     ```
   - Or set it as an environment variable in your system

3. **Run the Application**:
   ```bash
   python ivachat_cleaned.py
   ```

## Supported Financial Topics

- **Rent vs Buy**: Decision-making for home purchasing
- **Buy now and refinance later**: High interest rate scenarios
- **General Financial Questions**: AI-powered responses for other finance topics

## Project Structure

- `ivachat_cleaned.py` - Main application (recommended)
- `ivachat.py` - Original notebook code
- `ivachatprompt.txt` - System documentation and prompts
- `requirements.txt` - Python dependencies
- `.env.example` - Environment variable template

## Security

- API keys are loaded from environment variables
- Sensitive information is not committed to version control
- GitHub secret scanning protection enabled