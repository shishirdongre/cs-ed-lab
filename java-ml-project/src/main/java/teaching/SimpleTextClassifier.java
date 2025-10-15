/**
 * Simple Text Classifier for BlueJ Teaching
 * 
 * This class demonstrates basic text classification concepts
 * in a beginner-friendly way for BlueJ IDE.
 * 
 * @author Teaching Version
 * @version 1.0
 */
public class SimpleTextClassifier
{
    // Sample data for teaching
    private String[] formalWords = {"therefore", "however", "consequently", "furthermore", 
                                   "demonstrate", "illustrate", "analyze", "evaluate"};
    
    private String[] slangWords = {"omg", "lol", "btw", "fyi", "tbh", "imo", "yolo", "lit"};
    
    /**
     * Constructor for objects of class SimpleTextClassifier
     */
    public SimpleTextClassifier()
    {
        System.out.println("Simple Text Classifier created!");
        System.out.println("This classifier can tell if a word is formal or slang.");
    }
    
    /**
     * Check if a word is formal
     * @param word The word to check
     * @return true if the word is formal, false otherwise
     */
    public boolean isFormal(String word)
    {
        for (String formalWord : formalWords) {
            if (word.toLowerCase().equals(formalWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a word is slang
     * @param word The word to check
     * @return true if the word is slang, false otherwise
     */
    public boolean isSlang(String word)
    {
        for (String slangWord : slangWords) {
            if (word.toLowerCase().equals(slangWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Classify a word as formal or slang
     * @param word The word to classify
     * @return "formal", "slang", or "unknown"
     */
    public String classify(String word)
    {
        if (isFormal(word)) {
            return "formal";
        } else if (isSlang(word)) {
            return "slang";
        } else {
            return "unknown";
        }
    }
    
    /**
     * Show all formal words
     */
    public void showFormalWords()
    {
        System.out.println("Formal words I know:");
        for (int i = 0; i < formalWords.length; i++) {
            System.out.println((i + 1) + ". " + formalWords[i]);
        }
    }
    
    /**
     * Show all slang words
     */
    public void showSlangWords()
    {
        System.out.println("Slang words I know:");
        for (int i = 0; i < slangWords.length; i++) {
            System.out.println((i + 1) + ". " + slangWords[i]);
        }
    }
    
    /**
     * Test the classifier with example words
     */
    public void runTest()
    {
        System.out.println("=== Testing the Text Classifier ===");
        
        String[] testWords = {"therefore", "omg", "consequently", "lol", "demonstrate", "yolo"};
        
        for (String word : testWords) {
            String classification = classify(word);
            System.out.println("'" + word + "' is " + classification);
        }
    }
    
    /**
     * Interactive demo for students
     */
    public void interactiveDemo()
    {
        System.out.println("=== Interactive Text Classification Demo ===");
        System.out.println("I can classify words as formal or slang!");
        System.out.println("Try calling classify(\"your_word\") to test a word.");
        System.out.println("Or call runTest() to see examples.");
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args)
    {
        SimpleTextClassifier classifier = new SimpleTextClassifier();
        classifier.runTest();
        classifier.interactiveDemo();
    }
}