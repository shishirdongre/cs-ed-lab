package com.example.ml;

import smile.classification.LogisticRegression;
import smile.classification.NaiveBayes;
import smile.data.DataFrame;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import java.util.*;

/**
 * Simplified text classification using basic features
 */
public class SimpleTextClassifier {
    
    public static class TextFeatures {
        public final double[] features;
        public final String label;
        
        public TextFeatures(double[] features, String label) {
            this.features = features;
            this.label = label;
        }
    }
    
    public static TextFeatures extractFeatures(String text) {
        // Simple feature extraction: word length, character count, etc.
        double[] features = new double[5];
        
        // Feature 1: Text length
        features[0] = text.length();
        
        // Feature 2: Number of vowels
        features[1] = text.toLowerCase().replaceAll("[^aeiou]", "").length();
        
        // Feature 3: Number of consonants
        features[2] = text.toLowerCase().replaceAll("[^bcdfghjklmnpqrstvwxyz]", "").length();
        
        // Feature 4: Number of uppercase letters
        features[3] = text.replaceAll("[^A-Z]", "").length();
        
        // Feature 5: Number of special characters
        features[4] = text.replaceAll("[a-zA-Z0-9\\s]", "").length();
        
        return new TextFeatures(features, null);
    }
    
    public static String classifyFormalSlang(String text) {
        // Simple rule-based classification
        String lowerText = text.toLowerCase();
        
        // Formal indicators
        if (lowerText.length() > 8 || 
            lowerText.contains("therefore") || 
            lowerText.contains("however") ||
            lowerText.contains("consequently") ||
            lowerText.contains("furthermore") ||
            lowerText.contains("demonstrate") ||
            lowerText.contains("illustrate")) {
            return "formal";
        }
        
        // Slang indicators
        if (lowerText.length() <= 4 ||
            lowerText.contains("omg") ||
            lowerText.contains("lol") ||
            lowerText.contains("btw") ||
            lowerText.contains("fyi") ||
            lowerText.contains("yolo") ||
            lowerText.contains("lit")) {
            return "slang";
        }
        
        // Default to formal for longer words
        return text.length() > 6 ? "formal" : "slang";
    }
    
    public static void main(String[] args) {
        System.out.println("=== Simple Text Classification Demo ===\n");
        
        // Test examples
        String[] testTexts = {
            "therefore", "omg", "consequently", "lol", "demonstrate", "yolo",
            "however", "btw", "furthermore", "lit", "illustrate", "fyi"
        };
        
        System.out.println("Text Classification Results:");
        System.out.println("Text\t\t\tClassification");
        System.out.println("-".repeat(40));
        
        for (String text : testTexts) {
            String classification = classifyFormalSlang(text);
            TextFeatures features = extractFeatures(text);
            
            System.out.printf("%-15s\t%s\n", text, classification);
        }
        
        System.out.println("\nFeature Extraction Example:");
        TextFeatures features = extractFeatures("therefore");
        System.out.println("Text: 'therefore'");
        System.out.println("Features: " + Arrays.toString(features.features));
        System.out.println("  - Length: " + features.features[0]);
        System.out.println("  - Vowels: " + features.features[1]);
        System.out.println("  - Consonants: " + features.features[2]);
        System.out.println("  - Uppercase: " + features.features[3]);
        System.out.println("  - Special chars: " + features.features[4]);
        
        System.out.println("\n✅ Simple text classification completed!");
    }
}