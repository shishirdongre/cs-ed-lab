// BlueJ version

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * Model evaluation metrics - Phase 5 Implementation
 * Comprehensive evaluation metrics similar to sklearn.metrics
 */
public class ModelEvaluator {
    
    public static double calculateAccuracy(String[] trueLabels, String[] predictedLabels) {
        int correct = 0;
        for (int i = 0; i < trueLabels.length; i++) {
            if (trueLabels[i].equals(predictedLabels[i])) {
                correct++;
            }
        }
        return (double) correct / trueLabels.length;
    }
    
    public static void printConfusionMatrix(String[] trueLabels, String[] predictedLabels, String[] uniqueLabels) {
        int[][] matrix = new int[uniqueLabels.length][uniqueLabels.length];
        
        // Create label to index mapping
        Map<String, Integer> labelToIndex = new HashMap<>();
        for (int i = 0; i < uniqueLabels.length; i++) {
            labelToIndex.put(uniqueLabels[i], i);
        }
        
        // Fill confusion matrix
        for (int i = 0; i < trueLabels.length; i++) {
            int trueIndex = labelToIndex.get(trueLabels[i]);
            int predIndex = labelToIndex.get(predictedLabels[i]);
            matrix[trueIndex][predIndex]++;
        }
        
        // Print matrix
        System.out.println("Confusion Matrix:");
        System.out.print("Actual\\Predicted\t");
        for (String label : uniqueLabels) {
            System.out.print(label + "\t");
        }
        System.out.println();
        
        for (int i = 0; i < uniqueLabels.length; i++) {
            System.out.print(uniqueLabels[i] + "\t\t");
            for (int j = 0; j < uniqueLabels.length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }
    
    public static void printClassificationReport(String[] trueLabels, String[] predictedLabels, String[] uniqueLabels) {
        System.out.println("\nClassification Report:");
        System.out.println("Label\t\tPrecision\tRecall\t\tF1-Score");
        System.out.println("-".repeat(50));
        
        Map<String, Integer> labelToIndex = new HashMap<>();
        for (int i = 0; i < uniqueLabels.length; i++) {
            labelToIndex.put(uniqueLabels[i], i);
        }
        
        int[][] matrix = new int[uniqueLabels.length][uniqueLabels.length];
        for (int i = 0; i < trueLabels.length; i++) {
            int trueIndex = labelToIndex.get(trueLabels[i]);
            int predIndex = labelToIndex.get(predictedLabels[i]);
            matrix[trueIndex][predIndex]++;
        }
        
        for (int i = 0; i < uniqueLabels.length; i++) {
            String label = uniqueLabels[i];
            
            // Calculate precision
            int truePositives = matrix[i][i];
            int falsePositives = 0;
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (j != i) falsePositives += matrix[j][i];
            }
            double precision = (truePositives + falsePositives == 0) ? 0 : 
                (double) truePositives / (truePositives + falsePositives);
            
            // Calculate recall
            int falseNegatives = 0;
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (j != i) falseNegatives += matrix[i][j];
            }
            double recall = (truePositives + falseNegatives == 0) ? 0 : 
                (double) truePositives / (truePositives + falseNegatives);
            
            // Calculate F1-score
            double f1Score = (precision + recall == 0) ? 0 : 
                2 * (precision * recall) / (precision + recall);
            
            System.out.printf("%s\t\t%.3f\t\t%.3f\t\t%.3f\n", label, precision, recall, f1Score);
        }
    }
    
    /**
     * Calculate balanced accuracy (average of recall for each class)
     */
    public static double calculateBalancedAccuracy(String[] trueLabels, String[] predictedLabels, String[] uniqueLabels) {
        Map<String, Integer> labelToIndex = new HashMap<>();
        for (int i = 0; i < uniqueLabels.length; i++) {
            labelToIndex.put(uniqueLabels[i], i);
        }
        
        int[][] matrix = buildConfusionMatrix(trueLabels, predictedLabels, uniqueLabels, labelToIndex);
        
        double totalRecall = 0.0;
        for (int i = 0; i < uniqueLabels.length; i++) {
            int truePositives = matrix[i][i];
            int falseNegatives = 0;
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (j != i) falseNegatives += matrix[i][j];
            }
            double recall = (truePositives + falseNegatives == 0) ? 0 : 
                (double) truePositives / (truePositives + falseNegatives);
            totalRecall += recall;
        }
        
        return totalRecall / uniqueLabels.length;
    }
    
    /**
     * Calculate per-class accuracy
     */
    public static Map<String, Double> calculatePerClassAccuracy(String[] trueLabels, String[] predictedLabels, String[] uniqueLabels) {
        Map<String, Double> perClassAccuracy = new HashMap<>();
        Map<String, Integer> labelToIndex = new HashMap<>();
        for (int i = 0; i < uniqueLabels.length; i++) {
            labelToIndex.put(uniqueLabels[i], i);
        }
        
        int[][] matrix = buildConfusionMatrix(trueLabels, predictedLabels, uniqueLabels, labelToIndex);
        
        for (int i = 0; i < uniqueLabels.length; i++) {
            String label = uniqueLabels[i];
            int truePositives = matrix[i][i];
            int totalActual = 0;
            for (int j = 0; j < uniqueLabels.length; j++) {
                totalActual += matrix[i][j];
            }
            double accuracy = (totalActual == 0) ? 0 : (double) truePositives / totalActual;
            perClassAccuracy.put(label, accuracy);
        }
        
        return perClassAccuracy;
    }
    
    /**
     * Calculate macro and micro averages for precision, recall, and F1-score
     */
    public static void printDetailedClassificationReport(String[] trueLabels, String[] predictedLabels, String[] uniqueLabels) {
        System.out.println("\n=== DETAILED CLASSIFICATION REPORT ===");
        
        Map<String, Integer> labelToIndex = new HashMap<>();
        for (int i = 0; i < uniqueLabels.length; i++) {
            labelToIndex.put(uniqueLabels[i], i);
        }
        
        int[][] matrix = buildConfusionMatrix(trueLabels, predictedLabels, uniqueLabels, labelToIndex);
        
        // Calculate metrics for each class
        double[] precisions = new double[uniqueLabels.length];
        double[] recalls = new double[uniqueLabels.length];
        double[] f1Scores = new double[uniqueLabels.length];
        int[] supports = new int[uniqueLabels.length];
        
        System.out.println("Label\t\tPrecision\tRecall\t\tF1-Score\tSupport");
        System.out.println("-".repeat(70));
        
        for (int i = 0; i < uniqueLabels.length; i++) {
            String label = uniqueLabels[i];
            
            // Calculate support (actual count for this class)
            int support = 0;
            for (int j = 0; j < uniqueLabels.length; j++) {
                support += matrix[i][j];
            }
            supports[i] = support;
            
            // Calculate precision
            int truePositives = matrix[i][i];
            int falsePositives = 0;
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (j != i) falsePositives += matrix[j][i];
            }
            precisions[i] = (truePositives + falsePositives == 0) ? 0 : 
                (double) truePositives / (truePositives + falsePositives);
            
            // Calculate recall
            int falseNegatives = 0;
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (j != i) falseNegatives += matrix[i][j];
            }
            recalls[i] = (truePositives + falseNegatives == 0) ? 0 : 
                (double) truePositives / (truePositives + falseNegatives);
            
            // Calculate F1-score
            f1Scores[i] = (precisions[i] + recalls[i] == 0) ? 0 : 
                2 * (precisions[i] * recalls[i]) / (precisions[i] + recalls[i]);
            
            System.out.printf("%s\t\t%.3f\t\t%.3f\t\t%.3f\t\t%d\n", 
                label, precisions[i], recalls[i], f1Scores[i], support);
        }
        
        // Calculate macro averages
        double macroPrecision = Arrays.stream(precisions).average().orElse(0.0);
        double macroRecall = Arrays.stream(recalls).average().orElse(0.0);
        double macroF1 = Arrays.stream(f1Scores).average().orElse(0.0);
        
        // Calculate micro averages
        int totalTruePositives = 0;
        int totalFalsePositives = 0;
        int totalFalseNegatives = 0;
        
        for (int i = 0; i < uniqueLabels.length; i++) {
            totalTruePositives += matrix[i][i];
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (j != i) {
                    totalFalsePositives += matrix[j][i];
                    totalFalseNegatives += matrix[i][j];
                }
            }
        }
        
        double microPrecision = (totalTruePositives + totalFalsePositives == 0) ? 0 :
            (double) totalTruePositives / (totalTruePositives + totalFalsePositives);
        double microRecall = (totalTruePositives + totalFalseNegatives == 0) ? 0 :
            (double) totalTruePositives / (totalTruePositives + totalFalseNegatives);
        double microF1 = (microPrecision + microRecall == 0) ? 0 :
            2 * (microPrecision * microRecall) / (microPrecision + microRecall);
        
        int totalSupport = Arrays.stream(supports).sum();
        
        System.out.println("-".repeat(70));
        System.out.printf("Macro avg\t%.3f\t\t%.3f\t\t%.3f\t\t%d\n", 
            macroPrecision, macroRecall, macroF1, totalSupport);
        System.out.printf("Micro avg\t%.3f\t\t%.3f\t\t%.3f\t\t%d\n", 
            microPrecision, microRecall, microF1, totalSupport);
    }
    
    /**
     * Print comprehensive evaluation summary
     */
    public static void printEvaluationSummary(String[] trueLabels, String[] predictedLabels, String[] uniqueLabels) {
        System.out.println("\n=== EVALUATION SUMMARY ===");
        
        // Overall accuracy
        double accuracy = calculateAccuracy(trueLabels, predictedLabels);
        System.out.printf("Overall Accuracy: %.3f (%.1f%%)\n", accuracy, accuracy * 100);
        
        // Balanced accuracy
        double balancedAccuracy = calculateBalancedAccuracy(trueLabels, predictedLabels, uniqueLabels);
        System.out.printf("Balanced Accuracy: %.3f (%.1f%%)\n", balancedAccuracy, balancedAccuracy * 100);
        
        // Per-class accuracy
        Map<String, Double> perClassAccuracy = calculatePerClassAccuracy(trueLabels, predictedLabels, uniqueLabels);
        System.out.println("\nPer-Class Accuracy:");
        for (String label : uniqueLabels) {
            System.out.printf("  %s: %.3f (%.1f%%)\n", label, perClassAccuracy.get(label), perClassAccuracy.get(label) * 100);
        }
        
        // Confusion matrix
        printConfusionMatrix(trueLabels, predictedLabels, uniqueLabels);
        
        // Detailed classification report
        printDetailedClassificationReport(trueLabels, predictedLabels, uniqueLabels);
    }
    
    /**
     * Helper method to build confusion matrix
     */
    private static int[][] buildConfusionMatrix(String[] trueLabels, String[] predictedLabels, 
                                              String[] uniqueLabels, Map<String, Integer> labelToIndex) {
        int[][] matrix = new int[uniqueLabels.length][uniqueLabels.length];
        
        for (int i = 0; i < trueLabels.length; i++) {
            int trueIndex = labelToIndex.get(trueLabels[i]);
            int predIndex = labelToIndex.get(predictedLabels[i]);
            matrix[trueIndex][predIndex]++;
        }
        
        return matrix;
    }
    
    /**
     * Test the evaluation metrics
     */
    public static void main(String[] args) {
        System.out.println("=== Model Evaluator Test ===\n");
        
        // Test data
        String[] trueLabels = {"formal", "formal", "slang", "slang", "formal", "slang"};
        String[] predictedLabels = {"formal", "slang", "slang", "formal", "formal", "slang"};
        String[] uniqueLabels = {"formal", "slang"};
        
        System.out.println("True labels: " + Arrays.toString(trueLabels));
        System.out.println("Predicted:   " + Arrays.toString(predictedLabels));
        System.out.println();
        
        // Test all evaluation methods
        printEvaluationSummary(trueLabels, predictedLabels, uniqueLabels);
        
        System.out.println("\n✅ Model evaluation test completed!");
    }
}