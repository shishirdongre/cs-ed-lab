/**
 * Teaching Guide for BlueJ Text Classification
 * 
 * This class provides step-by-step instructions for teachers
 * to use this project in BlueJ for teaching machine learning concepts.
 * 
 * @author Teaching Version
 * @version 1.0
 */
public class TeachingGuide
{
    /**
     * Constructor for objects of class TeachingGuide
     */
    public TeachingGuide()
    {
        System.out.println("Welcome to the BlueJ Text Classification Teaching Guide!");
    }
    
    /**
     * Show the complete teaching guide
     */
    public void showTeachingGuide()
    {
        System.out.println("=== BLUEJ TEXT CLASSIFICATION TEACHING GUIDE ===");
        System.out.println();
        
        showSetupInstructions();
        showLessonPlan();
        showBlueJInstructions();
        showAssessmentIdeas();
        showExtensions();
    }
    
    /**
     * Setup instructions for teachers
     */
    public void showSetupInstructions()
    {
        System.out.println("1. SETUP INSTRUCTIONS");
        System.out.println("=====================");
        System.out.println("• Open BlueJ IDE");
        System.out.println("• Create a new project called 'TextClassification'");
        System.out.println("• Copy these Java files into the project:");
        System.out.println("  - SimpleTextClassifier.java");
        System.out.println("  - TextFeatures.java");
        System.out.println("  - SimpleMLDemo.java");
        System.out.println("  - TeachingGuide.java");
        System.out.println("• Compile all classes (Ctrl+K)");
        System.out.println();
    }
    
    /**
     * Lesson plan for teaching
     */
    public void showLessonPlan()
    {
        System.out.println("2. LESSON PLAN (3-4 sessions)");
        System.out.println("=============================");
        System.out.println();
        System.out.println("SESSION 1: Introduction to Text Classification");
        System.out.println("• Create SimpleTextClassifier object");
        System.out.println("• Use classify() method on different words");
        System.out.println("• Discuss formal vs slang language");
        System.out.println("• Show showFormalWords() and showSlangWords()");
        System.out.println();
        System.out.println("SESSION 2: Feature Extraction");
        System.out.println("• Create TextFeatures objects");
        System.out.println("• Explore getLength(), getVowelCount(), etc.");
        System.out.println("• Use showFeatures() to see all features");
        System.out.println("• Discuss how features help in classification");
        System.out.println();
        System.out.println("SESSION 3: Model Evaluation");
        System.out.println("• Create SimpleMLDemo object");
        System.out.println("• Run testAccuracy() to see performance");
        System.out.println("• Use showConfusionMatrix() to understand errors");
        System.out.println("• Discuss accuracy and evaluation concepts");
        System.out.println();
        System.out.println("SESSION 4: Hands-on Practice");
        System.out.println("• Students add their own words to the classifier");
        System.out.println("• Students test with their own examples");
        System.out.println("• Discuss limitations and improvements");
        System.out.println();
    }
    
    /**
     * BlueJ-specific instructions
     */
    public void showBlueJInstructions()
    {
        System.out.println("3. BLUEJ INSTRUCTIONS FOR STUDENTS");
        System.out.println("===================================");
        System.out.println();
        System.out.println("Getting Started:");
        System.out.println("1. Right-click on SimpleTextClassifier (blue box)");
        System.out.println("2. Select 'new SimpleTextClassifier()'");
        System.out.println("3. Name it 'classifier'");
        System.out.println("4. Right-click on the red object");
        System.out.println("5. Select methods to call (classify, showFormalWords, etc.)");
        System.out.println();
        System.out.println("Try These Commands:");
        System.out.println("• classifier.classify(\"therefore\")");
        System.out.println("• classifier.classify(\"omg\")");
        System.out.println("• classifier.showFormalWords()");
        System.out.println("• classifier.runTest()");
        System.out.println();
        System.out.println("For Features:");
        System.out.println("1. Right-click on TextFeatures");
        System.out.println("2. Select 'new TextFeatures(String)'");
        System.out.println("3. Enter a word like \"hello\"");
        System.out.println("4. Call showFeatures() method");
        System.out.println();
    }
    
    /**
     * Assessment ideas for teachers
     */
    public void showAssessmentIdeas()
    {
        System.out.println("4. ASSESSMENT IDEAS");
        System.out.println("===================");
        System.out.println();
        System.out.println("Beginner Level:");
        System.out.println("• Can students use the classify() method correctly?");
        System.out.println("• Do they understand formal vs slang concepts?");
        System.out.println("• Can they explain what features are?");
        System.out.println();
        System.out.println("Intermediate Level:");
        System.out.println("• Can students add new words to the classifier?");
        System.out.println("• Do they understand accuracy concepts?");
        System.out.println("• Can they interpret the confusion matrix?");
        System.out.println();
        System.out.println("Advanced Level:");
        System.out.println("• Can students modify the classification logic?");
        System.out.println("• Do they understand feature extraction?");
        System.out.println("• Can they suggest improvements to the model?");
        System.out.println();
    }
    
    /**
     * Extension ideas for advanced students
     */
    public void showExtensions()
    {
        System.out.println("5. EXTENSION IDEAS");
        System.out.println("==================");
        System.out.println();
        System.out.println("For Advanced Students:");
        System.out.println("• Add more formal/slang words to the arrays");
        System.out.println("• Create a new feature (e.g., word length categories)");
        System.out.println("• Modify the classification logic");
        System.out.println("• Add a confidence score to predictions");
        System.out.println("• Create a GUI for the classifier");
        System.out.println();
        System.out.println("For Teachers:");
        System.out.println("• Use real datasets from the main project");
        System.out.println("• Introduce more complex ML concepts");
        System.out.println("• Connect to the full ML pipeline");
        System.out.println("• Discuss real-world applications");
        System.out.println();
    }
    
    /**
     * Quick start demo for teachers
     */
    public void quickStartDemo()
    {
        System.out.println("=== QUICK START DEMO ===");
        System.out.println();
        System.out.println("1. Create a SimpleTextClassifier:");
        System.out.println("   SimpleTextClassifier classifier = new SimpleTextClassifier();");
        System.out.println();
        System.out.println("2. Test some words:");
        System.out.println("   classifier.classify(\"therefore\"); // Should return \"formal\"");
        System.out.println("   classifier.classify(\"omg\");       // Should return \"slang\"");
        System.out.println();
        System.out.println("3. See all features:");
        System.out.println("   TextFeatures features = new TextFeatures(\"hello\");");
        System.out.println("   features.showFeatures();");
        System.out.println();
        System.out.println("4. Run complete demo:");
        System.out.println("   SimpleMLDemo demo = new SimpleMLDemo();");
        System.out.println("   demo.runCompleteDemo();");
        System.out.println();
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args)
    {
        TeachingGuide guide = new TeachingGuide();
        guide.showTeachingGuide();
        System.out.println();
        guide.quickStartDemo();
    }
}