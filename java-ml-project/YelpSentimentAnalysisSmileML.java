// BlueJ version - Yelp Review Sentiment Analysis

import java.util.*;
import java.io.*;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import smile.classification.NaiveBayes;
import smile.stat.distribution.Distribution;
import smile.stat.distribution.GaussianDistribution;
import smile.nlp.stemmer.PorterStemmer;

/**
 * Yelp Review Sentiment Analysis
 * Uses real Yelp dataset for restaurant review sentiment classification
 * Implements Naive Bayes classifier for sentiment analysis
 */
public class YelpSentimentAnalysisSmileML {
    
    public static void main(String[] args) {
        System.out.println("=== Yelp Review Sentiment Analysis ===");
        
        try {
            // Load and prepare data
            DataPreparationResult dataResult = loadAndPrepareData();
            
            // Train the model
            ModelTrainingResult model = trainModel(dataResult);
            
            // Evaluate the model
            evaluateModel(model);
            
            // Test on sample reviews
            testSampleReviews(model);
            
            
            System.out.println("\n✅ Analysis completed!");
            
            
        } catch (Exception e) {
            System.err.println("Error in Yelp sentiment analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load and prepare the Yelp dataset
     */
    private static DataPreparationResult loadAndPrepareData() {
        
        // Load CSV data
        List<String[]> csvData = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader("simple_yelp_reviews.csv"))) {
            csvData = reader.readAll();
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            throw new RuntimeException("Error loading CSV file", e);
        }
        
        
        // Extract texts and labels
        String[] texts = new String[csvData.size() - 1];
        String[] labels = new String[csvData.size() - 1];
        
        for (int i = 1; i < csvData.size(); i++) {
            texts[i-1] = csvData.get(i)[0];
            labels[i-1] = csvData.get(i)[1];
        }
        
        
        // Text preprocessing
        String[] processedTexts = Arrays.stream(texts)
            .map(text -> StringUtils.lowerCase(text))
            .map(text -> StringUtils.replaceChars(text, "!@#$%^&*()_+-=[]{}|;':\",./<>?`~", " "))
            .map(text -> StringUtils.normalizeSpace(text))
            .toArray(String[]::new);
        
        
        return new DataPreparationResult(texts, processedTexts, labels);
    }
    
    /**
     * Train the Naive Bayes model
     */
    private static ModelTrainingResult trainModel(DataPreparationResult dataResult) {
        // Train-test split
        
        // Convert to feature format
        double[][] features = createBagOfWordsFeatures(dataResult.processedTexts);
        int[] labels = convertLabelsToInt(dataResult.labels);
        
        // Perform train-test split
        TrainTestSplitResult split = performTrainTestSplit(features, labels);
        
        
        // Train Naive Bayes model using Smile library
        NaiveBayes nbModel = trainSmileNaiveBayes(split.trainFeatures, split.trainLabels);
        
        
        // Make predictions
        int[] predictions = new int[split.testFeatures.length];
        for (int i = 0; i < split.testFeatures.length; i++) {
            predictions[i] = nbModel.predict(split.testFeatures[i]);
        }
        
        
        return new ModelTrainingResult(nbModel, features, split, predictions, dataResult.labels);
    }
    
    /**
     * Evaluate the model performance
     */
    private static void evaluateModel(ModelTrainingResult model) {
        
        // Calculate accuracy
        double accuracy = calculateAccuracy(model.split.testLabels, model.predictions);
        System.out.println("   Overall Accuracy: " + String.format("%.3f", accuracy) + " (" + String.format("%.1f%%", accuracy * 100) + ")");
        
        // Confusion matrix
        int[][] confusionMatrix = calculateConfusionMatrix(model.split.testLabels, model.predictions);
        System.out.println("\n   Confusion Matrix:");
        System.out.println("   Actual\\Predicted\tNegative\tPositive");
        System.out.println("   Negative\t\t" + confusionMatrix[0][0] + "\t\t" + confusionMatrix[0][1]);
        System.out.println("   Positive\t\t" + confusionMatrix[1][0] + "\t\t" + confusionMatrix[1][1]);
        
        // Classification metrics
        ClassificationMetrics metrics = calculateClassificationMetrics(model.split.testLabels, model.predictions);
        System.out.println("\n   Classification Report:");
        System.out.println("   Precision: " + String.format("%.3f", metrics.precision));
        System.out.println("   Recall: " + String.format("%.3f", metrics.recall));
        System.out.println("   F1-Score: " + String.format("%.3f", metrics.f1));
    }
    
    /**
     * Test the model on sample reviews
     */
    private static void testSampleReviews(ModelTrainingResult model) {
        String[] sampleReviews = {
            "Great food, excellent service!",
            "Terrible food, bad service",
            "Amazing pizza, friendly staff"
        };
        
        for (String review : sampleReviews) {
            // Preprocess the review
            String processed = StringUtils.lowerCase(review);
            processed = StringUtils.replaceChars(processed, "!@#$%^&*()_+-=[]{}|;':\",./<>?`~", " ");
            processed = StringUtils.normalizeSpace(processed);
            
            // Convert to feature vector
            double[] features = createSimpleFeatureVector(processed);
            
            // Make prediction
            int prediction = model.nbModel.predict(features);
            String sentiment = prediction == 0 ? "negative" : "positive";
            
            String shortReview = review.length() > 50 ? review.substring(0, 50) + "..." : review;
            System.out.println("   '" + shortReview + "' -> " + sentiment);
        }
    }
    
    
    
        
    /**
     * Train Smile NaiveBayes model
     */
    private static NaiveBayes trainSmileNaiveBayes(double[][] features, int[] labels) {
        int numClasses = 2; // negative (0) and positive (1)
        int numFeatures = features[0].length;
        
        // Calculate prior probabilities
        double[] priori = new double[numClasses];
        for (int label : labels) {
            priori[label]++;
        }
        for (int i = 0; i < numClasses; i++) {
            priori[i] /= labels.length;
        }
        
        // Calculate conditional distributions for each feature in each class
        Distribution[][] condprob = new Distribution[numClasses][numFeatures];
        
        for (int classIdx = 0; classIdx < numClasses; classIdx++) {
            // Get features for this class
            List<double[]> classFeatures = new ArrayList<>();
            for (int i = 0; i < features.length; i++) {
                if (labels[i] == classIdx) {
                    classFeatures.add(features[i]);
                }
            }
            
            // Calculate distribution for each feature in this class
            for (int featureIdx = 0; featureIdx < numFeatures; featureIdx++) {
                double[] featureValues = new double[classFeatures.size()];
                for (int i = 0; i < classFeatures.size(); i++) {
                    featureValues[i] = classFeatures.get(i)[featureIdx];
                }
                
                // Fit Gaussian distribution for this feature in this class
                condprob[classIdx][featureIdx] = GaussianDistribution.fit(featureValues);
            }
        }
        
        return new NaiveBayes(priori, condprob);
    }
    
    /**
     * Create bag of words features using Smile library with PorterStemmer
     */
    private static double[][] createBagOfWordsFeatures(String[] texts) {
        // Tokenize and create bag of words for each text
        List<Map<String, Integer>> bags = new ArrayList<>();
        PorterStemmer stemmer = new PorterStemmer();
        
        for (String text : texts) {
            // Simple tokenization and cleaning
            String[] words = text.toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", " ") // Remove punctuation
                .split("\\s+");
            
            // Create bag of words with stemming
            Map<String, Integer> bag = new HashMap<>();
            for (String word : words) {
                if (!word.isEmpty() && word.length() > 2) { // Filter short words
                    String stemmed = stemmer.stem(word);
                    bag.put(stemmed, bag.getOrDefault(stemmed, 0) + 1);
                }
            }
            bags.add(bag);
        }
        
        // Build vocabulary from all bags
        Set<String> vocabulary = new HashSet<>();
        for (Map<String, Integer> bag : bags) {
            vocabulary.addAll(bag.keySet());
        }
        
        // Convert to array and limit vocabulary size
        String[] features = vocabulary.toArray(new String[0]);
        int vocabSize = Math.min(features.length, 5000);
        String[] selectedFeatures = Arrays.copyOf(features, vocabSize);
        
        // Create feature matrix
        double[][] featureMatrix = new double[texts.length][vocabSize];
        
        for (int i = 0; i < texts.length; i++) {
            Map<String, Integer> bag = bags.get(i);
            for (int j = 0; j < vocabSize; j++) {
                featureMatrix[i][j] = bag.getOrDefault(selectedFeatures[j], 0);
            }
        }
        
        return featureMatrix;
    }
    
    /**
     * Convert string labels to integers
     */
    private static int[] convertLabelsToInt(String[] labels) {
        int[] intLabels = new int[labels.length];
        for (int i = 0; i < labels.length; i++) {
            intLabels[i] = "positive".equals(labels[i]) ? 1 : 0;
        }
        return intLabels;
    }
    
    /**
     * Create feature vector for prediction using same approach as training
     */
    private static double[] createSimpleFeatureVector(String text) {
        // Use same vocabulary size as training (5000)
        double[] features = new double[5000];
        PorterStemmer stemmer = new PorterStemmer();
        
        // Simple tokenization and cleaning (same as training)
        String[] words = text.toLowerCase()
            .replaceAll("[^a-zA-Z\\s]", " ") // Remove punctuation
            .split("\\s+");
        
        // Create bag of words with stemming (same as training)
        Map<String, Integer> bag = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty() && word.length() > 2) { // Filter short words
                String stemmed = stemmer.stem(word);
                bag.put(stemmed, bag.getOrDefault(stemmed, 0) + 1);
            }
        }
        
        // Use hash-based mapping to fit into fixed vocabulary size
        for (Map.Entry<String, Integer> entry : bag.entrySet()) {
            int hash = Math.abs(entry.getKey().hashCode()) % 5000;
            features[hash] += entry.getValue();
        }
        
        return features;
    }
    
    /**
     * Perform train-test split with default values (20% test, random seed 42)
     */
    private static TrainTestSplitResult performTrainTestSplit(double[][] features, int[] labels) {
        return performTrainTestSplit(features, labels, 0.2, 42);
    }
    
    /**
     * Perform train-test split with custom parameters
     */
    private static TrainTestSplitResult performTrainTestSplit(double[][] features, int[] labels, double testSize, long randomSeed) {
        Random random = new Random(randomSeed);
        int totalSize = features.length;
        int testSizeInt = (int) (totalSize * testSize);
        
        // Create indices and shuffle
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < totalSize; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);
        
        // Split indices
        List<Integer> testIndices = indices.subList(0, testSizeInt);
        List<Integer> trainIndices = indices.subList(testSizeInt, totalSize);
        
        // Create arrays
        double[][] trainFeatures = trainIndices.stream().map(i -> features[i]).toArray(double[][]::new);
        double[][] testFeatures = testIndices.stream().map(i -> features[i]).toArray(double[][]::new);
        int[] trainLabels = trainIndices.stream().mapToInt(i -> labels[i]).toArray();
        int[] testLabels = testIndices.stream().mapToInt(i -> labels[i]).toArray();
        
        return new TrainTestSplitResult(trainFeatures, testFeatures, trainLabels, testLabels);
    }
    
    /**
     * Calculate accuracy
     */
    private static double calculateAccuracy(int[] trueLabels, int[] predictedLabels) {
        int correct = 0;
        for (int i = 0; i < trueLabels.length; i++) {
            if (trueLabels[i] == predictedLabels[i]) {
                correct++;
            }
        }
        return (double) correct / trueLabels.length;
    }
    
    /**
     * Calculate confusion matrix
     */
    private static int[][] calculateConfusionMatrix(int[] trueLabels, int[] predictedLabels) {
        int[][] matrix = new int[2][2];
        for (int i = 0; i < trueLabels.length; i++) {
            matrix[trueLabels[i]][predictedLabels[i]]++;
        }
        return matrix;
    }
    
    /**
     * Calculate classification metrics
     */
    private static ClassificationMetrics calculateClassificationMetrics(int[] trueLabels, int[] predictedLabels) {
        int[][] matrix = calculateConfusionMatrix(trueLabels, predictedLabels);
        
        double precision = (double) matrix[1][1] / (matrix[0][1] + matrix[1][1]);
        double recall = (double) matrix[1][1] / (matrix[1][0] + matrix[1][1]);
        double f1 = 2 * (precision * recall) / (precision + recall);
        
        return new ClassificationMetrics(precision, recall, f1);
    }
    
    
    /**
     * Train-test split result container
     */
    private static class TrainTestSplitResult {
        final double[][] trainFeatures;
        final double[][] testFeatures;
        final int[] trainLabels;
        final int[] testLabels;
        
        TrainTestSplitResult(double[][] trainFeatures, double[][] testFeatures, 
                           int[] trainLabels, int[] testLabels) {
            this.trainFeatures = trainFeatures;
            this.testFeatures = testFeatures;
            this.trainLabels = trainLabels;
            this.testLabels = testLabels;
        }
    }
    
    /**
     * Classification metrics container
     */
    private static class ClassificationMetrics {
        final double precision;
        final double recall;
        final double f1;
        
        ClassificationMetrics(double precision, double recall, double f1) {
            this.precision = precision;
            this.recall = recall;
            this.f1 = f1;
        }
    }
    
    /**
     * Data preparation result container
     */
    private static class DataPreparationResult {
        final String[] originalTexts;
        final String[] processedTexts;
        final String[] labels;
        
        DataPreparationResult(String[] originalTexts, String[] processedTexts, String[] labels) {
            this.originalTexts = originalTexts;
            this.processedTexts = processedTexts;
            this.labels = labels;
        }
    }
    
    /**
     * Model training result container
     */
    private static class ModelTrainingResult {
        final NaiveBayes nbModel;
        final double[][] features;
        final TrainTestSplitResult split;
        final int[] predictions;
        final String[] originalLabels;
        
        ModelTrainingResult(NaiveBayes nbModel, double[][] features, TrainTestSplitResult split, 
                          int[] predictions, String[] originalLabels) {
            this.nbModel = nbModel;
            this.features = features;
            this.split = split;
            this.predictions = predictions;
            this.originalLabels = originalLabels;
        }
    }
}