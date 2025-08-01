#!/usr/bin/env python3
"""
IvaChat AI Agent - Financial Q&A System
Converts user questions to canonical financial questions and provides interactive responses.
For unmatched questions, provides general financial advice using AI if the question is finance-related.
"""

import openai
from openai import OpenAI
import pandas as pd

class IvaChatAgent:
    def __init__(self, api_key):
        """Initialize the IvaChat agent with OpenAI API key."""
        self.client = OpenAI(api_key=api_key)
        self.canonical_questions = {
            "Rent Vs Buy": [
                "Is it profitable to buy a home now or should I wait?",
                "Does my wealth grow better if I buy a home?",
                "Do I loose more money if I keep renting?"
            ],
            "Buy now and refinance later": [
                "Is it profitable to buy now and refinance later?",
                "Buy now when interest rates are high and refinnce later when interest rates drops?",
                "Even interest rates are high, home prices are low now. It is better to buy now"
            ]
        }
        
        # Initialize template table (simulates database table)
        self.template_table_df = self._create_template_table()
        
        # Initialize sequence table (simulates database table)
        self.sequence_table_df = self._create_sequence_table()
    
    def _create_template_table(self):
        """Create the template table DataFrame."""
        data = {
            'FAQ_key': [1000, 2000],
            'FAQ': ['Rent Vs Buy', 'Buy now and refinance later'],
            'Template': [
                {
                    'nickname': 'primary saving',
                    'balance': 1000,
                    'APR': 0.8,
                    'rent': 2500,
                    'salary': 500,
                    'connection': 'primaryhome'
                },
                {
                    'nickname': 'current home',
                    'balance': 1000000,
                    'APR': 0.8,
                    'connection': 'refinance'
                }
            ]
        }
        return pd.DataFrame(data)
    
    def _create_sequence_table(self):
        """Create the sequence table DataFrame."""
        data = {
            'FAQ_key': [1000, 1000, 1000, 1000, 1000],
            'SEQ': [1, 2, 3, 4, 5],
            'IsQuestion': [True, True, False, True, True],
            'Display Text': [
                'Current Saving Balance: ',
                'Interest Rate: ',
                'The canvas is populated from the info given, please continue',
                'How much is rent? ',
                'Your salary: '
            ],
            'key': ['balance', 'APR', 'N/A', 'rent', 'salary']
        }
        return pd.DataFrame(data)
    
    def build_prompt(self, user_question):
        """Build prompt for OpenAI API to match user question to canonical questions."""
        lines = ["Here's a list of canonical questions and their alternate phrasings:"]
        for i, (canon, alternates) in enumerate(self.canonical_questions.items(), 1):
            lines.append(f"Q{i}: \"{canon}\"")
            for alt in alternates:
                lines.append(f"- \"{alt}\"")
        lines.append(f"\nUser input: \"{user_question}\"")
        lines.append("Which canonical question does this match best? Just return the canonical question exactly as written above, no explanations.")
        return "\n".join(lines)
    
    def match_question(self, user_question):
        """Use OpenAI API to match user question to canonical questions."""
        prompt = self.build_prompt(user_question)
        try:
            response = self.client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "You're an assistant that helps map user questions to canonical questions."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.2
            )
            return response.choices[0].message.content
        except Exception as e:
            print(f"Error calling OpenAI API: {e}")
            return "Error"
    
    def is_financial_question(self, user_question):
        """Check if the user question is financial-related using OpenAI API."""
        prompt = f"""
        Determine if the following question is related to personal finance, investing, banking, loans, mortgages, insurance, budgeting, savings, retirement planning, taxes, or any other financial topics.
        
        Question: "{user_question}"
        
        Respond with only "YES" if it's financial-related, or "NO" if it's not financial-related.
        """
        
        try:
            response = self.client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "You are an expert at categorizing questions into financial and non-financial topics."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.1
            )
            return response.choices[0].message.content.strip().upper() == "YES"
        except Exception as e:
            print(f"Error checking if question is financial: {e}")
            return False
    
    def get_financial_advice(self, user_question):
        """Get general financial advice from OpenAI API."""
        prompt = f"""
        You are a knowledgeable financial advisor. Please provide helpful, accurate, and responsible financial advice for the following question. 
        
        Important guidelines:
        - Provide general educational information, not personalized investment advice
        - Suggest consulting with qualified financial professionals for specific situations
        - Be clear about risks and limitations
        - Keep responses concise but informative
        
        Question: {user_question}
        """
        
        try:
            response = self.client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "You are a helpful financial advisor providing educational information about personal finance topics."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                max_tokens=500
            )
            return response.choices[0].message.content
        except Exception as e:
            print(f"Error getting financial advice: {e}")
            return "Sorry, I'm unable to provide financial advice at the moment. Please try again later."
    
    def send_user_input(self, user_text):
        """Process user input and return FAQ information."""
        matched_question = self.match_question(user_text)
        
        if not matched_question.startswith('Q'):
            return 0, {}, pd.DataFrame()
        
        # Extract FAQ from matched question
        faq = matched_question[4:].strip('"')
        print(f"Matched FAQ: {faq}")
        
        # Find FAQ in template table
        faq_matches = self.template_table_df.loc[self.template_table_df['FAQ'] == faq]
        if faq_matches.empty:
            return 0, {}, pd.DataFrame()
        
        faq_key = faq_matches['FAQ_key'].iloc[0]
        faq_json_blob = faq_matches['Template'].iloc[0].copy()  # Make a copy to avoid modifying original
        
        # Get interaction sequence
        interaction_sequence = self.sequence_table_df.loc[
            self.sequence_table_df['FAQ_key'] == faq_key
        ][['SEQ', 'IsQuestion', 'Display Text', 'key']].sort_values('SEQ')
        
        return faq_key, faq_json_blob, interaction_sequence
    
    def run_interactive_session(self):
        """Run the main interactive session with the user."""
        print("Welcome to IvaChat AI Agent!")
        print("=" * 50)
        
        user_text = input("We can answer most frequently asked financial questions. How could we help? ")
        
        faq_key, faq_json_blob, interaction_sequence = self.send_user_input(user_text)
        
        if faq_key != 0:
            print(f"\nProcessing your question about: {user_text}")
            print("-" * 50)
            
            # Process each interaction in sequence
            for index, row in interaction_sequence.iterrows():
                if row['IsQuestion']:
                    # Get user input for questions
                    input_value = input(row['Display Text'])
                    # Update the JSON blob with user input
                    faq_json_blob[row['key']] = input_value
                else:
                    # Display information to user
                    print('\n' + '-' * 30)
                    print("Current Data:")
                    for key, value in faq_json_blob.items():
                        print(f"  {key}: {value}")
                    print('=' * 30)
                    print(row['Display Text'])
            
            # Final output
            print('\n' + '-' * 30)
            print("Final Configuration:")
            for key, value in faq_json_blob.items():
                print(f"  {key}: {value}")
            print('=' * 30)
            print('You can now move each element to see how the wealth changes as the time changes')
        
        else:
            # Check if it's a general financial question
            if self.is_financial_question(user_text):
                print("\nI don't have a specific template for your question, but I can provide some general financial advice:")
                print("=" * 70)
                
                financial_advice = self.get_financial_advice(user_text)
                print(financial_advice)
                
                print("\n" + "=" * 70)
                print("Note: This is general educational information. For personalized advice,")
                print("please consult with a qualified financial professional.")
                print("\nFor more detailed analysis, you can also start drawing using individual")
                print("blocks in our interactive tool. Please watch the video for guidance.")
            else:
                print('Sorry, we specialize in financial questions and do not have a template for your question.')
                print('You can start drawing using individual blocks for financial planning.')
                print('Please watch the video for guidance, or ask a finance-related question.')

def main():
    """Main function to run the application."""
    # Get API key from environment variable or set it here
    import os
    api_key = os.getenv('OPENAI_API_KEY', 'your-api-key-here')
    
    # Create and run the IvaChat agent
    agent = IvaChatAgent(api_key)
    
    try:
        agent.run_interactive_session()
    except KeyboardInterrupt:
        print("\n\nGoodbye! Thanks for using IvaChat AI Agent.")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    #This is the main entry point for the script
    main()
