// BlueJ version

import java.util.*;

/**
 * Train-Test Split Implementation - Phase 3
 * Implements stratified train-test splitting similar to sklearn.train_test_split
 */
public class TrainTestSplitter {
    
    public static class SplitResult {
        public final String[] trainTexts;
        public final String[] testTexts;
        public final String[] trainLabels;
        public final String[] testLabels;
        public final int[] trainIndices;
        public final int[] testIndices;
        
        public SplitResult(String[] trainTexts, String[] testTexts, 
                         String[] trainLabels, String[] testLabels,
                         int[] trainIndices, int[] testIndices) {
            this.trainTexts = trainTexts;
            this.testTexts = testTexts;
            this.trainLabels = trainLabels;
            this.testLabels = testLabels;
            this.trainIndices = trainIndices;
            this.testIndices = testIndices;
        }
        
        public void printInfo() {
            System.out.println("Train-Test Split Results:");
            System.out.println("  Training set: " + trainTexts.length + " samples");
            System.out.println("  Test set: " + testTexts.length + " samples");
            System.out.println("  Test ratio: " + String.format("%.1f%%", 
                (double) testTexts.length / (trainTexts.length + testTexts.length) * 100));
        }
        
        public void printLabelDistribution() {
            System.out.println("\nLabel Distribution:");
            
            // Training set distribution
            Map<String, Integer> trainCounts = new HashMap<>();
            for (String label : trainLabels) {
                trainCounts.put(label, trainCounts.getOrDefault(label, 0) + 1);
            }
            
            System.out.println("  Training set:");
            for (Map.Entry<String, Integer> entry : trainCounts.entrySet()) {
                System.out.println("    " + entry.getKey() + ": " + entry.getValue());
            }
            
            // Test set distribution
            Map<String, Integer> testCounts = new HashMap<>();
            for (String label : testLabels) {
                testCounts.put(label, testCounts.getOrDefault(label, 0) + 1);
            }
            
            System.out.println("  Test set:");
            for (Map.Entry<String, Integer> entry : testCounts.entrySet()) {
                System.out.println("    " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        public void printSampleData() {
            System.out.println("\nSample Training Data:");
            for (int i = 0; i < Math.min(5, trainTexts.length); i++) {
                System.out.println("  " + (i+1) + ". " + trainTexts[i] + " -> " + trainLabels[i]);
            }
            
            System.out.println("\nSample Test Data:");
            for (int i = 0; i < Math.min(5, testTexts.length); i++) {
                System.out.println("  " + (i+1) + ". " + testTexts[i] + " -> " + testLabels[i]);
            }
        }
    }
    
    /**
     * Perform stratified train-test split
     * Similar to sklearn.train_test_split with stratify parameter
     */
    public static SplitResult split(String[] texts, String[] labels, 
                                  double testSize, long randomSeed) {
        if (texts.length != labels.length) {
            throw new IllegalArgumentException("Texts and labels must have same length");
        }
        
        if (testSize <= 0 || testSize >= 1) {
            throw new IllegalArgumentException("Test size must be between 0 and 1");
        }
        
        Random random = new Random(randomSeed);
        
        // Group indices by label for stratified splitting
        Map<String, List<Integer>> labelGroups = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            labelGroups.computeIfAbsent(labels[i], k -> new ArrayList<>()).add(i);
        }
        
        List<Integer> trainIndices = new ArrayList<>();
        List<Integer> testIndices = new ArrayList<>();
        
        // Split each label group proportionally
        for (Map.Entry<String, List<Integer>> entry : labelGroups.entrySet()) {
            List<Integer> indices = entry.getValue();
            Collections.shuffle(indices, random);
            
            int groupSize = indices.size();
            int testGroupSize = (int) Math.round(groupSize * testSize);
            
            // Ensure at least one sample in each set
            if (testGroupSize == 0 && groupSize > 1) {
                testGroupSize = 1;
            } else if (testGroupSize == groupSize && groupSize > 1) {
                testGroupSize = groupSize - 1;
            }
            
            // Add to test set
            for (int i = 0; i < testGroupSize; i++) {
                testIndices.add(indices.get(i));
            }
            
            // Add to training set
            for (int i = testGroupSize; i < groupSize; i++) {
                trainIndices.add(indices.get(i));
            }
        }
        
        // Shuffle the final indices
        Collections.shuffle(trainIndices, random);
        Collections.shuffle(testIndices, random);
        
        // Create arrays
        String[] trainTexts = new String[trainIndices.size()];
        String[] testTexts = new String[testIndices.size()];
        String[] trainLabels = new String[trainIndices.size()];
        String[] testLabels = new String[testIndices.size()];
        int[] trainIdxArray = new int[trainIndices.size()];
        int[] testIdxArray = new int[testIndices.size()];
        
        for (int i = 0; i < trainIndices.size(); i++) {
            int idx = trainIndices.get(i);
            trainTexts[i] = texts[idx];
            trainLabels[i] = labels[idx];
            trainIdxArray[i] = idx;
        }
        
        for (int i = 0; i < testIndices.size(); i++) {
            int idx = testIndices.get(i);
            testTexts[i] = texts[idx];
            testLabels[i] = labels[idx];
            testIdxArray[i] = idx;
        }
        
        return new SplitResult(trainTexts, testTexts, trainLabels, testLabels, 
                             trainIdxArray, testIdxArray);
    }
    
    /**
     * Perform stratified train-test split with default parameters
     */
    public static SplitResult split(String[] texts, String[] labels) {
        return split(texts, labels, 0.2, 42);
    }
    
    /**
     * Validate split results
     */
    public static boolean validateSplit(SplitResult split) {
        // Check that all labels are represented in both sets
        Set<String> trainLabels = new HashSet<>(Arrays.asList(split.trainLabels));
        Set<String> testLabels = new HashSet<>(Arrays.asList(split.testLabels));
        
        // All labels in test set should be in training set
        for (String label : testLabels) {
            if (!trainLabels.contains(label)) {
                System.err.println("Warning: Label '" + label + "' only in test set");
                return false;
            }
        }
        
        // Check minimum sample requirements
        if (split.trainTexts.length < 2) {
            System.err.println("Warning: Training set too small (" + split.trainTexts.length + " samples)");
            return false;
        }
        
        if (split.testTexts.length < 1) {
            System.err.println("Warning: Test set too small (" + split.testTexts.length + " samples)");
            return false;
        }
        
        return true;
    }
    
    /**
     * Test the train-test split functionality
     */
    public static void main(String[] args) {
        System.out.println("=== Train-Test Split Test ===\n");
        
        // Create test data
        String[] texts = {
            "therefore", "however", "consequently", "furthermore", "demonstrate",
            "illustrate", "analyze", "evaluate", "investigate", "examine",
            "omg", "lol", "btw", "fyi", "tbh", "imo", "yolo", "lit", "fire", "goals", "mood"
        };
        
        String[] labels = {
            "formal", "formal", "formal", "formal", "formal",
            "formal", "formal", "formal", "formal", "formal",
            "slang", "slang", "slang", "slang", "slang", "slang", 
            "slang", "slang", "slang", "slang", "slang"
        };
        
        System.out.println("Original dataset:");
        System.out.println("  Total samples: " + texts.length);
        
        Map<String, Integer> originalCounts = new HashMap<>();
        for (String label : labels) {
            originalCounts.put(label, originalCounts.getOrDefault(label, 0) + 1);
        }
        System.out.println("  Label distribution:");
        for (Map.Entry<String, Integer> entry : originalCounts.entrySet()) {
            System.out.println("    " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
        
        // Test different split ratios
        double[] testSizes = {0.2, 0.3, 0.4};
        
        for (double testSize : testSizes) {
            System.out.println("=== Test Size: " + String.format("%.0f%%", testSize * 100) + " ===");
            
            SplitResult split = split(texts, labels, testSize, 42);
            
            split.printInfo();
            split.printLabelDistribution();
            
            // Validate split
            boolean isValid = validateSplit(split);
            System.out.println("  Split validation: " + (isValid ? "✅ Valid" : "❌ Invalid"));
            
            System.out.println();
        }
        
        // Test reproducibility
        System.out.println("=== Reproducibility Test ===");
        SplitResult split1 = split(texts, labels, 0.2, 42);
        SplitResult split2 = split(texts, labels, 0.2, 42);
        
        boolean reproducible = Arrays.equals(split1.testIndices, split2.testIndices);
        System.out.println("Reproducible with same seed: " + (reproducible ? "✅ Yes" : "❌ No"));
        
        System.out.println("\n✅ Train-test split test completed!");
    }
}