package com.example.ml;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.*;

/**
 * Integrated Pipeline Test - Phases 1, 2, 3 Combined
 * Tests the complete data loading -> preprocessing -> splitting pipeline
 */
public class IntegratedPipelineTest {
    
    public static void main(String[] args) {
        System.out.println("=== Integrated ML Pipeline Test ===\n");
        
        try {
            // Phase 1: Load data
            System.out.println("🔸 Phase 1: Data Loading");
            CsvDataLoader.Dataset dataset = CsvDataLoader.loadCsv("sample_data.csv");
            
            System.out.println("✅ Dataset loaded successfully");
            dataset.printInfo();
            dataset.printLabelDistribution();
            System.out.println();
            
            // Extract texts and labels
            String[] texts = dataset.getColumn("text");
            String[] labels = dataset.getColumn("label");
            
            // Phase 2: Feature Engineering
            System.out.println("🔸 Phase 2: Feature Engineering");
            
            // Text preprocessing
            String[] processedTexts = TextPreprocessor.preprocessTexts(texts);
            System.out.println("✅ Text preprocessing completed");
            
            // Bag of Words
            TextPreprocessor.BagOfWords bow = new TextPreprocessor.BagOfWords();
            double[][] bowFeatures = bow.fitTransform(texts);
            System.out.println("✅ Bag of Words features: " + bowFeatures.length + " x " + bowFeatures[0].length);
            
            // TF-IDF
            TextPreprocessor.TfIdfVectorizer tfidf = new TextPreprocessor.TfIdfVectorizer();
            double[][] tfidfFeatures = tfidf.fitTransform(texts);
            System.out.println("✅ TF-IDF features: " + tfidfFeatures.length + " x " + tfidfFeatures[0].length);
            System.out.println();
            
            // Phase 3: Train-Test Split
            System.out.println("🔸 Phase 3: Train-Test Split");
            
            // Split with different ratios
            double[] testSizes = {0.2, 0.3};
            
            for (double testSize : testSizes) {
                System.out.println("--- Test Size: " + String.format("%.0f%%", testSize * 100) + " ---");
                
                TrainTestSplitter.SplitResult split = TrainTestSplitter.split(texts, labels, testSize, 42);
                
                System.out.println("✅ Split completed:");
                split.printInfo();
                split.printLabelDistribution();
                
                // Validate split
                boolean isValid = TrainTestSplitter.validateSplit(split);
                System.out.println("✅ Split validation: " + (isValid ? "Valid" : "Invalid"));
                
                // Show sample data
                split.printSampleData();
                System.out.println();
            }
            
            // Test feature extraction on split data
            System.out.println("🔸 Feature Extraction on Split Data");
            
            TrainTestSplitter.SplitResult split = TrainTestSplitter.split(texts, labels, 0.2, 42);
            
            // Extract features for training and test sets
            TextPreprocessor.BagOfWords trainBow = new TextPreprocessor.BagOfWords();
            double[][] trainFeatures = trainBow.fitTransform(split.trainTexts);
            double[][] testFeatures = trainBow.transform(split.testTexts);
            
            System.out.println("✅ Training features: " + trainFeatures.length + " x " + trainFeatures[0].length);
            System.out.println("✅ Test features: " + testFeatures.length + " x " + testFeatures[0].length);
            System.out.println("✅ Vocabulary size: " + trainBow.getVocabularySize());
            
            // Show feature vectors for first few examples
            System.out.println("\nSample feature vectors:");
            for (int i = 0; i < Math.min(3, trainFeatures.length); i++) {
                System.out.println("Training " + (i+1) + " (" + split.trainTexts[i] + "): " + 
                    Arrays.toString(Arrays.copyOf(trainFeatures[i], Math.min(8, trainFeatures[i].length))));
            }
            
            for (int i = 0; i < Math.min(3, testFeatures.length); i++) {
                System.out.println("Test " + (i+1) + " (" + split.testTexts[i] + "): " + 
                    Arrays.toString(Arrays.copyOf(testFeatures[i], Math.min(8, testFeatures[i].length))));
            }
            
            // Summary
            System.out.println("\n🎯 Pipeline Summary:");
            System.out.println("  ✅ Data Loading: " + dataset.getRowCount() + " samples loaded");
            System.out.println("  ✅ Feature Engineering: " + bow.getVocabularySize() + " features extracted");
            System.out.println("  ✅ Train-Test Split: " + split.trainTexts.length + " train, " + split.testTexts.length + " test");
            System.out.println("  ✅ Ready for Phase 4: Machine Learning Models!");
            
            System.out.println("\n✅ Integrated pipeline test completed successfully!");
            
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } catch (CsvException e) {
            System.err.println("CSV Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}