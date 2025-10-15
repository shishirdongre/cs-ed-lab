package com.example.ml;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.*;

/**
 * Test feature engineering with real data
 */
public class FeatureEngineeringTest {
    
    public static void main(String[] args) {
        System.out.println("=== Feature Engineering Integration Test ===\n");
        
        try {
            // Load the sample dataset
            CsvDataLoader.Dataset dataset = CsvDataLoader.loadCsv("sample_data.csv");
            
            System.out.println("✅ Loaded dataset:");
            dataset.printInfo();
            System.out.println();
            
            // Extract texts and labels
            String[] texts = dataset.getColumn("text");
            String[] labels = dataset.getColumn("label");
            
            System.out.println("Sample texts:");
            for (int i = 0; i < Math.min(5, texts.length); i++) {
                System.out.println((i+1) + ". " + texts[i] + " -> " + labels[i]);
            }
            System.out.println();
            
            // Test preprocessing
            System.out.println("=== Text Preprocessing ===");
            String[] processedTexts = TextPreprocessor.preprocessTexts(texts);
            
            System.out.println("Preprocessed sample texts:");
            for (int i = 0; i < Math.min(5, processedTexts.length); i++) {
                System.out.println((i+1) + ". " + processedTexts[i]);
            }
            System.out.println();
            
            // Test Bag of Words
            System.out.println("=== Bag of Words Feature Extraction ===");
            TextPreprocessor.BagOfWords bow = new TextPreprocessor.BagOfWords();
            double[][] bowFeatures = bow.fitTransform(texts);
            
            System.out.println("BoW Results:");
            System.out.println("  Vocabulary size: " + bow.getVocabularySize());
            System.out.println("  Feature matrix: " + bowFeatures.length + " x " + bowFeatures[0].length);
            System.out.println("  Sample vocabulary: " + bow.getVocabulary().subList(0, Math.min(10, bow.getVocabulary().size())));
            System.out.println();
            
            // Test TF-IDF
            System.out.println("=== TF-IDF Feature Extraction ===");
            TextPreprocessor.TfIdfVectorizer tfidf = new TextPreprocessor.TfIdfVectorizer();
            double[][] tfidfFeatures = tfidf.fitTransform(texts);
            
            System.out.println("TF-IDF Results:");
            System.out.println("  Vocabulary size: " + tfidf.getVocabularySize());
            System.out.println("  Feature matrix: " + tfidfFeatures.length + " x " + tfidfFeatures[0].length);
            System.out.println();
            
            // Show feature vectors for first few examples
            System.out.println("=== Sample Feature Vectors ===");
            System.out.println("BoW features for first 3 examples:");
            for (int i = 0; i < Math.min(3, bowFeatures.length); i++) {
                System.out.println("Text " + (i+1) + " (" + texts[i] + "):");
                System.out.println("  BoW: " + Arrays.toString(Arrays.copyOf(bowFeatures[i], Math.min(8, bowFeatures[i].length))));
                System.out.println("  TF-IDF: " + Arrays.toString(Arrays.copyOf(tfidfFeatures[i], Math.min(8, tfidfFeatures[i].length))));
            }
            System.out.println();
            
            // Analyze vocabulary
            System.out.println("=== Vocabulary Analysis ===");
            System.out.println("Most common words in vocabulary:");
            List<String> vocab = bow.getVocabulary();
            for (int i = 0; i < Math.min(15, vocab.size()); i++) {
                System.out.println("  " + (i+1) + ". " + vocab.get(i));
            }
            System.out.println();
            
            // Test with formal vs slang separation
            System.out.println("=== Formal vs Slang Analysis ===");
            List<String> formalWords = new ArrayList<>();
            List<String> slangWords = new ArrayList<>();
            
            for (int i = 0; i < texts.length; i++) {
                if ("formal".equals(labels[i])) {
                    formalWords.add(texts[i]);
                } else {
                    slangWords.add(texts[i]);
                }
            }
            
            System.out.println("Formal examples: " + formalWords.size());
            System.out.println("Slang examples: " + slangWords.size());
            System.out.println();
            
            System.out.println("✅ Feature engineering integration test completed successfully!");
            System.out.println("Ready for Phase 3: Train-Test Split!");
            
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