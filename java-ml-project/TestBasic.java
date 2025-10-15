// Simple test to verify Java setup works
public class TestBasic {
    public static void main(String[] args) {
        System.out.println("=== Java Text Classification Test ===");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        
        // Test basic functionality
        String[] formalWords = {"therefore", "however", "consequently"};
        String[] slangWords = {"omg", "lol", "btw"};
        
        System.out.println("\nFormal words: " + String.join(", ", formalWords));
        System.out.println("Slang words: " + String.join(", ", slangWords));
        
        System.out.println("\n✅ Basic Java test successful!");
        System.out.println("You can now run the full ML pipeline with:");
        System.out.println("  ./compile_and_run.sh");
    }
}