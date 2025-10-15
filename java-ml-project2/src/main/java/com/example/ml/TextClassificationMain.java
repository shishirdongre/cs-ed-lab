package com.example.ml;

import com.example.ml.LogisticRegressionClassifier;
import com.example.ml.NaiveBayesClassifier;
import smile.data.DataFrame;
import com.example.ml.TextPreprocessor.BagOfWords;
import com.example.ml.TextPreprocessor.TfIdfVectorizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main class to run the complete text classification pipeline
 */
public class TextClassificationMain {
    
    public static void main(String[] args) {
        System.out.println("=== Java Text Classification ML Pipeline ===\n");
        
        try {
            // Step 1: Create sample data
            System.out.println("1. Creating sample dataset...");
            List<String[]> sampleData = TextClassificationDemo.createSampleData();
            TextClassificationDemo.saveToCSV(sampleData, "formal_slang.csv");
            
            System.out.println("   Sample dataset created with " + (sampleData.size() - 1) + " examples");
            System.out.println("   Formal examples: " + sampleData.stream().skip(1).filter(row -> "formal".equals(row[1])).count());
            System.out.println("   Slang examples: " + sampleData.stream().skip(1).filter(row -> "slang".equals(row[1])).count());
            
            // Step 2: Load data
            System.out.println("\n2. Loading dataset...");
            List<String[]> rawData = DataLoader.loadCSV("formal_slang.csv");
            DataFrame df = DataLoader.createDataFrame(rawData);
            
            System.out.println("   Dataset loaded successfully!");
            System.out.println("   Shape: " + df.nrow() + " rows, " + df.ncol() + " columns");
            System.out.println("\n   First 5 rows:");
            // Print first 5 rows manually since print() method is not available
            for (int i = 0; i < Math.min(5, df.nrow()); i++) {
                System.out.println("   " + df.get(i, 0) + " | " + df.get(i, 1));
            }
            
            // Step 3: Text preprocessing
            System.out.println("\n3. Text preprocessing...");
            String[] texts = df.column("text").toStringArray();
            String[] labels = df.column("label").toStringArray();
            
            BagOfWords bagOfWords = new BagOfWords();
            bagOfWords.fit(texts);
            
            TfIdfVectorizer tfidf = new TfIdfVectorizer();
            tfidf.fit(texts);
            
            System.out.println("   Text preprocessing completed!");
            System.out.println("   Bag of Words vocabulary size: " + bagOfWords.getVocabularySize());
            System.out.println("   TF-IDF vocabulary size: " + tfidf.getVocabularySize());
            
            // Step 4: Train-test split
            System.out.println("\n4. Train-test split...");
            TrainTestSplit.SplitResult split = TrainTestSplit.split(texts, labels, 0.2, 42);
            
            System.out.println("   Train-test split completed!");
            System.out.println("   Training set size: " + split.trainTexts.length);
            System.out.println("   Test set size: " + split.testTexts.length);
            System.out.println("   Test ratio: " + String.format("%.1f%%", (double)split.testTexts.length / texts.length * 100));
            
            // Step 5: Train Naive Bayes
            System.out.println("\n5. Training Naive Bayes model...");
            NaiveBayesClassifier nbModel = NaiveBayesClassifier.trainNaiveBayes(split.trainTexts, split.trainLabels, bagOfWords);
            
            Set<String> uniqueLabels = Arrays.stream(split.trainLabels).collect(Collectors.toSet());
            String[] labelNames = uniqueLabels.toArray(new String[0]);
            
            String[] nbPredictions = NaiveBayesClassifier.predict(nbModel, split.testTexts, bagOfWords, labelNames);
            
            System.out.println("   Naive Bayes model trained successfully!");
            System.out.println("   Predictions made for " + nbPredictions.length + " test samples");
            
            // Step 6: Train Logistic Regression
            System.out.println("\n6. Training Logistic Regression model...");
            LogisticRegressionClassifier lrModel = LogisticRegressionClassifier.trainLogisticRegression(split.trainTexts, split.trainLabels, bagOfWords);
            
            String[] lrPredictions = LogisticRegressionClassifier.predict(lrModel, split.testTexts, bagOfWords, labelNames);
            
            System.out.println("   Logistic Regression model trained successfully!");
            System.out.println("   Predictions made for " + lrPredictions.length + " test samples");
            
            // Step 7: Model evaluation
            System.out.println("\n7. Model evaluation...");
            
            // Evaluate Naive Bayes
            System.out.println("\n=== NAIVE BAYES EVALUATION ===");
            ModelEvaluator.printEvaluationSummary(split.testLabels, nbPredictions, labelNames);
            
            // Evaluate Logistic Regression
            System.out.println("\n=== LOGISTIC REGRESSION EVALUATION ===");
            ModelEvaluator.printEvaluationSummary(split.testLabels, lrPredictions, labelNames);
            
            // Step 8: Prediction demo
            System.out.println("\n8. Prediction demo...");
            System.out.println("\n=== PREDICTION DEMO ===");
            
            String[] testExamples = {"therefore", "omg", "consequently", "lol", "demonstrate", "yolo"};
            
            for (String example : testExamples) {
                PredictionDemo.predictAndDisplay(example, nbModel, lrModel, bagOfWords, labelNames);
            }
            
            // Summary
            System.out.println("\n=== FINAL SUMMARY ===");
            double nbAccuracy = ModelEvaluator.calculateAccuracy(split.testLabels, nbPredictions);
            double lrAccuracy = ModelEvaluator.calculateAccuracy(split.testLabels, lrPredictions);
            double nbBalancedAccuracy = ModelEvaluator.calculateBalancedAccuracy(split.testLabels, nbPredictions, labelNames);
            double lrBalancedAccuracy = ModelEvaluator.calculateBalancedAccuracy(split.testLabels, lrPredictions, labelNames);
            
            System.out.println("Model Performance Comparison:");
            System.out.printf("Naive Bayes - Accuracy: %.1f%%, Balanced Accuracy: %.1f%%\n", 
                nbAccuracy * 100, nbBalancedAccuracy * 100);
            System.out.printf("Logistic Regression - Accuracy: %.1f%%, Balanced Accuracy: %.1f%%\n", 
                lrAccuracy * 100, lrBalancedAccuracy * 100);
            
            String bestModel = (lrAccuracy > nbAccuracy) ? "Logistic Regression" : "Naive Bayes";
            System.out.println("\nBest performing model: " + bestModel);
            System.out.println("\n✅ Complete ML pipeline successfully executed!");
            
        } catch (Exception e) {
            System.err.println("Error running the pipeline: " + e.getMessage());
            e.printStackTrace();
        }
    }
}