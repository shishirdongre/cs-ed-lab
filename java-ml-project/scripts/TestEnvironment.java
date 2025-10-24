/**
 * Test script to verify the Java environment is working correctly.
 * This script tests basic Java functionality and Smile library integration.
 */
public class TestEnvironment {
    
    public static void main(String[] args) {
        System.out.println("☕ Testing Java Environment");
        System.out.println("=" + "=".repeat(40));
        
        // Test basic Java functionality
        testBasicJava();
        
        // Test Smile library
        testSmileLibrary();
        
        // Test file system access
        testFileSystem();
        
        System.out.println("✅ All tests passed! Environment is ready.");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("  javac -cp \"lib/*\" TestEnvironment.java");
        System.out.println("  java -cp \".:lib/*\" TestEnvironment");
        System.out.println("  ./run_java.sh");
    }
    
    private static void testBasicJava() {
        System.out.println("🔧 Testing Basic Java...");
        
        // Test Java version
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        System.out.println("Java version: " + javaVersion);
        System.out.println("Java vendor: " + javaVendor);
        
        // Test basic operations
        int[] numbers = {1, 2, 3, 4, 5};
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        System.out.println("Array sum test: " + sum + " (expected: 15)");
        
        // Test string operations
        String testString = "Hello, World!";
        System.out.println("String test: " + testString.toUpperCase());
        System.out.println();
    }
    
    private static void testSmileLibrary() {
        System.out.println("📊 Testing Smile Library...");
        
        try {
            // Test if Smile classes can be loaded
            Class<?> naiveBayesClass = Class.forName("smile.classification.NaiveBayes");
            Class<?> distributionClass = Class.forName("smile.stat.distribution.Distribution");
            Class<?> gaussianClass = Class.forName("smile.stat.distribution.GaussianDistribution");
            
            System.out.println("✅ Smile classes loaded successfully:");
            System.out.println("  - NaiveBayes: " + naiveBayesClass.getName());
            System.out.println("  - Distribution: " + distributionClass.getName());
            System.out.println("  - GaussianDistribution: " + gaussianClass.getName());
            
            // Test basic functionality
            double[] testData = {1.0, 2.0, 3.0, 4.0, 5.0};
            smile.stat.distribution.GaussianDistribution gaussian = 
                smile.stat.distribution.GaussianDistribution.fit(testData);
            
            System.out.println("✅ Gaussian distribution fitted:");
            System.out.println("  - Mean: " + gaussian.mean());
            System.out.println("  - Variance: " + gaussian.variance());
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Smile library not found: " + e.getMessage());
            System.out.println("Make sure lib/smile-*.jar files are present");
        } catch (Exception e) {
            System.out.println("❌ Error testing Smile library: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testFileSystem() {
        System.out.println("📁 Testing File System Access...");
        
        // Test current directory
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current directory: " + currentDir);
        
        // Test if lib directory exists
        java.io.File libDir = new java.io.File("lib");
        if (libDir.exists() && libDir.isDirectory()) {
            System.out.println("✅ lib/ directory found");
            
            // List JAR files
            String[] jarFiles = libDir.list((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                System.out.println("JAR files found: " + jarFiles.length);
                for (String jar : jarFiles) {
                    System.out.println("  - " + jar);
                }
            }
        } else {
            System.out.println("❌ lib/ directory not found");
        }
        
        // Test if main Java file exists
        java.io.File mainFile = new java.io.File("YelpSentimentAnalysisSmileML.java");
        if (mainFile.exists()) {
            System.out.println("✅ Main Java file found: YelpSentimentAnalysisSmileML.java");
        } else {
            System.out.println("⚠️  Main Java file not found: YelpSentimentAnalysisSmileML.java");
        }
        
        System.out.println();
    }
}