// BlueJ version

import java.util.*;
import java.util.regex.Pattern;

/**
 * Text Preprocessing and Feature Extraction - Phase 2 Implementation
 * Handles text preprocessing and feature extraction similar to scikit-learn
 */
public class TextPreprocessor {
    
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9\\s]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    
    /**
     * Preprocess a single text string
     */
    public static String preprocessText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Convert to lowercase
        String processed = text.toLowerCase();
        
        // Remove special characters (keep alphanumeric and spaces)
        processed = NON_ALPHANUMERIC.matcher(processed).replaceAll(" ");
        
        // Normalize whitespace
        processed = WHITESPACE.matcher(processed).replaceAll(" ").trim();
        
        return processed;
    }
    
    /**
     * Preprocess an array of text strings
     */
    public static String[] preprocessTexts(String[] texts) {
        String[] processed = new String[texts.length];
        for (int i = 0; i < texts.length; i++) {
            processed[i] = preprocessText(texts[i]);
        }
        return processed;
    }
    
    /**
     * Tokenize text into words
     */
    public static String[] tokenize(String text) {
        String processed = preprocessText(text);
        if (processed.isEmpty()) {
            return new String[0];
        }
        return processed.split("\\s+");
    }
    
    /**
     * Tokenize multiple texts
     */
    public static String[][] tokenizeTexts(String[] texts) {
        String[][] tokens = new String[texts.length][];
        for (int i = 0; i < texts.length; i++) {
            tokens[i] = tokenize(texts[i]);
        }
        return tokens;
    }
    
    /**
     * Simple Bag of Words implementation
     */
    public static class BagOfWords {
        private final Map<String, Integer> vocabulary;
        private final List<String> vocabularyList;
        private final int minDf;
        private final int maxDf;
        
        public BagOfWords(int minDf, int maxDf) {
            this.minDf = minDf;
            this.maxDf = maxDf;
            this.vocabulary = new HashMap<>();
            this.vocabularyList = new ArrayList<>();
        }
        
        public BagOfWords() {
            this(1, Integer.MAX_VALUE);
        }
        
        /**
         * Fit the vocabulary from training texts
         */
        public void fit(String[] texts) {
            Map<String, Integer> wordCounts = new HashMap<>();
            int totalDocs = texts.length;
            
            // Count word frequencies across all documents
            for (String text : texts) {
                String[] tokens = tokenize(text);
                Set<String> uniqueWords = new HashSet<>(Arrays.asList(tokens));
                
                for (String word : uniqueWords) {
                    wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                }
            }
            
            // Build vocabulary based on document frequency
            vocabulary.clear();
            vocabularyList.clear();
            
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                String word = entry.getKey();
                int docFreq = entry.getValue();
                
                if (docFreq >= minDf && docFreq <= maxDf) {
                    vocabulary.put(word, vocabularyList.size());
                    vocabularyList.add(word);
                }
            }
        }
        
        /**
         * Transform texts to feature matrix
         */
        public double[][] transform(String[] texts) {
            double[][] features = new double[texts.length][vocabularyList.size()];
            
            for (int i = 0; i < texts.length; i++) {
                String[] tokens = tokenize(texts[i]);
                
                // Count word frequencies in this document
                Map<String, Integer> wordCounts = new HashMap<>();
                for (String token : tokens) {
                    wordCounts.put(token, wordCounts.getOrDefault(token, 0) + 1);
                }
                
                // Create feature vector
                for (int j = 0; j < vocabularyList.size(); j++) {
                    String word = vocabularyList.get(j);
                    features[i][j] = wordCounts.getOrDefault(word, 0);
                }
            }
            
            return features;
        }
        
        /**
         * Fit and transform in one step
         */
        public double[][] fitTransform(String[] texts) {
            fit(texts);
            return transform(texts);
        }
        
        public int getVocabularySize() {
            return vocabularyList.size();
        }
        
        public List<String> getVocabulary() {
            return new ArrayList<>(vocabularyList);
        }
        
        public Map<String, Integer> getVocabularyMap() {
            return new HashMap<>(vocabulary);
        }
    }
    
    /**
     * Simple TF-IDF implementation
     */
    public static class TfIdfVectorizer {
        private final BagOfWords bow;
        private double[] idfScores;
        
        public TfIdfVectorizer(int minDf, int maxDf) {
            this.bow = new BagOfWords(minDf, maxDf);
        }
        
        public TfIdfVectorizer() {
            this.bow = new BagOfWords();
        }
        
        /**
         * Fit TF-IDF on training texts
         */
        public void fit(String[] texts) {
            bow.fit(texts);
            
            // Calculate IDF scores
            int vocabSize = bow.getVocabularySize();
            int totalDocs = texts.length;
            idfScores = new double[vocabSize];
            
            for (int i = 0; i < vocabSize; i++) {
                String word = bow.getVocabulary().get(i);
                int docCount = 0;
                
                // Count documents containing this word
                for (String text : texts) {
                    String[] tokens = tokenize(text);
                    if (Arrays.asList(tokens).contains(word)) {
                        docCount++;
                    }
                }
                
                // Calculate IDF: log(total_docs / docs_with_word)
                idfScores[i] = Math.log((double) totalDocs / docCount);
            }
        }
        
        /**
         * Transform texts to TF-IDF matrix
         */
        public double[][] transform(String[] texts) {
            double[][] tfMatrix = bow.transform(texts);
            double[][] tfIdfMatrix = new double[texts.length][bow.getVocabularySize()];
            
            for (int i = 0; i < texts.length; i++) {
                for (int j = 0; j < bow.getVocabularySize(); j++) {
                    // TF-IDF = TF * IDF
                    tfIdfMatrix[i][j] = tfMatrix[i][j] * idfScores[j];
                }
            }
            
            return tfIdfMatrix;
        }
        
        /**
         * Fit and transform in one step
         */
        public double[][] fitTransform(String[] texts) {
            fit(texts);
            return transform(texts);
        }
        
        public int getVocabularySize() {
            return bow.getVocabularySize();
        }
        
        public List<String> getVocabulary() {
            return bow.getVocabulary();
        }
    }
    
    /**
     * Test the preprocessing functionality
     */
    public static void main(String[] args) {
        System.out.println("=== Text Preprocessing Test ===\n");
        
        // Test data
        String[] testTexts = {
            "Therefore, we can conclude...",
            "OMG! That's amazing!!!",
            "However, the results show...",
            "LOL, that's hilarious!",
            "Demonstrate the concept clearly."
        };
        
        System.out.println("Original texts:");
        for (int i = 0; i < testTexts.length; i++) {
            System.out.println((i+1) + ". " + testTexts[i]);
        }
        System.out.println();
        
        // Test preprocessing
        String[] processed = preprocessTexts(testTexts);
        System.out.println("Preprocessed texts:");
        for (int i = 0; i < processed.length; i++) {
            System.out.println((i+1) + ". " + processed[i]);
        }
        System.out.println();
        
        // Test tokenization
        System.out.println("Tokenized texts:");
        for (int i = 0; i < processed.length; i++) {
            String[] tokens = tokenize(testTexts[i]);
            System.out.println((i+1) + ". " + Arrays.toString(tokens));
        }
        System.out.println();
        
        // Test Bag of Words
        System.out.println("=== Bag of Words Test ===");
        BagOfWords bow = new BagOfWords();
        double[][] bowFeatures = bow.fitTransform(testTexts);
        
        System.out.println("Vocabulary size: " + bow.getVocabularySize());
        System.out.println("Vocabulary: " + bow.getVocabulary());
        System.out.println("Feature matrix shape: " + bowFeatures.length + " x " + bowFeatures[0].length);
        
        // Show first few features
        System.out.println("\nFirst 3 feature vectors:");
        for (int i = 0; i < Math.min(3, bowFeatures.length); i++) {
            System.out.println("Text " + (i+1) + ": " + Arrays.toString(Arrays.copyOf(bowFeatures[i], Math.min(10, bowFeatures[i].length))));
        }
        
        // Test TF-IDF
        System.out.println("\n=== TF-IDF Test ===");
        TfIdfVectorizer tfidf = new TfIdfVectorizer();
        double[][] tfidfFeatures = tfidf.fitTransform(testTexts);
        
        System.out.println("TF-IDF vocabulary size: " + tfidf.getVocabularySize());
        System.out.println("TF-IDF matrix shape: " + tfidfFeatures.length + " x " + tfidfFeatures[0].length);
        
        System.out.println("\n✅ Text preprocessing test completed!");
    }
}