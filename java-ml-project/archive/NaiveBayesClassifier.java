// BlueJ version

import java.util.*;

/**
 * Naive Bayes Classifier Implementation - Phase 4
 * Implements Multinomial Naive Bayes similar to sklearn.naive_bayes.MultinomialNB
 */
public class NaiveBayesClassifier {
    
    private Map<String, Integer> labelToIndex;
    private Map<Integer, String> indexToLabel;
    private double[][] featureProbs; // [label_index][feature_index] = P(feature|label)
    private double[] labelProbs;     // [label_index] = P(label)
    private double[] featureCounts;  // [feature_index] = total count for smoothing
    private int numFeatures;
    private int numLabels;
    private double alpha; // Laplace smoothing parameter
    
    public NaiveBayesClassifier(double alpha) {
        this.alpha = alpha;
        this.labelToIndex = new HashMap<>();
        this.indexToLabel = new HashMap<>();
    }
    
    public NaiveBayesClassifier() {
        this(1.0); // Default Laplace smoothing
    }
    
    /**
     * Fit the Naive Bayes model to training data
     */
    public void fit(double[][] X, String[] y) {
        if (X.length != y.length) {
            throw new IllegalArgumentException("X and y must have same length");
        }
        
        // Build label mapping
        Set<String> uniqueLabels = new HashSet<>(Arrays.asList(y));
        numLabels = uniqueLabels.size();
        
        int labelIndex = 0;
        for (String label : uniqueLabels) {
            labelToIndex.put(label, labelIndex);
            indexToLabel.put(labelIndex, label);
            labelIndex++;
        }
        
        numFeatures = X[0].length;
        
        // Initialize probability matrices
        featureProbs = new double[numLabels][numFeatures];
        labelProbs = new double[numLabels];
        featureCounts = new double[numFeatures];
        
        // Count features and labels
        double[][] featureCountsByLabel = new double[numLabels][numFeatures];
        double[] labelCounts = new double[numLabels];
        
        for (int i = 0; i < X.length; i++) {
            int labelIdx = labelToIndex.get(y[i]);
            labelCounts[labelIdx]++;
            
            for (int j = 0; j < numFeatures; j++) {
                double count = X[i][j];
                featureCountsByLabel[labelIdx][j] += count;
                featureCounts[j] += count;
            }
        }
        
        // Calculate probabilities with Laplace smoothing
        double totalSamples = X.length;
        
        // Label probabilities: P(label) = count(label) / total_samples
        for (int i = 0; i < numLabels; i++) {
            labelProbs[i] = labelCounts[i] / totalSamples;
        }
        
        // Feature probabilities: P(feature|label) = (count + alpha) / (sum + alpha * num_features)
        for (int i = 0; i < numLabels; i++) {
            double labelSum = Arrays.stream(featureCountsByLabel[i]).sum();
            
            for (int j = 0; j < numFeatures; j++) {
                featureProbs[i][j] = (featureCountsByLabel[i][j] + alpha) / 
                                   (labelSum + alpha * numFeatures);
            }
        }
    }
    
    /**
     * Predict class labels for test data
     */
    public String[] predict(double[][] X) {
        String[] predictions = new String[X.length];
        
        for (int i = 0; i < X.length; i++) {
            predictions[i] = predictSingle(X[i]);
        }
        
        return predictions;
    }
    
    /**
     * Predict class label for a single sample
     */
    private String predictSingle(double[] x) {
        double[] logProbs = new double[numLabels];
        
        // Calculate log probabilities for each label
        for (int i = 0; i < numLabels; i++) {
            // Start with log P(label)
            logProbs[i] = Math.log(labelProbs[i]);
            
            // Add log P(feature|label) for each feature
            for (int j = 0; j < numFeatures; j++) {
                if (x[j] > 0) {
                    logProbs[i] += x[j] * Math.log(featureProbs[i][j]);
                }
            }
        }
        
        // Find label with highest log probability
        int bestLabelIndex = 0;
        double bestLogProb = logProbs[0];
        
        for (int i = 1; i < numLabels; i++) {
            if (logProbs[i] > bestLogProb) {
                bestLogProb = logProbs[i];
                bestLabelIndex = i;
            }
        }
        
        return indexToLabel.get(bestLabelIndex);
    }
    
    /**
     * Predict class probabilities for test data
     */
    public double[][] predictProba(double[][] X) {
        double[][] probabilities = new double[X.length][numLabels];
        
        for (int i = 0; i < X.length; i++) {
            probabilities[i] = predictProbaSingle(X[i]);
        }
        
        return probabilities;
    }
    
    /**
     * Predict class probabilities for a single sample
     */
    private double[] predictProbaSingle(double[] x) {
        double[] logProbs = new double[numLabels];
        
        // Calculate log probabilities for each label
        for (int i = 0; i < numLabels; i++) {
            logProbs[i] = Math.log(labelProbs[i]);
            
            for (int j = 0; j < numFeatures; j++) {
                if (x[j] > 0) {
                    logProbs[i] += x[j] * Math.log(featureProbs[i][j]);
                }
            }
        }
        
        // Convert log probabilities to probabilities using log-sum-exp trick
        double maxLogProb = Arrays.stream(logProbs).max().orElse(0.0);
        double[] probs = new double[numLabels];
        double sumExp = 0.0;
        
        for (int i = 0; i < numLabels; i++) {
            probs[i] = Math.exp(logProbs[i] - maxLogProb);
            sumExp += probs[i];
        }
        
        // Normalize
        for (int i = 0; i < numLabels; i++) {
            probs[i] /= sumExp;
        }
        
        return probs;
    }
    
    /**
     * Get model information
     */
    public void printModelInfo() {
        System.out.println("Naive Bayes Model Info:");
        System.out.println("  Number of labels: " + numLabels);
        System.out.println("  Number of features: " + numFeatures);
        System.out.println("  Alpha (smoothing): " + alpha);
        System.out.println("  Labels: " + labelToIndex.keySet());
        
        System.out.println("\nLabel Probabilities:");
        for (int i = 0; i < numLabels; i++) {
            System.out.println("  " + indexToLabel.get(i) + ": " + String.format("%.4f", labelProbs[i]));
        }
    }
    
    /**
     * Static method to train Naive Bayes model (for compatibility with main class)
     */
    public static NaiveBayesClassifier trainNaiveBayes(String[] texts, String[] labels, 
                                                      TextPreprocessor.BagOfWords bagOfWords) {
        // Convert texts to feature matrix using BagOfWords
        double[][] X = bagOfWords.transform(texts);
        
        // Train the model
        NaiveBayesClassifier model = new NaiveBayesClassifier();
        model.fit(X, labels);
        
        return model;
    }
    
    /**
     * Static method to make predictions (for compatibility with main class)
     */
    public static String[] predict(NaiveBayesClassifier model, String[] texts, 
                                 TextPreprocessor.BagOfWords bagOfWords, 
                                 String[] labelNames) {
        // Convert texts to feature matrix using BagOfWords
        double[][] X = bagOfWords.transform(texts);
        
        // Make predictions
        return model.predict(X);
    }
    
    /**
     * Test the Naive Bayes classifier
     */
    public static void main(String[] args) {
        System.out.println("=== Naive Bayes Classifier Test ===\n");
        
        // Create simple test data
        double[][] X_train = {
            {1, 0, 1, 0}, // formal words
            {1, 0, 0, 1},
            {0, 1, 1, 0},
            {0, 0, 1, 1},
            {1, 1, 0, 0}, // slang words
            {0, 1, 0, 1},
            {1, 0, 1, 1},
            {0, 1, 1, 1}
        };
        
        String[] y_train = {"formal", "formal", "formal", "formal", 
                          "slang", "slang", "slang", "slang"};
        
        double[][] X_test = {
            {1, 0, 0, 0}, // should be formal
            {0, 1, 0, 0}  // should be slang
        };
        
        System.out.println("Training data:");
        System.out.println("  Features: " + X_train.length + " x " + X_train[0].length);
        System.out.println("  Labels: " + Arrays.toString(y_train));
        System.out.println();
        
        // Train the model
        NaiveBayesClassifier nb = new NaiveBayesClassifier();
        nb.fit(X_train, y_train);
        
        System.out.println("✅ Model trained successfully");
        nb.printModelInfo();
        System.out.println();
        
        // Make predictions
        String[] predictions = nb.predict(X_test);
        double[][] probabilities = nb.predictProba(X_test);
        
        System.out.println("Predictions:");
        for (int i = 0; i < X_test.length; i++) {
            System.out.println("  Sample " + (i+1) + ": " + Arrays.toString(X_test[i]) + 
                             " -> " + predictions[i]);
            System.out.println("    Probabilities: " + Arrays.toString(probabilities[i]));
        }
        
        System.out.println("\n✅ Naive Bayes classifier test completed!");
    }
}