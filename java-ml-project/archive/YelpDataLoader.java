// BlueJ version - Yelp Dataset Loader

import java.util.*;
import java.io.*;
import com.opencsv.CSVReader;

/**
 * Data loader specifically for Yelp review dataset
 */
public class YelpDataLoader {
    
    /**
     * Load Yelp dataset from CSV file
     */
    public static List<String[]> loadYelpDataset(String filename) {
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            data = reader.readAll();
        } catch (IOException e) {
            System.err.println("Error loading Yelp dataset: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }
    
    /**
     * Load training dataset
     */
    public static List<String[]> loadTrainingData() {
        return loadYelpDataset("yelp_train.csv");
    }
    
    /**
     * Load test dataset
     */
    public static List<String[]> loadTestData() {
        return loadYelpDataset("yelp_test.csv");
    }
    
    /**
     * Load combined dataset
     */
    public static List<String[]> loadCombinedData() {
        return loadYelpDataset("yelp_reviews.csv");
    }
    
    /**
     * Get dataset statistics
     */
    public static void printDatasetStats(List<String[]> data) {
        if (data.size() <= 1) {
            System.out.println("Dataset is empty or has no data rows");
            return;
        }
        
        // Skip header row
        List<String[]> dataRows = data.subList(1, data.size());
        
        // Count positive and negative reviews
        long positiveCount = dataRows.stream().filter(row -> "positive".equals(row[1])).count();
        long negativeCount = dataRows.stream().filter(row -> "negative".equals(row[1])).count();
        
        System.out.println("Dataset Statistics:");
        System.out.println("  Total reviews: " + dataRows.size());
        System.out.println("  Positive reviews: " + positiveCount + " (" + String.format("%.1f%%", (double)positiveCount / dataRows.size() * 100) + ")");
        System.out.println("  Negative reviews: " + negativeCount + " (" + String.format("%.1f%%", (double)negativeCount / dataRows.size() * 100) + ")");
        
        // Show sample reviews
        System.out.println("\nSample reviews:");
        for (int i = 0; i < Math.min(3, dataRows.size()); i++) {
            String review = dataRows.get(i)[0];
            String sentiment = dataRows.get(i)[1];
            String shortReview = review.length() > 80 ? review.substring(0, 80) + "..." : review;
            System.out.println("  " + (i+1) + ". (" + sentiment + ") " + shortReview);
        }
    }
    
    /**
     * Test the data loader
     */
    public static void main(String[] args) {
        System.out.println("=== Yelp Data Loader Test ===\n");
        
        // Test loading combined dataset
        System.out.println("Loading combined dataset...");
        List<String[]> combinedData = loadCombinedData();
        printDatasetStats(combinedData);
        
        System.out.println("\n" + "=".repeat(50));
        
        // Test loading training dataset
        System.out.println("\nLoading training dataset...");
        List<String[]> trainData = loadTrainingData();
        printDatasetStats(trainData);
        
        System.out.println("\n" + "=".repeat(50));
        
        // Test loading test dataset
        System.out.println("\nLoading test dataset...");
        List<String[]> testData = loadTestData();
        printDatasetStats(testData);
        
        System.out.println("\n✅ Yelp data loader test completed!");
    }
}