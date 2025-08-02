import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * IvaChat AI Agent - Financial Q&A System
 * Converts user questions to canonical financial questions and provides interactive responses.
 * For unmatched questions, provides general financial advice using AI if the question is finance-related.
 */
public class IvaChatAgent {
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private String apiKey;
    private HttpClient httpClient;
    private Gson gson;
    private Scanner scanner;
    
    // Canonical questions mapping
    private Map<String, List<String>> canonicalQuestions;
    
    // Template and sequence data
    private List<TemplateData> templateTable;
    private List<SequenceData> sequenceTable;
    
    /**
     * Template data structure
     */
    public static class TemplateData {
        public int faqKey;
        public String faq;
        public Map<String, Object> template;
        
        public TemplateData(int faqKey, String faq, Map<String, Object> template) {
            this.faqKey = faqKey;
            this.faq = faq;
            this.template = template;
        }
    }
    
    /**
     * Sequence data structure
     */
    public static class SequenceData {
        public int faqKey;
        public int seq;
        public boolean isQuestion;
        public String displayText;
        public String key;
        
        public SequenceData(int faqKey, int seq, boolean isQuestion, String displayText, String key) {
            this.faqKey = faqKey;
            this.seq = seq;
            this.isQuestion = isQuestion;
            this.displayText = displayText;
            this.key = key;
        }
    }
    
    /**
     * OpenAI API response structures
     */
    public static class OpenAIResponse {
        public List<Choice> choices;
        
        public static class Choice {
            public Message message;
            
            public static class Message {
                public String content;
            }
        }
    }
    
    /**
     * Constructor - Initialize the IvaChat agent with OpenAI API key
     */
    public IvaChatAgent(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.scanner = new Scanner(System.in);
        
        initializeCanonicalQuestions();
        initializeTemplateTable();
        initializeSequenceTable();
    }
    
    /**
     * Initialize canonical questions
     */
    private void initializeCanonicalQuestions() {
        canonicalQuestions = new HashMap<>();
        
        canonicalQuestions.put("Rent Vs Buy", Arrays.asList(
            "Is it profitable to buy a home now or should I wait?",
            "Does my wealth grow better if I buy a home?",
            "Do I loose more money if I keep renting?"
        ));
        
        canonicalQuestions.put("Buy now and refinance later", Arrays.asList(
            "Is it profitable to buy now and refinance later?",
            "Buy now when interest rates are high and refinnce later when interest rates drops?",
            "Even interest rates are high, home prices are low now. It is better to buy now"
        ));
    }
    
    /**
     * Initialize template table
     */
    private void initializeTemplateTable() {
        templateTable = new ArrayList<>();
        
        Map<String, Object> template1 = new HashMap<>();
        template1.put("nickname", "primary saving");
        template1.put("balance", 1000);
        template1.put("APR", 0.8);
        template1.put("rent", 2500);
        template1.put("salary", 500);
        template1.put("connection", "primaryhome");
        
        Map<String, Object> template2 = new HashMap<>();
        template2.put("nickname", "current home");
        template2.put("balance", 1000000);
        template2.put("APR", 0.8);
        template2.put("connection", "refinance");
        
        templateTable.add(new TemplateData(1000, "Rent Vs Buy", template1));
        templateTable.add(new TemplateData(2000, "Buy now and refinance later", template2));
    }
    
    /**
     * Initialize sequence table
     */
    private void initializeSequenceTable() {
        sequenceTable = new ArrayList<>();
        
        sequenceTable.add(new SequenceData(1000, 1, true, "Current Saving Balance: ", "balance"));
        sequenceTable.add(new SequenceData(1000, 2, true, "Interest Rate: ", "APR"));
        sequenceTable.add(new SequenceData(1000, 3, false, "The canvas is populated from the info given, please continue", "N/A"));
        sequenceTable.add(new SequenceData(1000, 4, true, "How much is rent? ", "rent"));
        sequenceTable.add(new SequenceData(1000, 5, true, "Your salary: ", "salary"));
    }
    
    /**
     * Build prompt for OpenAI API to match user question to canonical questions
     */
    private String buildPrompt(String userQuestion) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Here's a list of canonical questions and their alternate phrasings:\n");
        
        int i = 1;
        for (Map.Entry<String, List<String>> entry : canonicalQuestions.entrySet()) {
            prompt.append("Q").append(i).append(": \"").append(entry.getKey()).append("\"\n");
            for (String alt : entry.getValue()) {
                prompt.append("- \"").append(alt).append("\"\n");
            }
            i++;
        }
        
        prompt.append("\nUser input: \"").append(userQuestion).append("\"\n");
        prompt.append("Which canonical question does this match best? Just return the canonical question exactly as written above, no explanations.");
        
        return prompt.toString();
    }
    
    /**
     * Call OpenAI API
     */
    private String callOpenAI(String systemMessage, String userMessage, double temperature, Integer maxTokens) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("temperature", temperature);
            if (maxTokens != null) {
                requestBody.put("max_tokens", maxTokens);
            }
            
            List<Map<String, String>> messages = Arrays.asList(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", userMessage)
            );
            requestBody.put("messages", messages);
            
            String jsonBody = gson.toJson(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                OpenAIResponse apiResponse = gson.fromJson(response.body(), OpenAIResponse.class);
                return apiResponse.choices.get(0).message.content;
            } else {
                System.err.println("OpenAI API Error: " + response.statusCode() + " - " + response.body());
                return "Error";
            }
            
        } catch (Exception e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            return "Error";
        }
    }
    
    /**
     * Use OpenAI API to match user question to canonical questions
     */
    private String matchQuestion(String userQuestion) {
        String prompt = buildPrompt(userQuestion);
        return callOpenAI(
            "You're an assistant that helps map user questions to canonical questions.",
            prompt,
            0.2,
            null
        );
    }
    
    /**
     * Check if the user question is financial-related using OpenAI API
     */
    private boolean isFinancialQuestion(String userQuestion) {
        String prompt = String.format(
            "Determine if the following question is related to personal finance, investing, banking, loans, mortgages, insurance, budgeting, savings, retirement planning, taxes, or any other financial topics.\n\n" +
            "Question: \"%s\"\n\n" +
            "Respond with only \"YES\" if it's financial-related, or \"NO\" if it's not financial-related.",
            userQuestion
        );
        
        String response = callOpenAI(
            "You are an expert at categorizing questions into financial and non-financial topics.",
            prompt,
            0.1,
            null
        );
        
        return "YES".equals(response.trim().toUpperCase());
    }
    
    /**
     * Get general financial advice from OpenAI API
     */
    private String getFinancialAdvice(String userQuestion) {
        String prompt = String.format(
            "You are a knowledgeable financial advisor. Please provide helpful, accurate, and responsible financial advice for the following question.\n\n" +
            "Important guidelines:\n" +
            "- Provide general educational information, not personalized investment advice\n" +
            "- Suggest consulting with qualified financial professionals for specific situations\n" +
            "- Be clear about risks and limitations\n" +
            "- Keep responses concise but informative\n\n" +
            "Question: %s",
            userQuestion
        );
        
        String response = callOpenAI(
            "You are a helpful financial advisor providing educational information about personal finance topics.",
            prompt,
            0.3,
            500
        );
        
        if ("Error".equals(response)) {
            return "Sorry, I'm unable to provide financial advice at the moment. Please try again later.";
        }
        
        return response;
    }
    
    /**
     * Process user input and return FAQ information
     */
    private ProcessResult sendUserInput(String userText) {
        String matchedQuestion = matchQuestion(userText);
        
        if (!matchedQuestion.startsWith("Q")) {
            return new ProcessResult(0, new HashMap<>(), new ArrayList<>());
        }
        
        // Extract FAQ from matched question
        String faq = matchedQuestion.substring(4).replaceAll("\"", "").trim();
        System.out.println("Matched FAQ: " + faq);
        
        // Find FAQ in template table
        Optional<TemplateData> template = templateTable.stream()
            .filter(t -> t.faq.equals(faq))
            .findFirst();
        
        if (!template.isPresent()) {
            return new ProcessResult(0, new HashMap<>(), new ArrayList<>());
        }
        
        TemplateData templateData = template.get();
        Map<String, Object> jsonBlob = new HashMap<>(templateData.template);
        
        // Get interaction sequence
        List<SequenceData> interactionSequence = sequenceTable.stream()
            .filter(s -> s.faqKey == templateData.faqKey)
            .sorted(Comparator.comparing(s -> s.seq))
            .collect(Collectors.toList());
        
        return new ProcessResult(templateData.faqKey, jsonBlob, interactionSequence);
    }
    
    /**
     * Result class for sendUserInput method
     */
    public static class ProcessResult {
        public int faqKey;
        public Map<String, Object> jsonBlob;
        public List<SequenceData> interactionSequence;
        
        public ProcessResult(int faqKey, Map<String, Object> jsonBlob, List<SequenceData> interactionSequence) {
            this.faqKey = faqKey;
            this.jsonBlob = jsonBlob;
            this.interactionSequence = interactionSequence;
        }
    }
    
    /**
     * Run the main interactive session with the user
     */
    public void runInteractiveSession() {
        System.out.println("Welcome to IvaChat AI Agent!");
        System.out.println("=".repeat(50));
        
        System.out.print("We can answer most frequently asked financial questions. How could we help? ");
        String userText = scanner.nextLine();
        
        ProcessResult result = sendUserInput(userText);
        
        if (result.faqKey != 0) {
            System.out.println("\nProcessing your question about: " + userText);
            System.out.println("-".repeat(50));
            
            // Process each interaction in sequence
            for (SequenceData row : result.interactionSequence) {
                if (row.isQuestion) {
                    // Get user input for questions
                    System.out.print(row.displayText);
                    String inputValue = scanner.nextLine();
                    // Update the JSON blob with user input
                    try {
                        // Try to parse as number if possible
                        if (inputValue.matches("\\d+")) {
                            result.jsonBlob.put(row.key, Integer.parseInt(inputValue));
                        } else if (inputValue.matches("\\d+\\.\\d+")) {
                            result.jsonBlob.put(row.key, Double.parseDouble(inputValue));
                        } else {
                            result.jsonBlob.put(row.key, inputValue);
                        }
                    } catch (NumberFormatException e) {
                        result.jsonBlob.put(row.key, inputValue);
                    }
                } else {
                    // Display information to user
                    System.out.println("\n" + "-".repeat(30));
                    System.out.println("Current Data:");
                    for (Map.Entry<String, Object> entry : result.jsonBlob.entrySet()) {
                        System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                    }
                    System.out.println("=".repeat(30));
                    System.out.println(row.displayText);
                }
            }
            
            // Final output
            System.out.println("\n" + "-".repeat(30));
            System.out.println("Final Configuration:");
            for (Map.Entry<String, Object> entry : result.jsonBlob.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("=".repeat(30));
            System.out.println("You can now move each element to see how the wealth changes as the time changes");
            
        } else {
            // Check if it's a general financial question
            if (isFinancialQuestion(userText)) {
                System.out.println("\nI don't have a specific template for your question, but I can provide some general financial advice:");
                System.out.println("=".repeat(70));
                
                String financialAdvice = getFinancialAdvice(userText);
                System.out.println(financialAdvice);
                
                System.out.println("\n" + "=".repeat(70));
                System.out.println("Note: This is general educational information. For personalized advice,");
                System.out.println("please consult with a qualified financial professional.");
                System.out.println("\nFor more detailed analysis, you can also start drawing using individual");
                System.out.println("blocks in our interactive tool. Please watch the video for guidance.");
            } else {
                System.out.println("Sorry, we specialize in financial questions and do not have a template for your question.");
                System.out.println("You can start drawing using individual blocks for financial planning.");
                System.out.println("Please watch the video for guidance, or ask a finance-related question.");
            }
        }
    }
    
    /**
     * Main method to run the application
     */
    public static void main(String[] args) {
        // Get API key from environment variable or set it here
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.equals("")) {
            apiKey = "your-api-key-here";
            System.out.println("Warning: Using placeholder API key. Set OPENAI_API_KEY environment variable.");
        }
        
        // Create and run the IvaChat agent
        IvaChatAgent agent = new IvaChatAgent(apiKey);
        
        try {
            agent.runInteractiveSession();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nGoodbye! Thanks for using IvaChat AI Agent.");
    }
}
