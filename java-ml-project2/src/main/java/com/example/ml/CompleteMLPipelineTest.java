package com.example.ml;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.*;

/**
 * Complete ML Pipeline Test - All Phases Integrated
 * Tests the complete pipeline: Data -> Features -> Split -> Train -> Predict
 */
public class CompleteMLPipelineTest {
    
    public static void main(String[] args) {
        System.out.println("=== Complete ML Pipeline Test ===\n");
        
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
            
            TrainTestSplitter.SplitResult split = TrainTestSplitter.split(texts, labels, 0.2, 42);
            
            System.out.println("✅ Split completed:");
            split.printInfo();
            split.printLabelDistribution();
            System.out.println();
            
            // Phase 4: Machine Learning Models
            System.out.println("🔸 Phase 4: Machine Learning Models");
            
            // Extract features for training and test sets
            TextPreprocessor.BagOfWords trainBow = new TextPreprocessor.BagOfWords();
            double[][] trainFeatures = trainBow.fitTransform(split.trainTexts);
            double[][] testFeatures = trainBow.transform(split.testTexts);
            
            System.out.println("✅ Feature extraction on split data:");
            System.out.println("  Training features: " + trainFeatures.length + " x " + trainFeatures[0].length);
            System.out.println("  Test features: " + testFeatures.length + " x " + testFeatures[0].length);
            System.out.println("  Vocabulary size: " + trainBow.getVocabularySize());
            System.out.println();
            
            // Train Naive Bayes
            System.out.println("--- Training Naive Bayes ---");
            NaiveBayesClassifier nb = new NaiveBayesClassifier();
            nb.fit(trainFeatures, split.trainLabels);
            
            System.out.println("✅ Naive Bayes trained successfully");
            nb.printModelInfo();
            System.out.println();
            
            // Train Logistic Regression
            System.out.println("--- Training Logistic Regression ---");
            LogisticRegressionClassifier lr = new LogisticRegressionClassifier();
            lr.fit(trainFeatures, split.trainLabels);
            
            System.out.println("✅ Logistic Regression trained successfully");
            lr.printModelInfo();
            System.out.println();
            
            // Make predictions
            System.out.println("🔸 Phase 5: Predictions and Evaluation");
            
            // Naive Bayes predictions
            String[] nbPredictions = nb.predict(testFeatures);
            double[][] nbProbabilities = nb.predictProba(testFeatures);
            
            System.out.println("Naive Bayes Predictions:");
            for (int i = 0; i < testFeatures.length; i++) {
                System.out.println("  " + split.testTexts[i] + " -> " + nbPredictions[i] + 
                                 " (actual: " + split.testLabels[i] + ")");
                System.out.println("    Probabilities: " + Arrays.toString(nbProbabilities[i]));
            }
            System.out.println();
            
            // Logistic Regression predictions
            String[] lrPredictions = lr.predict(testFeatures);
            double[][] lrProbabilities = lr.predictProba(testFeatures);
            
            System.out.println("Logistic Regression Predictions:");
            for (int i = 0; i < testFeatures.length; i++) {
                System.out.println("  " + split.testTexts[i] + " -> " + lrPredictions[i] + 
                                 " (actual: " + split.testLabels[i] + ")");
                System.out.println("    Probabilities: " + Arrays.toString(lrProbabilities[i]));
            }
            System.out.println();
            
            // Calculate accuracy
            int nbCorrect = 0;
            int lrCorrect = 0;
            
            for (int i = 0; i < split.testLabels.length; i++) {
                if (nbPredictions[i].equals(split.testLabels[i])) {
                    nbCorrect++;
                }
                if (lrPredictions[i].equals(split.testLabels[i])) {
                    lrCorrect++;
                }
            }
            
            double nbAccuracy = (double) nbCorrect / split.testLabels.length;
            double lrAccuracy = (double) lrCorrect / split.testLabels.length;
            
            System.out.println("🔸 Model Performance:");
            System.out.println("  Naive Bayes Accuracy: " + String.format("%.2f%%", nbAccuracy * 100) + 
                             " (" + nbCorrect + "/" + split.testLabels.length + ")");
            System.out.println("  Logistic Regression Accuracy: " + String.format("%.2f%%", lrAccuracy * 100) + 
                             " (" + lrCorrect + "/" + split.testLabels.length + ")");
            System.out.println();
            
            // Test on new samples
            System.out.println("🔸 Testing on New Samples:");
            String[] newTexts = {"therefore", "omg", "demonstrate", "lol", "analyze", "btw"};
            double[][] newFeatures = trainBow.transform(newTexts);
            
            String[] newNbPredictions = nb.predict(newFeatures);
            String[] newLrPredictions = lr.predict(newFeatures);
            
            for (int i = 0; i < newTexts.length; i++) {
                System.out.println("  " + newTexts[i] + ":");
                System.out.println("    Naive Bayes: " + newNbPredictions[i]);
                System.out.println("    Logistic Regression: " + newLrPredictions[i]);
            }
            System.out.println();
            
            // Summary
            System.out.println("🎯 Complete Pipeline Summary:");
            System.out.println("  ✅ Data Loading: " + dataset.getRowCount() + " samples loaded");
            System.out.println("  ✅ Feature Engineering: " + trainBow.getVocabularySize() + " features extracted");
            System.out.println("  ✅ Train-Test Split: " + split.trainTexts.length + " train, " + split.testTexts.length + " test");
            System.out.println("  ✅ Naive Bayes: " + String.format("%.1f%%", nbAccuracy * 100) + " accuracy");
            System.out.println("  ✅ Logistic Regression: " + String.format("%.1f%%", lrAccuracy * 100) + " accuracy");
            System.out.println("  ✅ Ready for Phase 5: Model Evaluation!");
            
            System.out.println("\n✅ Complete ML pipeline test completed successfully!");
            
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