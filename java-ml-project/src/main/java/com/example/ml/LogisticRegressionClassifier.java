package com.example.ml;

import java.util.*;

/**
 * Logistic Regression Classifier Implementation - Phase 4
 * Implements Logistic Regression similar to sklearn.linear_model.LogisticRegression
 */
public class LogisticRegressionClassifier {
    
    private Map<String, Integer> labelToIndex;
    private Map<Integer, String> indexToLabel;
    private double[][] weights; // [label_index][feature_index] = weight
    private double[] biases;    // [label_index] = bias
    private int numFeatures;
    private int numLabels;
    private double learningRate;
    private int maxIterations;
    private double tolerance;
    
    public LogisticRegressionClassifier(double learningRate, int maxIterations, double tolerance) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
        this.labelToIndex = new HashMap<>();
        this.indexToLabel = new HashMap<>();
    }
    
    public LogisticRegressionClassifier() {
        this(0.01, 1000, 1e-4); // Default parameters
    }
    
    /**
     * Fit the Logistic Regression model to training data
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
        
        // Initialize weights and biases
        weights = new double[numLabels][numFeatures];
        biases = new double[numLabels];
        
        // Initialize with small random values
        Random random = new Random(42);
        for (int i = 0; i < numLabels; i++) {
            biases[i] = 0.0;
            for (int j = 0; j < numFeatures; j++) {
                weights[i][j] = random.nextGaussian() * 0.01;
            }
        }
        
        // Convert labels to numeric
        int[] yNumeric = new int[y.length];
        for (int i = 0; i < y.length; i++) {
            yNumeric[i] = labelToIndex.get(y[i]);
        }
        
        // Train using gradient descent
        trainGradientDescent(X, yNumeric);
    }
    
    /**
     * Train the model using gradient descent
     */
    private void trainGradientDescent(double[][] X, int[] y) {
        int nSamples = X.length;
        
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            double[][] gradients = new double[numLabels][numFeatures];
            double[] biasGradients = new double[numLabels];
            
            // Calculate gradients for each sample
            for (int i = 0; i < nSamples; i++) {
                double[] probabilities = softmax(X[i]);
                
                // Calculate gradients
                for (int j = 0; j < numLabels; j++) {
                    double target = (y[i] == j) ? 1.0 : 0.0;
                    double error = probabilities[j] - target;
                    
                    biasGradients[j] += error;
                    
                    for (int k = 0; k < numFeatures; k++) {
                        gradients[j][k] += error * X[i][k];
                    }
                }
            }
            
            // Update weights and biases
            boolean converged = true;
            for (int j = 0; j < numLabels; j++) {
                double biasUpdate = learningRate * biasGradients[j] / nSamples;
                biases[j] -= biasUpdate;
                
                for (int k = 0; k < numFeatures; k++) {
                    double weightUpdate = learningRate * gradients[j][k] / nSamples;
                    weights[j][k] -= weightUpdate;
                    
                    if (Math.abs(weightUpdate) > tolerance) {
                        converged = false;
                    }
                }
            }
            
            if (converged) {
                System.out.println("Converged after " + (iteration + 1) + " iterations");
                break;
            }
        }
    }
    
    /**
     * Apply softmax function to get probabilities
     */
    private double[] softmax(double[] x) {
        double[] scores = new double[numLabels];
        
        // Calculate scores
        for (int i = 0; i < numLabels; i++) {
            scores[i] = biases[i];
            for (int j = 0; j < numFeatures; j++) {
                scores[i] += weights[i][j] * x[j];
            }
        }
        
        // Apply softmax
        double maxScore = Arrays.stream(scores).max().orElse(0.0);
        double sumExp = 0.0;
        
        for (int i = 0; i < numLabels; i++) {
            scores[i] = Math.exp(scores[i] - maxScore);
            sumExp += scores[i];
        }
        
        for (int i = 0; i < numLabels; i++) {
            scores[i] /= sumExp;
        }
        
        return scores;
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
        double[] probabilities = softmax(x);
        
        // Find label with highest probability
        int bestLabelIndex = 0;
        double bestProb = probabilities[0];
        
        for (int i = 1; i < numLabels; i++) {
            if (probabilities[i] > bestProb) {
                bestProb = probabilities[i];
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
            probabilities[i] = softmax(X[i]);
        }
        
        return probabilities;
    }
    
    /**
     * Get model information
     */
    public void printModelInfo() {
        System.out.println("Logistic Regression Model Info:");
        System.out.println("  Number of labels: " + numLabels);
        System.out.println("  Number of features: " + numFeatures);
        System.out.println("  Learning rate: " + learningRate);
        System.out.println("  Max iterations: " + maxIterations);
        System.out.println("  Labels: " + labelToIndex.keySet());
        
        System.out.println("\nBiases:");
        for (int i = 0; i < numLabels; i++) {
            System.out.println("  " + indexToLabel.get(i) + ": " + String.format("%.4f", biases[i]));
        }
        
        System.out.println("\nFeature Weights (first 5 features):");
        for (int i = 0; i < numLabels; i++) {
            System.out.print("  " + indexToLabel.get(i) + ": [");
            for (int j = 0; j < Math.min(5, numFeatures); j++) {
                System.out.print(String.format("%.4f", weights[i][j]));
                if (j < Math.min(5, numFeatures) - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }
    
    /**
     * Static method to train Logistic Regression model (for compatibility with main class)
     */
    public static LogisticRegressionClassifier trainLogisticRegression(String[] texts, String[] labels, 
                                                                     com.example.ml.TextPreprocessor.BagOfWords bagOfWords) {
        // Convert texts to feature matrix using BagOfWords
        double[][] X = bagOfWords.transform(texts);
        
        // Train the model
        LogisticRegressionClassifier model = new LogisticRegressionClassifier();
        model.fit(X, labels);
        
        return model;
    }
    
    /**
     * Static method to make predictions (for compatibility with main class)
     */
    public static String[] predict(LogisticRegressionClassifier model, String[] texts, 
                                 com.example.ml.TextPreprocessor.BagOfWords bagOfWords, 
                                 String[] labelNames) {
        // Convert texts to feature matrix using BagOfWords
        double[][] X = bagOfWords.transform(texts);
        
        // Make predictions
        return model.predict(X);
    }
    
    /**
     * Test the Logistic Regression classifier
     */
    public static void main(String[] args) {
        System.out.println("=== Logistic Regression Classifier Test ===\n");
        
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
        LogisticRegressionClassifier lr = new LogisticRegressionClassifier();
        lr.fit(X_train, y_train);
        
        System.out.println("✅ Model trained successfully");
        lr.printModelInfo();
        System.out.println();
        
        // Make predictions
        String[] predictions = lr.predict(X_test);
        double[][] probabilities = lr.predictProba(X_test);
        
        System.out.println("Predictions:");
        for (int i = 0; i < X_test.length; i++) {
            System.out.println("  Sample " + (i+1) + ": " + Arrays.toString(X_test[i]) + 
                             " -> " + predictions[i]);
            System.out.println("    Probabilities: " + Arrays.toString(probabilities[i]));
        }
        
        System.out.println("\n✅ Logistic Regression classifier test completed!");
    }
}