/**
 * Text Features for BlueJ Teaching
 * 
 * This class demonstrates how to extract features from text
 * for machine learning in a beginner-friendly way.
 * 
 * @author Teaching Version
 * @version 1.0
 */
public class TextFeatures
{
    private String text;
    private int length;
    private int vowelCount;
    private int consonantCount;
    private int uppercaseCount;
    private int specialCharCount;
    
    /**
     * Constructor for objects of class TextFeatures
     * @param inputText The text to analyze
     */
    public TextFeatures(String inputText)
    {
        this.text = inputText;
        calculateFeatures();
    }
    
    /**
     * Calculate all features for the text
     */
    private void calculateFeatures()
    {
        length = text.length();
        vowelCount = countVowels(text);
        consonantCount = countConsonants(text);
        uppercaseCount = countUppercase(text);
        specialCharCount = countSpecialChars(text);
    }
    
    /**
     * Count vowels in the text
     * @param text The text to analyze
     * @return number of vowels
     */
    private int countVowels(String text)
    {
        int count = 0;
        String vowels = "aeiouAEIOU";
        for (int i = 0; i < text.length(); i++) {
            if (vowels.indexOf(text.charAt(i)) != -1) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count consonants in the text
     * @param text The text to analyze
     * @return number of consonants
     */
    private int countConsonants(String text)
    {
        int count = 0;
        String consonants = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
        for (int i = 0; i < text.length(); i++) {
            if (consonants.indexOf(text.charAt(i)) != -1) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count uppercase letters in the text
     * @param text The text to analyze
     * @return number of uppercase letters
     */
    private int countUppercase(String text)
    {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count special characters in the text
     * @param text The text to analyze
     * @return number of special characters
     */
    private int countSpecialChars(String text)
    {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get the original text
     * @return the original text
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * Get the length of the text
     * @return text length
     */
    public int getLength()
    {
        return length;
    }
    
    /**
     * Get the number of vowels
     * @return vowel count
     */
    public int getVowelCount()
    {
        return vowelCount;
    }
    
    /**
     * Get the number of consonants
     * @return consonant count
     */
    public int getConsonantCount()
    {
        return consonantCount;
    }
    
    /**
     * Get the number of uppercase letters
     * @return uppercase count
     */
    public int getUppercaseCount()
    {
        return uppercaseCount;
    }
    
    /**
     * Get the number of special characters
     * @return special character count
     */
    public int getSpecialCharCount()
    {
        return specialCharCount;
    }
    
    /**
     * Get all features as an array
     * @return array of features [length, vowels, consonants, uppercase, special]
     */
    public int[] getAllFeatures()
    {
        return new int[]{length, vowelCount, consonantCount, uppercaseCount, specialCharCount};
    }
    
    /**
     * Display all features in a readable format
     */
    public void showFeatures()
    {
        System.out.println("=== Text Features for: '" + text + "' ===");
        System.out.println("Length: " + length);
        System.out.println("Vowels: " + vowelCount);
        System.out.println("Consonants: " + consonantCount);
        System.out.println("Uppercase letters: " + uppercaseCount);
        System.out.println("Special characters: " + specialCharCount);
    }
    
    /**
     * Test the feature extraction with example texts
     */
    public static void runTest()
    {
        System.out.println("=== Testing Text Features ===");
        
        String[] testTexts = {"therefore", "OMG!", "consequently", "lol", "Demonstrate"};
        
        for (String text : testTexts) {
            TextFeatures features = new TextFeatures(text);
            features.showFeatures();
            System.out.println();
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args)
    {
        runTest();
    }
}