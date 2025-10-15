// BlueJ version - Yelp Review Sentiment Analysis using Naive Bayes

import java.util.*;
import com.example.ml.TextPreprocessor;
import com.example.ml.TrainTestSplit;
import com.example.ml.NaiveBayesClassifier;
import com.example.ml.ModelEvaluator;
import com.example.ml.DataLoader;

/**
 * Yelp Review Sentiment Analysis using Naive Bayes Classifier
 * Uses real Yelp dataset for restaurant review sentiment classification
 */
public class YelpSentimentAnalysis {
    
    public static void main(String[] args) {
        System.out.println("=== Yelp Review Sentiment Analysis ===\n");
        
        try {
            // Load and prepare data
            DataPreparationResult dataResult = loadAndPrepareData();
            
            // Train the model
            ModelTrainingResult modelResult = trainModel(dataResult);
            
            // Evaluate the model
            evaluateModel(modelResult);
            
            // Test on sample reviews
            testSampleReviews(modelResult);
            
            // Show model insights
            showModelInsights(modelResult);
            
            // Performance summary
            showPerformanceSummary(modelResult);
            
            System.out.println("\n✅ Yelp sentiment analysis completed successfully!");
            
            // Student exercises
            runStudentExercises(modelResult);
            
        } catch (Exception e) {
            System.err.println("Error in Yelp sentiment analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load and prepare the Yelp dataset
     */
    private static DataPreparationResult loadAndPrepareData() {
        System.out.println("1. Loading Yelp dataset...");
        List<String[]> yelpData = DataLoader.loadCSV("simple_yelp_reviews.csv");
        
        System.out.println("   Dataset loaded successfully!");
        System.out.println("   Total reviews: " + (yelpData.size() - 1));
        
        // Count positive vs negative reviews
        long positiveCount = yelpData.stream().skip(1).filter(row -> "positive".equals(row[1])).count();
        long negativeCount = yelpData.stream().skip(1).filter(row -> "negative".equals(row[1])).count();
        System.out.println("   Positive reviews: " + positiveCount);
        System.out.println("   Negative reviews: " + negativeCount);
        
        // Extract texts and labels
        System.out.println("\n2. Preparing data...");
        String[] texts = new String[yelpData.size() - 1];
        String[] labels = new String[yelpData.size() - 1];
        
        for (int i = 1; i < yelpData.size(); i++) {
            texts[i-1] = yelpData.get(i)[0];
            labels[i-1] = yelpData.get(i)[1];
        }
        
        // Show sample reviews
        System.out.println("\n   Sample reviews:");
        for (int i = 0; i < 3; i++) {
            String review = texts[i].length() > 100 ? texts[i].substring(0, 100) + "..." : texts[i];
            System.out.println("   " + (i+1) + ". (" + labels[i] + ") " + review);
        }
        
        // Text preprocessing and feature extraction
        System.out.println("\n3. Text preprocessing and feature extraction...");
        TextPreprocessor.BagOfWords bagOfWords = new TextPreprocessor.BagOfWords();
        double[][] features = bagOfWords.fitTransform(texts);
        
        System.out.println("   Text preprocessing completed!");
        System.out.println("   Vocabulary size: " + bagOfWords.getVocabularySize());
        System.out.println("   Feature matrix shape: " + features.length + " x " + features[0].length);
        
        // Show most common words
        System.out.println("\n   Most common words in vocabulary:");
        List<String> vocab = bagOfWords.getVocabulary();
        for (int i = 0; i < Math.min(10, vocab.size()); i++) {
            System.out.println("   " + (i+1) + ". " + vocab.get(i));
        }
        
        return new DataPreparationResult(texts, labels, bagOfWords);
    }
    
    /**
     * Train the Naive Bayes model
     */
    private static ModelTrainingResult trainModel(DataPreparationResult dataResult) {
        // Train-test split
        System.out.println("\n4. Train-test split...");
        TrainTestSplit.SplitResult split = TrainTestSplit.split(dataResult.texts, dataResult.labels, 0.2, 42);
        
        System.out.println("   Train-test split completed!");
        System.out.println("   Training set size: " + split.trainTexts.length);
        System.out.println("   Test set size: " + split.testTexts.length);
        System.out.println("   Test ratio: " + String.format("%.1f%%", (double)split.testTexts.length / dataResult.texts.length * 100));
        
        // Train Naive Bayes model
        System.out.println("\n5. Training Naive Bayes model...");
        NaiveBayesClassifier nbModel = NaiveBayesClassifier.trainNaiveBayes(split.trainTexts, split.trainLabels, dataResult.bagOfWords);
        
        System.out.println("   ✅ Naive Bayes model trained successfully!");
        nbModel.printModelInfo();
        
        // Make predictions
        System.out.println("\n6. Making predictions...");
        Set<String> uniqueLabels = new HashSet<>(Arrays.asList(split.trainLabels));
        String[] labelNames = uniqueLabels.toArray(new String[0]);
        
        String[] predictions = NaiveBayesClassifier.predict(nbModel, split.testTexts, dataResult.bagOfWords, labelNames);
        
        System.out.println("   Test predictions completed!");
        System.out.println("   Predictions made for " + predictions.length + " test samples");
        
        return new ModelTrainingResult(nbModel, dataResult.bagOfWords, split, predictions, labelNames);
    }
    
    /**
     * Evaluate the model performance
     */
    private static void evaluateModel(ModelTrainingResult modelResult) {
        System.out.println("\n7. Model evaluation...");
        double accuracy = ModelEvaluator.calculateAccuracy(modelResult.split.testLabels, modelResult.predictions);
        System.out.println("   Overall Accuracy: " + String.format("%.3f", accuracy) + " (" + String.format("%.1f%%", accuracy * 100) + ")");
        
        // Confusion matrix
        ModelEvaluator.printConfusionMatrix(modelResult.split.testLabels, modelResult.predictions, modelResult.labelNames);
        
        // Classification report
        ModelEvaluator.printClassificationReport(modelResult.split.testLabels, modelResult.predictions, modelResult.labelNames);
    }
    
    /**
     * Test the model on sample reviews
     */
    private static void testSampleReviews(ModelTrainingResult modelResult) {
        System.out.println("\n8. Testing on sample reviews...");
        String[] sampleReviews = {
            "Great food, excellent service!",
            "Terrible food, bad service",
            "Amazing pizza, friendly staff",
            "Cold food, rude waiter",
            "Love this place, will come back",
            "Hate it, never coming back",
            "Outstanding quality, great atmosphere",
            "Waste of money, terrible experience"
        };
        
        System.out.println("   Sample review predictions:");
        String[] samplePredictions = NaiveBayesClassifier.predict(modelResult.nbModel, sampleReviews, modelResult.bagOfWords, modelResult.labelNames);
        
        for (int i = 0; i < sampleReviews.length; i++) {
            String review = sampleReviews[i].length() > 50 ? sampleReviews[i].substring(0, 50) + "..." : sampleReviews[i];
            System.out.println("   '" + review + "' -> " + samplePredictions[i]);
        }
    }
    
    /**
     * Show model insights and configuration
     */
    private static void showModelInsights(ModelTrainingResult modelResult) {
        System.out.println("\n9. Model insights...");
        System.out.println("   Model type: Multinomial Naive Bayes");
        System.out.println("   Smoothing parameter (alpha): 1.0");
        System.out.println("   Features used: Bag of Words (unigrams)");
        System.out.println("   Vocabulary size: " + modelResult.bagOfWords.getVocabularySize());
        System.out.println("   Training samples: " + modelResult.split.trainTexts.length);
        System.out.println("   Test samples: " + modelResult.split.testTexts.length);
    }
    
    /**
     * Show detailed performance summary
     */
    private static void showPerformanceSummary(ModelTrainingResult modelResult) {
        System.out.println("\n10. Performance summary...");
        double accuracy = ModelEvaluator.calculateAccuracy(modelResult.split.testLabels, modelResult.predictions);
        double balancedAccuracy = ModelEvaluator.calculateBalancedAccuracy(modelResult.split.testLabels, modelResult.predictions, modelResult.labelNames);
        System.out.println("   Overall Accuracy: " + String.format("%.1f%%", accuracy * 100));
        System.out.println("   Balanced Accuracy: " + String.format("%.1f%%", balancedAccuracy * 100));
        
        // Count correct predictions by class
        int correctPositive = 0, correctNegative = 0;
        int totalPositive = 0, totalNegative = 0;
        
        for (int i = 0; i < modelResult.split.testLabels.length; i++) {
            if ("positive".equals(modelResult.split.testLabels[i])) {
                totalPositive++;
                if (modelResult.split.testLabels[i].equals(modelResult.predictions[i])) {
                    correctPositive++;
                }
            } else {
                totalNegative++;
                if (modelResult.split.testLabels[i].equals(modelResult.predictions[i])) {
                    correctNegative++;
                }
            }
        }
        
        System.out.println("   Positive class accuracy: " + String.format("%.1f%%", (double)correctPositive / totalPositive * 100));
        System.out.println("   Negative class accuracy: " + String.format("%.1f%%", (double)correctNegative / totalNegative * 100));
    }
    
    /**
     * Run all student exercises
     */
    private static void runStudentExercises(ModelTrainingResult modelResult) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎓 STUDENT EXERCISE: Add Your Own Examples!");
        System.out.println("=".repeat(60));
        
        runBasicStudentExercise(modelResult);
        runAdvancedStudentExercise(modelResult);
        runChallengeStudentExercise(modelResult);
        showReflectionQuestions();
        
        System.out.println("\n🎓 Student exercise completed! Great job experimenting with the model!");
    }
    
    /**
     * Basic student exercise - Add your own examples
     */
    private static void runBasicStudentExercise(ModelTrainingResult modelResult) {
        System.out.println("\n11. Student Exercise - Test Your Own Examples!");
        System.out.println("   Instructions:");
        System.out.println("   1. Add your own restaurant review examples below");
        System.out.println("   2. Try to write both positive and negative reviews");
        System.out.println("   3. See if the model correctly predicts the sentiment");
        System.out.println("   4. Experiment with different types of reviews");
        
        // TODO: Students should fill in their own examples here
        String[] studentExamples = {
            // TODO: Add your positive review examples here
            "This place has amazing food and great service!",
            "Love the atmosphere and friendly staff.",
            "Best pizza I've ever had, definitely coming back!",
            
            // TODO: Add your negative review examples here
            "Terrible experience, food was cold and service was slow.",
            "Overpriced and not worth the money.",
            "Rude staff and dirty restaurant, never coming back."
        };
        
        System.out.println("\n   Your examples:");
        for (int i = 0; i < studentExamples.length; i++) {
            System.out.println("   " + (i+1) + ". " + studentExamples[i]);
        }
        
        // Test student examples
        System.out.println("\n   Testing your examples...");
        String[] studentPredictions = NaiveBayesClassifier.predict(modelResult.nbModel, studentExamples, modelResult.bagOfWords, modelResult.labelNames);
        
        System.out.println("\n   Results:");
        for (int i = 0; i < studentExamples.length; i++) {
            String review = studentExamples[i].length() > 50 ? studentExamples[i].substring(0, 50) + "..." : studentExamples[i];
            System.out.println("   '" + review + "' -> " + studentPredictions[i]);
        }
    }
    
    /**
     * Advanced student exercise - Analyze word importance
     */
    private static void runAdvancedStudentExercise(ModelTrainingResult modelResult) {
        System.out.println("\n12. Advanced Exercise - Analyze Word Importance!");
        System.out.println("   Instructions:");
        System.out.println("   1. Look at the vocabulary below");
        System.out.println("   2. Try to identify which words are most important for classification");
        System.out.println("   3. Add examples using these important words");
        System.out.println("   4. See how changing words affects predictions");
        
        // Show vocabulary for analysis
        System.out.println("\n   Vocabulary (first 50 words):");
        List<String> studentVocab = modelResult.bagOfWords.getVocabulary();
        for (int i = 0; i < Math.min(50, studentVocab.size()); i++) {
            System.out.print("   " + studentVocab.get(i));
            if ((i + 1) % 10 == 0) System.out.println(); // New line every 10 words
            else System.out.print(", ");
        }
        if (studentVocab.size() > 50) System.out.println("\n   ... and " + (studentVocab.size() - 50) + " more words");
    }
    
    /**
     * Challenge student exercise - Test edge cases
     */
    private static void runChallengeStudentExercise(ModelTrainingResult modelResult) {
        System.out.println("\n13. Challenge Exercise - Test Edge Cases!");
        System.out.println("   Instructions:");
        System.out.println("   1. Try reviews with mixed sentiment (positive and negative)");
        System.out.println("   2. Test very short reviews (1-2 words)");
        System.out.println("   3. Try reviews with sarcasm or irony");
        System.out.println("   4. Test reviews with typos or informal language");
        
        // TODO: Students should add edge case examples here
        String[] edgeCaseExamples = {
            // TODO: Add mixed sentiment examples
            "Great food but terrible service",
            "Nice atmosphere but overpriced",
            
            // TODO: Add very short examples
            "Amazing!",
            "Terrible",
            "OK",
            
            // TODO: Add sarcastic examples
            "Oh great, another hour wait for cold food",
            "Sure, if you like paying $20 for a sandwich",
            
            // TODO: Add examples with typos
            "Fud was gud but servis was bad",
            "Luv this place, def coming bak"
        };
        
        System.out.println("\n   Edge case examples:");
        for (int i = 0; i < edgeCaseExamples.length; i++) {
            System.out.println("   " + (i+1) + ". " + edgeCaseExamples[i]);
        }
        
        // Test edge cases
        System.out.println("\n   Testing edge cases...");
        String[] edgeCasePredictions = NaiveBayesClassifier.predict(modelResult.nbModel, edgeCaseExamples, modelResult.bagOfWords, modelResult.labelNames);
        
        System.out.println("\n   Edge case results:");
        for (int i = 0; i < edgeCaseExamples.length; i++) {
            String review = edgeCaseExamples[i].length() > 40 ? edgeCaseExamples[i].substring(0, 40) + "..." : edgeCaseExamples[i];
            System.out.println("   '" + review + "' -> " + edgeCasePredictions[i]);
        }
    }
    
    /**
     * Show reflection questions for students
     */
    private static void showReflectionQuestions() {
        System.out.println("\n14. Reflection Questions!");
        System.out.println("   Think about these questions:");
        System.out.println("   1. Which examples did the model get right/wrong?");
        System.out.println("   2. What patterns do you notice in the predictions?");
        System.out.println("   3. How does the model handle sarcasm or mixed sentiment?");
        System.out.println("   4. What words seem most important for classification?");
        System.out.println("   5. How could you improve the model's performance?");
    }
    
    /**
     * Data preparation result container
     */
    private static class DataPreparationResult {
        final String[] texts;
        final String[] labels;
        final TextPreprocessor.BagOfWords bagOfWords;
        
        DataPreparationResult(String[] texts, String[] labels, TextPreprocessor.BagOfWords bagOfWords) {
            this.texts = texts;
            this.labels = labels;
            this.bagOfWords = bagOfWords;
        }
    }
    
    /**
     * Model training result container
     */
    private static class ModelTrainingResult {
        final NaiveBayesClassifier nbModel;
        final TextPreprocessor.BagOfWords bagOfWords;
        final TrainTestSplit.SplitResult split;
        final String[] predictions;
        final String[] labelNames;
        
        ModelTrainingResult(NaiveBayesClassifier nbModel, TextPreprocessor.BagOfWords bagOfWords, 
                          TrainTestSplit.SplitResult split, String[] predictions, String[] labelNames) {
            this.nbModel = nbModel;
            this.bagOfWords = bagOfWords;
            this.split = split;
            this.predictions = predictions;
            this.labelNames = labelNames;
        }
    }
}