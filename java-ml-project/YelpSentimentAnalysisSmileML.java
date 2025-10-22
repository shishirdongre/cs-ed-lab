// BlueJ version - Yelp Review Sentiment Analysis

import java.util.*;
import java.io.*;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import smile.classification.NaiveBayes;
import smile.nlp.stemmer.PorterStemmer;
import smile.stat.distribution.Distribution;
import smile.stat.distribution.GaussianDistribution;
import smile.stat.distribution.PoissonDistribution;

/**
 * Yelp Review Sentiment Analysis
 * Uses real Yelp dataset for restaurant review sentiment classification
 * Implements Multinomial Naive Bayes classifier for sentiment analysis
 */
public class YelpSentimentAnalysisSmileML {
    
    // Vocabulary class for consistent word-to-index mapping
    static class Vocab {
        final Map<String,Integer> index;  // token -> column
        Vocab(Map<String,Integer> index) { this.index = index; }
        int size() { return index.size(); }
    }

    // Vectorization result containing vocabulary and feature matrix
    private static class Vectorization {
        final Vocab vocab;
        final double[][] X;
        Vectorization(Vocab vocab, double[][] X) { this.vocab = vocab; this.X = X; }
    }

    // Index split for stratified sampling
    private static class IndexSplit { 
        final int[] trainIdx, testIdx; 
        IndexSplit(int[] tr, int[] te){trainIdx=tr;testIdx=te;} 
    }


    
    public static void main(String[] args) {
        System.out.println("=== Yelp Review Sentiment Analysis ===");
        
        try {
            // Load and prepare data
            System.out.println("Loading data...");
            DataPreparationResult dataResult = loadAndPrepareData();
            System.out.println("Data loaded: " + dataResult.processedTexts.length + " samples");
            System.out.println("About to call trainModel...");
            
            // Train the model
            System.out.println("About to call trainModel...");
            ModelTrainingResult model;
            try {
                model = trainModel(dataResult);
                System.out.println("trainModel completed successfully");
            } catch (Exception e) {
                System.err.println("Error in trainModel: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // Evaluate the model
            evaluateModel(model);
            
            // Test on sample reviews
            testSampleReviews(model);
            
            
            System.out.println("\n✅ Analysis completed!");
            
            
        } catch (Exception e) {
            System.err.println("Error in Yelp sentiment analysis: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Stack trace:");
            e.printStackTrace();
        }
    }
    
    /**
     * Load and prepare the Yelp dataset with proper header handling
     */
    private static DataPreparationResult loadAndPrepareData() {
        List<String[]> rows;
        try (CSVReader reader = new CSVReader(new FileReader("simple_yelp_reviews.csv"))) {
            rows = reader.readAll();
        } catch (Exception e) {
            throw new RuntimeException("Error loading CSV file", e);
        }
        if (rows.isEmpty()) throw new IllegalArgumentException("Empty CSV");

        // Skip header
        rows = rows.subList(1, rows.size());

        String[] texts  = new String[rows.size()];
        String[] labels = new String[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            texts[i]  = rows.get(i)[0];              // "text"
            labels[i] = rows.get(i)[1].trim();       // "positive"/"negative"
        }

        // Simple normalization consistent with vectorizer below
        String[] processed = Arrays.stream(texts)
            .map(StringUtils::lowerCase)
            .map(t -> t.replaceAll("[^a-zA-Z\\s]", " "))
            .map(StringUtils::normalizeSpace)
            .toArray(String[]::new);

        System.out.println("Loaded " + texts.length + " samples");
        System.out.println("Sample labels: " + Arrays.toString(Arrays.copyOf(labels, Math.min(5, labels.length))));
        
        return new DataPreparationResult(texts, processed, labels);
    }
    
    /**
     * Create bag of words for a single text using PorterStemmer
     */
    private static Map<String,Integer> bagOfWords(String text, PorterStemmer stemmer) {
        // Enhanced text preprocessing
        String processed = text.toLowerCase();
        // Remove URLs, emails, and special characters
        processed = processed.replaceAll("http\\S+|www\\S+|@\\S+", "");
        processed = processed.replaceAll("[^a-zA-Z\\s]", " ");
        // Remove extra whitespace
        processed = processed.replaceAll("\\s+", " ").trim();
        
        String[] words = processed.split("\\s+");
        Map<String,Integer> bag = new HashMap<>();
        for (String w : words) {
            if (w.length() > 2 && w.matches("[a-zA-Z]+")) { // Only alphabetic words
                String s = stemmer.stem(w);
                bag.put(s, bag.getOrDefault(s, 0) + 1);
            }
        }
        return bag;
    }
    
    /**
     * Build vocabulary from training data and vectorize
     */
    private static Vectorization buildVocabAndVectorizeTrain(String[] trainTexts, int vocabSize) {
        PorterStemmer stemmer = new PorterStemmer();
        // Document frequency (how many docs contain the token)
        Map<String,Integer> df = new HashMap<>();
        List<Map<String,Integer>> bags = new ArrayList<>();

        for (String t : trainTexts) {
            Map<String,Integer> bag = bagOfWords(t, stemmer);
            bags.add(bag);
            for (String w : bag.keySet()) df.put(w, df.getOrDefault(w, 0) + 1);
        }

        // Filter by minimum document frequency (at least 2 documents)
        Map<String,Integer> filteredDf = new HashMap<>();
        for (Map.Entry<String,Integer> entry : df.entrySet()) {
            if (entry.getValue() >= 2) { // Minimum document frequency
                filteredDf.put(entry.getKey(), entry.getValue());
            }
        }

        // top-K by DF (stable & effective for NB)
        List<Map.Entry<String,Integer>> sorted = new ArrayList<>(filteredDf.entrySet());
        sorted.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));
        int K = Math.min(vocabSize, sorted.size());
        Map<String,Integer> idx = new HashMap<>(K);
        for (int i = 0; i < K; i++) idx.put(sorted.get(i).getKey(), i);
        Vocab vocab = new Vocab(idx);

        double[][] X = new double[trainTexts.length][K];
        for (int i = 0; i < trainTexts.length; i++) fillRow(X[i], bags.get(i), vocab);
        return new Vectorization(vocab, X);
    }
    
    /**
     * Vectorize texts using existing vocabulary
     */
    private static double[][] vectorizeWithVocab(String[] texts, Vocab vocab) {
        PorterStemmer stemmer = new PorterStemmer();
        double[][] X = new double[texts.length][vocab.size()];
        for (int i = 0; i < texts.length; i++) {
            Map<String,Integer> bag = bagOfWords(texts[i], stemmer);
            fillRow(X[i], bag, vocab);
        }
        return X;
    }
    
    /**
     * Fill feature row using vocabulary mapping
     */
    private static void fillRow(double[] row, Map<String,Integer> bag, Vocab vocab) {
        Arrays.fill(row, 0.0);
        for (Map.Entry<String,Integer> e : bag.entrySet()) {
            Integer j = vocab.index.get(e.getKey());
            if (j != null) {
                // Use log(1 + count) to reduce impact of very frequent words
                row[j] = Math.log(1.0 + e.getValue());
            }
        }
    }
    
    /**
     * Convert string labels to integers with robust parsing
     */
    private static int[] convertLabelsToInt(String[] labels) {
        int[] y = new int[labels.length];
        for (int i = 0; i < labels.length; i++) {
            String s = labels[i].trim().toLowerCase();
            if (s.startsWith("pos"))      y[i] = 1;
            else if (s.startsWith("neg")) y[i] = 0;
            else throw new IllegalArgumentException("Unknown label: " + labels[i]);
        }
        return y;
    }
    
    /**
     * Perform stratified train-test split by class labels
     */
    private static TrainTestSplitResult stratifiedSplit(double[][] X, int[] y, double testSize, long seed) {
        Random rnd = new Random(seed);
        List<Integer> pos = new ArrayList<>(), neg = new ArrayList<>();
        for (int i = 0; i < y.length; i++) (y[i] == 1 ? pos : neg).add(i);
        Collections.shuffle(pos, rnd);
        Collections.shuffle(neg, rnd);

        int tp = Math.max(1, (int)Math.round(pos.size() * testSize));
        int tn = Math.max(1, (int)Math.round(neg.size() * testSize));

        List<Integer> testIdx = new ArrayList<>();
        testIdx.addAll(pos.subList(0, Math.min(tp, pos.size())));
        testIdx.addAll(neg.subList(0, Math.min(tn, neg.size())));

        boolean[] isTest = new boolean[y.length];
        for (int i : testIdx) isTest[i] = true;

        List<Integer> trainIdx = new ArrayList<>();
        for (int i = 0; i < y.length; i++) if (!isTest[i]) trainIdx.add(i);

        return slice(X, y, trainIdx, testIdx);
    }
    
    /**
     * Slice data using train/test indices
     */
    private static TrainTestSplitResult slice(double[][] X, int[] y, List<Integer> trainIdx, List<Integer> testIdx) {
        double[][] Xtr = new double[trainIdx.size()][];
        double[][] Xte = new double[testIdx.size()][];
        int[] ytr = new int[trainIdx.size()];
        int[] yte = new int[testIdx.size()];
        for (int i = 0; i < trainIdx.size(); i++) { Xtr[i] = X[trainIdx.get(i)]; ytr[i] = y[trainIdx.get(i)]; }
        for (int i = 0; i < testIdx.size(); i++)  { Xte[i] = X[testIdx.get(i)];  yte[i] = y[testIdx.get(i)];  }
        return new TrainTestSplitResult(Xtr, Xte, ytr, yte);
    }
    
    /**
     * Helper to compute stratified indices
     */
    private static IndexSplit performTrainTestSplitIndices(int n, double testSize, long seed, int[] y) {
        // Stratify by labels
        List<Integer> pos = new ArrayList<>(), neg = new ArrayList<>();
        for (int i = 0; i < n; i++) (y[i]==1?pos:neg).add(i);
        Random rnd = new Random(seed);
        Collections.shuffle(pos, rnd); Collections.shuffle(neg, rnd);
        int tp = Math.max(1, (int)Math.round(pos.size()*testSize));
        int tn = Math.max(1, (int)Math.round(neg.size()*testSize));
        List<Integer> test = new ArrayList<>();
        test.addAll(pos.subList(0, Math.min(tp, pos.size())));
        test.addAll(neg.subList(0, Math.min(tn, neg.size())));
        boolean[] isTest = new boolean[n]; for (int i:test) isTest[i]=true;
        List<Integer> train = new ArrayList<>(); for (int i=0;i<n;i++) if (!isTest[i]) train.add(i);
        return new IndexSplit(train.stream().mapToInt(Integer::intValue).toArray(),
                              test.stream().mapToInt(Integer::intValue).toArray());
    }
    
    /**
     * Train the Multinomial Naive Bayes model with proper vocabulary management
     */
    private static ModelTrainingResult trainModel(DataPreparationResult data) {
        System.out.println("DEBUG: trainModel called with " + data.processedTexts.length + " samples");
        int[] y = convertLabelsToInt(data.labels);

            // Build vocab and vectorize **on training only**
            int vocabSize = 50000; // Increased vocabulary size
        // First do a temporary vectorization on all to split indices consistently
        IndexSplit indexSplit = performTrainTestSplitIndices(y.length, 0.2, 42, y);
        // Build train texts and test texts arrays
        String[] trainTexts = new String[indexSplit.trainIdx.length];
        String[] testTexts = new String[indexSplit.testIdx.length];
        int[] yTrain = new int[indexSplit.trainIdx.length];
        int[] yTest = new int[indexSplit.testIdx.length];
        
        for (int i = 0; i < indexSplit.trainIdx.length; i++) {
            trainTexts[i] = data.processedTexts[indexSplit.trainIdx[i]];
            yTrain[i] = y[indexSplit.trainIdx[i]];
        }
        
        for (int i = 0; i < indexSplit.testIdx.length; i++) {
            testTexts[i] = data.processedTexts[indexSplit.testIdx[i]];
            yTest[i] = y[indexSplit.testIdx[i]];
        }
        
        System.out.println("Train texts: " + trainTexts.length + ", Test texts: " + testTexts.length);

        Vectorization vecTrain = buildVocabAndVectorizeTrain(trainTexts, vocabSize);
        double[][] Xtrain = vecTrain.X;
        double[][] Xtest  = vectorizeWithVocab(testTexts, vecTrain.vocab);

        // Debug: Print some statistics
        System.out.println("Training data shape: " + Xtrain.length + " x " + Xtrain[0].length);
        System.out.println("Test data shape: " + Xtest.length + " x " + Xtest[0].length);
        System.out.println("Vocabulary size: " + vecTrain.vocab.size());
        System.out.println("Training labels - Positive: " + Arrays.stream(yTrain).sum() + ", Negative: " + (yTrain.length - Arrays.stream(yTrain).sum()));
        System.out.println("Test labels - Positive: " + Arrays.stream(yTest).sum() + ", Negative: " + (yTest.length - Arrays.stream(yTest).sum()));
        
        // Debug: Check feature values
        double[] trainSums = new double[Xtrain.length];
        for (int i = 0; i < Xtrain.length; i++) {
            trainSums[i] = Arrays.stream(Xtrain[i]).sum();
        }
        System.out.println("Training feature sums - Min: " + Arrays.stream(trainSums).min().orElse(0) + 
                          ", Max: " + Arrays.stream(trainSums).max().orElse(0) + 
                          ", Mean: " + Arrays.stream(trainSums).average().orElse(0));
        
        // Check if features are too sparse
        int nonZeroFeatures = 0;
        for (int i = 0; i < Xtrain.length; i++) {
            for (int j = 0; j < Xtrain[i].length; j++) {
                if (Xtrain[i][j] > 0) nonZeroFeatures++;
            }
        }
        System.out.println("Non-zero features: " + nonZeroFeatures + " out of " + (Xtrain.length * Xtrain[0].length));
        
        // Debug: Check a few sample feature vectors
        System.out.println("Sample feature vectors:");
        for (int i = 0; i < Math.min(3, Xtrain.length); i++) {
            System.out.println("Sample " + i + " (class " + yTrain[i] + "): sum=" + trainSums[i] + 
                             ", non-zero features=" + Arrays.stream(Xtrain[i]).mapToInt(x -> x > 0 ? 1 : 0).sum());
        }

        NaiveBayes nb = trainSmileNaiveBayes(Xtrain, yTrain);

        int[] preds = new int[Xtest.length];
        for (int i = 0; i < Xtest.length; i++) preds[i] = nb.predict(Xtest[i]);
        
        // Debug: Print predictions
        System.out.println("Predictions - Positive: " + Arrays.stream(preds).sum() + ", Negative: " + (preds.length - Arrays.stream(preds).sum()));

        // Pack split so your evaluateModel(...) keeps working
        TrainTestSplitResult split = new TrainTestSplitResult(Xtrain, Xtest, yTrain, yTest);
        return new ModelTrainingResult(nb, null, split, preds, data.labels, vecTrain.vocab);
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
        String[] samples = {
            "Great food, excellent service!",
            "Terrible food, bad service",
            "Amazing pizza, friendly staff"
        };
        for (String s : samples) {
            String sentiment = predictSentiment(model.nbModel, model.vocab, s);
            System.out.println("   '" + (s.length()>60? s.substring(0,60)+"..." : s) + "' -> " + sentiment);
        }
    }
    
    /**
     * Predict sentiment using shared vocabulary
     */
    private static String predictSentiment(NaiveBayes nb, Vocab vocab, String raw) {
        PorterStemmer stemmer = new PorterStemmer();
        Map<String,Integer> bag = bagOfWords(raw, stemmer);
        double[] x = new double[vocab.size()];
        fillRow(x, bag, vocab);
        int pred = nb.predict(x);
        return pred == 1 ? "positive" : "negative";
    }
    
    
    
        
    /**
     * Train Smile NaiveBayes model
     */
    private static NaiveBayes trainSmileNaiveBayes(double[][] trainX, int[] trainY) {
        // Use Gaussian distribution with improved parameters for word counts
        int numClasses = 2;
        int numFeatures = trainX[0].length;

        // Calculate prior probabilities
        double[] priori = new double[numClasses];
        for (int label : trainY) {
            priori[label]++;
        }
        for (int i = 0; i < numClasses; i++) {
            priori[i] /= trainY.length;
        }

        // Calculate conditional distributions for each feature in each class
        Distribution[][] condprob = new Distribution[numClasses][numFeatures];

        for (int classIdx = 0; classIdx < numClasses; classIdx++) {
            // Get features for this class
            List<double[]> classFeatures = new ArrayList<>();
            for (int i = 0; i < trainX.length; i++) {
                if (trainY[i] == classIdx) {
                    classFeatures.add(trainX[i]);
                }
            }

            // Calculate distribution for each feature in this class
            for (int featureIdx = 0; featureIdx < numFeatures; featureIdx++) {
                double[] featureValues = new double[classFeatures.size()];
                for (int i = 0; i < classFeatures.size(); i++) {
                    featureValues[i] = classFeatures.get(i)[featureIdx];
                }

                // Calculate mean and variance for this feature in this class
                double mean = Arrays.stream(featureValues).average().orElse(0.0);
                double variance = 0.0;
                if (featureValues.length > 1) {
                    double sumSquaredDiffs = 0.0;
                    for (double value : featureValues) {
                        sumSquaredDiffs += Math.pow(value - mean, 2);
                    }
                    variance = sumSquaredDiffs / (featureValues.length - 1);
                }

                // Improved smoothing parameters
                variance = Math.max(variance, 0.5); // Higher minimum variance
                mean = Math.max(mean, 0.001); // Lower minimum mean

                condprob[classIdx][featureIdx] = new smile.stat.distribution.GaussianDistribution(mean, Math.sqrt(variance));
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
        final double[][] features;              // (unused now)
        final TrainTestSplitResult split;
        final int[] predictions;
        final String[] originalLabels;
        final Vocab vocab;                      // NEW

        ModelTrainingResult(NaiveBayes nbModel, double[][] features, TrainTestSplitResult split,
                            int[] predictions, String[] originalLabels, Vocab vocab) {
            this.nbModel = nbModel;
            this.features = features;
            this.split = split;
            this.predictions = predictions;
            this.originalLabels = originalLabels;
            this.vocab = vocab;
        }
    }
}