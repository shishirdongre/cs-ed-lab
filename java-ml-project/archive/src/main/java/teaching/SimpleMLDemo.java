/**
 * Simple Machine Learning Demo for BlueJ Teaching
 * 
 * This class demonstrates basic machine learning concepts
 * in a beginner-friendly way for BlueJ IDE.
 * 
 * @author Teaching Version
 * @version 1.0
 */
public class SimpleMLDemo
{
    private SimpleTextClassifier classifier;
    private String[] testWords;
    private String[] expectedLabels;
    
    /**
     * Constructor for objects of class SimpleMLDemo
     */
    public SimpleMLDemo()
    {
        classifier = new SimpleTextClassifier();
        setupTestData();
    }
    
    /**
     * Set up test data for the demo
     */
    private void setupTestData()
    {
        testWords = new String[]{"therefore", "omg", "consequently", "lol", 
                                "demonstrate", "yolo", "however", "btw"};
        expectedLabels = new String[]{"formal", "slang", "formal", "slang",
                                     "formal", "slang", "formal", "slang"};
    }
    
    /**
     * Run a simple accuracy test
     */
    public void testAccuracy()
    {
        System.out.println("=== Testing Classifier Accuracy ===");
        
        int correct = 0;
        int total = testWords.length;
        
        for (int i = 0; i < testWords.length; i++) {
            String predicted = classifier.classify(testWords[i]);
            String expected = expectedLabels[i];
            
            System.out.println("Word: '" + testWords[i] + "'");
            System.out.println("  Expected: " + expected);
            System.out.println("  Predicted: " + predicted);
            System.out.println("  Correct: " + (predicted.equals(expected) ? "Yes" : "No"));
            System.out.println();
            
            if (predicted.equals(expected)) {
                correct++;
            }
        }
        
        double accuracy = (double) correct / total * 100;
        System.out.println("Accuracy: " + correct + "/" + total + " = " + 
                          String.format("%.1f", accuracy) + "%");
    }
    
    /**
     * Demonstrate feature extraction
     */
    public void demonstrateFeatures()
    {
        System.out.println("=== Demonstrating Text Features ===");
        
        String[] examples = {"therefore", "OMG!", "consequently"};
        
        for (String word : examples) {
            TextFeatures features = new TextFeatures(word);
            features.showFeatures();
            System.out.println();
        }
    }
    
    /**
     * Show a confusion matrix (simplified for beginners)
     */
    public void showConfusionMatrix()
    {
        System.out.println("=== Confusion Matrix ===");
        System.out.println("This shows how many predictions were correct/incorrect");
        System.out.println();
        
        int formalCorrect = 0, formalTotal = 0;
        int slangCorrect = 0, slangTotal = 0;
        
        for (int i = 0; i < testWords.length; i++) {
            String predicted = classifier.classify(testWords[i]);
            String expected = expectedLabels[i];
            
            if (expected.equals("formal")) {
                formalTotal++;
                if (predicted.equals("formal")) {
                    formalCorrect++;
                }
            } else if (expected.equals("slang")) {
                slangTotal++;
                if (predicted.equals("slang")) {
                    slangCorrect++;
                }
            }
        }
        
        System.out.println("Formal words: " + formalCorrect + "/" + formalTotal + " correct");
        System.out.println("Slang words: " + slangCorrect + "/" + slangTotal + " correct");
    }
    
    /**
     * Interactive prediction demo
     */
    public void interactivePrediction()
    {
        System.out.println("=== Interactive Prediction Demo ===");
        System.out.println("Try these examples:");
        System.out.println("classifier.classify(\"therefore\")");
        System.out.println("classifier.classify(\"omg\")");
        System.out.println("classifier.classify(\"your_word_here\")");
        System.out.println();
        System.out.println("Or call testAccuracy() to see how well it performs!");
    }
    
    /**
     * Run the complete demo
     */
    public void runCompleteDemo()
    {
        System.out.println("=== Complete Machine Learning Demo ===");
        System.out.println();
        
        // Show what the classifier knows
        classifier.showFormalWords();
        System.out.println();
        classifier.showSlangWords();
        System.out.println();
        
        // Test accuracy
        testAccuracy();
        
        // Show features
        demonstrateFeatures();
        
        // Show confusion matrix
        showConfusionMatrix();
        
        System.out.println("=== Demo Complete ===");
        System.out.println("This demonstrates basic concepts of:");
        System.out.println("1. Text classification");
        System.out.println("2. Feature extraction");
        System.out.println("3. Model evaluation");
        System.out.println("4. Accuracy measurement");
    }
    
    /**
     * Get the classifier for interactive use
     * @return the text classifier
     */
    public SimpleTextClassifier getClassifier()
    {
        return classifier;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args)
    {
        SimpleMLDemo demo = new SimpleMLDemo();
        demo.runCompleteDemo();
    }
}