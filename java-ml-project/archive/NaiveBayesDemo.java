// BlueJ version - Naive Bayes Demo for ML Project

import java.util.*;
import com.example.ml.TextPreprocessor;
import com.example.ml.TrainTestSplit;
import com.example.ml.NaiveBayesClassifier;
import com.example.ml.ModelEvaluator;

/**
 * Naive Bayes Demo - Focused demonstration of Naive Bayes classifier
 * Matches the Python notebook implementation for formal/slang classification
 */
public class NaiveBayesDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Naive Bayes Classifier Demo ===\n");
        
        try {
            // Step 1: Create sample data (matching Python notebook)
            System.out.println("1. Creating sample dataset...");
            List<String[]> sampleData = createSampleData();
            System.out.println("   Dataset created with " + (sampleData.size() - 1) + " examples");
            
            // Count formal vs slang
            long formalCount = sampleData.stream().skip(1).filter(row -> "formal".equals(row[1])).count();
            long slangCount = sampleData.stream().skip(1).filter(row -> "slang".equals(row[1])).count();
            System.out.println("   Formal examples: " + formalCount);
            System.out.println("   Slang examples: " + slangCount);
            
            // Step 2: Extract texts and labels
            System.out.println("\n2. Preparing data...");
            String[] texts = new String[sampleData.size() - 1];
            String[] labels = new String[sampleData.size() - 1];
            
            for (int i = 1; i < sampleData.size(); i++) {
                texts[i-1] = sampleData.get(i)[0];
                labels[i-1] = sampleData.get(i)[1];
            }
            
            System.out.println("   Texts: " + Arrays.toString(texts));
            System.out.println("   Labels: " + Arrays.toString(labels));
            
            // Step 3: Text preprocessing and feature extraction
            System.out.println("\n3. Text preprocessing and feature extraction...");
            TextPreprocessor.BagOfWords bagOfWords = new TextPreprocessor.BagOfWords();
            double[][] features = bagOfWords.fitTransform(texts);
            
            System.out.println("   Vocabulary size: " + bagOfWords.getVocabularySize());
            System.out.println("   Feature matrix shape: " + features.length + " x " + features[0].length);
            
            // Show feature matrix for first few samples
            System.out.println("\n   Feature matrix (first 5 samples):");
            for (int i = 0; i < Math.min(5, features.length); i++) {
                System.out.println("   " + texts[i] + " -> " + Arrays.toString(features[i]));
            }
            
            // Step 4: Train-test split (matching Python: 80/20 split)
            System.out.println("\n4. Train-test split...");
            TrainTestSplit.SplitResult split = TrainTestSplit.split(texts, labels, 0.2, 42);
            
            System.out.println("   Training set size: " + split.trainTexts.length);
            System.out.println("   Test set size: " + split.testTexts.length);
            System.out.println("   Test ratio: " + String.format("%.1f%%", (double)split.testTexts.length / texts.length * 100));
            
            // Show train/test split
            System.out.println("\n   Training texts: " + Arrays.toString(split.trainTexts));
            System.out.println("   Training labels: " + Arrays.toString(split.trainLabels));
            System.out.println("   Test texts: " + Arrays.toString(split.testTexts));
            System.out.println("   Test labels: " + Arrays.toString(split.testLabels));
            
            // Step 5: Train Naive Bayes model
            System.out.println("\n5. Training Naive Bayes model...");
            NaiveBayesClassifier nbModel = NaiveBayesClassifier.trainNaiveBayes(split.trainTexts, split.trainLabels, bagOfWords);
            
            System.out.println("   ✅ Naive Bayes model trained successfully!");
            nbModel.printModelInfo();
            
            // Step 6: Make predictions
            System.out.println("\n6. Making predictions...");
            Set<String> uniqueLabels = new HashSet<>(Arrays.asList(split.trainLabels));
            String[] labelNames = uniqueLabels.toArray(new String[0]);
            
            String[] predictions = NaiveBayesClassifier.predict(nbModel, split.testTexts, bagOfWords, labelNames);
            
            System.out.println("   Test predictions:");
            for (int i = 0; i < split.testTexts.length; i++) {
                System.out.println("   '" + split.testTexts[i] + "' -> " + predictions[i] + " (actual: " + split.testLabels[i] + ")");
            }
            
            // Step 7: Model evaluation
            System.out.println("\n7. Model evaluation...");
            double accuracy = ModelEvaluator.calculateAccuracy(split.testLabels, predictions);
            System.out.println("   Accuracy: " + String.format("%.3f", accuracy) + " (" + String.format("%.1f%%", accuracy * 100) + ")");
            
            // Confusion matrix
            ModelEvaluator.printConfusionMatrix(split.testLabels, predictions, labelNames);
            
            // Classification report
            ModelEvaluator.printClassificationReport(split.testLabels, predictions, labelNames);
            
            // Step 8: Test on new examples (matching Python notebook)
            System.out.println("\n8. Testing on new examples...");
            String[] newExamples = {"therefore", "pls", "regarding", "gonna", "idk", "with regard to"};
            
            System.out.println("   New examples: " + Arrays.toString(newExamples));
            String[] newPredictions = NaiveBayesClassifier.predict(nbModel, newExamples, bagOfWords, labelNames);
            
            System.out.println("   Naive Bayes predictions: " + Arrays.toString(newPredictions));
            
            // Step 9: Show model details
            System.out.println("\n9. Model details...");
            System.out.println("   Model type: Multinomial Naive Bayes");
            System.out.println("   Smoothing parameter (alpha): 1.0");
            System.out.println("   Features used: Bag of Words (unigrams)");
            System.out.println("   Vocabulary size: " + bagOfWords.getVocabularySize());
            
            System.out.println("\n✅ Naive Bayes demo completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error in Naive Bayes demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create sample data matching the Python notebook
     */
    public static List<String[]> createSampleData() {
        List<String[]> data = new ArrayList<>();
        
        // Add header
        data.add(new String[]{"text", "label"});
        
        // Formal examples (matching Python notebook)
        String[] formalTexts = {
            "therefore", "however", "consequently", "furthermore", "moreover",
            "nevertheless", "nonetheless", "accordingly", "subsequently", "previously",
            "approximately", "specifically", "particularly", "especially",
            "demonstrate", "illustrate", "exemplify", "substantiate", "validate",
            "analyze", "evaluate", "investigate", "examine", "assess"
        };
        
        // Slang examples (matching Python notebook)
        String[] slangTexts = {
            "omg", "lol", "btw", "fyi", "tbh", "imo", "imho", "nvm", "idk", "ikr",
            "yolo", "fomo", "lit", "sick", "dope", "fire", "goals", "mood", "vibe", "flex",
            "salty", "basic", "extra", "lowkey", "highkey"
        };
        
        // Add formal data
        for (String text : formalTexts) {
            data.add(new String[]{text, "formal"});
        }
        
        // Add slang data
        for (String text : slangTexts) {
            data.add(new String[]{text, "slang"});
        }
        
        return data;
    }
}