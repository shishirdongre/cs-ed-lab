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
            // Step 1: Load Yelp dataset
            System.out.println("1. Loading Yelp dataset...");
            List<String[]> yelpData = DataLoader.loadCSV("simple_yelp_reviews.csv");
            
            System.out.println("   Dataset loaded successfully!");
            System.out.println("   Total reviews: " + (yelpData.size() - 1));
            
            // Count positive vs negative reviews
            long positiveCount = yelpData.stream().skip(1).filter(row -> "positive".equals(row[1])).count();
            long negativeCount = yelpData.stream().skip(1).filter(row -> "negative".equals(row[1])).count();
            System.out.println("   Positive reviews: " + positiveCount);
            System.out.println("   Negative reviews: " + negativeCount);
            
            // Step 2: Extract texts and labels
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
            
            // Step 3: Text preprocessing and feature extraction
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
            
            // Step 4: Train-test split
            System.out.println("\n4. Train-test split...");
            TrainTestSplit.SplitResult split = TrainTestSplit.split(texts, labels, 0.2, 42);
            
            System.out.println("   Train-test split completed!");
            System.out.println("   Training set size: " + split.trainTexts.length);
            System.out.println("   Test set size: " + split.testTexts.length);
            System.out.println("   Test ratio: " + String.format("%.1f%%", (double)split.testTexts.length / texts.length * 100));
            
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
            
            System.out.println("   Test predictions completed!");
            System.out.println("   Predictions made for " + predictions.length + " test samples");
            
            // Step 7: Model evaluation
            System.out.println("\n7. Model evaluation...");
            double accuracy = ModelEvaluator.calculateAccuracy(split.testLabels, predictions);
            System.out.println("   Overall Accuracy: " + String.format("%.3f", accuracy) + " (" + String.format("%.1f%%", accuracy * 100) + ")");
            
            // Confusion matrix
            ModelEvaluator.printConfusionMatrix(split.testLabels, predictions, labelNames);
            
            // Classification report
            ModelEvaluator.printClassificationReport(split.testLabels, predictions, labelNames);
            
            // Step 8: Test on sample reviews
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
            String[] samplePredictions = NaiveBayesClassifier.predict(nbModel, sampleReviews, bagOfWords, labelNames);
            
            for (int i = 0; i < sampleReviews.length; i++) {
                String review = sampleReviews[i].length() > 50 ? sampleReviews[i].substring(0, 50) + "..." : sampleReviews[i];
                System.out.println("   '" + review + "' -> " + samplePredictions[i]);
            }
            
            // Step 9: Show model insights
            System.out.println("\n9. Model insights...");
            System.out.println("   Model type: Multinomial Naive Bayes");
            System.out.println("   Smoothing parameter (alpha): 1.0");
            System.out.println("   Features used: Bag of Words (unigrams)");
            System.out.println("   Vocabulary size: " + bagOfWords.getVocabularySize());
            System.out.println("   Training samples: " + split.trainTexts.length);
            System.out.println("   Test samples: " + split.testTexts.length);
            
            // Step 10: Performance summary
            System.out.println("\n10. Performance summary...");
            double balancedAccuracy = ModelEvaluator.calculateBalancedAccuracy(split.testLabels, predictions, labelNames);
            System.out.println("   Overall Accuracy: " + String.format("%.1f%%", accuracy * 100));
            System.out.println("   Balanced Accuracy: " + String.format("%.1f%%", balancedAccuracy * 100));
            
            // Count correct predictions by class
            int correctPositive = 0, correctNegative = 0;
            int totalPositive = 0, totalNegative = 0;
            
            for (int i = 0; i < split.testLabels.length; i++) {
                if ("positive".equals(split.testLabels[i])) {
                    totalPositive++;
                    if (split.testLabels[i].equals(predictions[i])) {
                        correctPositive++;
                    }
                } else {
                    totalNegative++;
                    if (split.testLabels[i].equals(predictions[i])) {
                        correctNegative++;
                    }
                }
            }
            
            System.out.println("   Positive class accuracy: " + String.format("%.1f%%", (double)correctPositive / totalPositive * 100));
            System.out.println("   Negative class accuracy: " + String.format("%.1f%%", (double)correctNegative / totalNegative * 100));
            
            System.out.println("\n✅ Yelp sentiment analysis completed successfully!");
            
            // ========================================
            // STUDENT EXERCISE SECTION
            // ========================================
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🎓 STUDENT EXERCISE: Add Your Own Examples!");
            System.out.println("=".repeat(60));
            
            // Step 11: Student exercise - Add your own examples
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
            String[] studentPredictions = NaiveBayesClassifier.predict(nbModel, studentExamples, bagOfWords, labelNames);
            
            System.out.println("\n   Results:");
            for (int i = 0; i < studentExamples.length; i++) {
                String review = studentExamples[i].length() > 50 ? studentExamples[i].substring(0, 50) + "..." : studentExamples[i];
                System.out.println("   '" + review + "' -> " + studentPredictions[i]);
            }
            
            // Step 12: Advanced student exercise - Analyze word importance
            System.out.println("\n12. Advanced Exercise - Analyze Word Importance!");
            System.out.println("   Instructions:");
            System.out.println("   1. Look at the vocabulary below");
            System.out.println("   2. Try to identify which words are most important for classification");
            System.out.println("   3. Add examples using these important words");
            System.out.println("   4. See how changing words affects predictions");
            
            // Show vocabulary for analysis
            System.out.println("\n   Vocabulary (first 50 words):");
            List<String> studentVocab = bagOfWords.getVocabulary();
            for (int i = 0; i < Math.min(50, studentVocab.size()); i++) {
                System.out.print("   " + studentVocab.get(i));
                if ((i + 1) % 10 == 0) System.out.println(); // New line every 10 words
                else System.out.print(", ");
            }
            if (studentVocab.size() > 50) System.out.println("\n   ... and " + (studentVocab.size() - 50) + " more words");
            
            // Step 13: Challenge exercise - Edge cases
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
            String[] edgeCasePredictions = NaiveBayesClassifier.predict(nbModel, edgeCaseExamples, bagOfWords, labelNames);
            
            System.out.println("\n   Edge case results:");
            for (int i = 0; i < edgeCaseExamples.length; i++) {
                String review = edgeCaseExamples[i].length() > 40 ? edgeCaseExamples[i].substring(0, 40) + "..." : edgeCaseExamples[i];
                System.out.println("   '" + review + "' -> " + edgeCasePredictions[i]);
            }
            
            // Step 14: Reflection questions
            System.out.println("\n14. Reflection Questions!");
            System.out.println("   Think about these questions:");
            System.out.println("   1. Which examples did the model get right/wrong?");
            System.out.println("   2. What patterns do you notice in the predictions?");
            System.out.println("   3. How does the model handle sarcasm or mixed sentiment?");
            System.out.println("   4. What words seem most important for classification?");
            System.out.println("   5. How could you improve the model's performance?");
            
            System.out.println("\n🎓 Student exercise completed! Great job experimenting with the model!");
            
        } catch (Exception e) {
            System.err.println("Error in Yelp sentiment analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}