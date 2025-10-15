package com.example.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Handles train-test split functionality
 */
public class TrainTestSplit {
    
    public static class SplitResult {
        public final String[] trainTexts;
        public final String[] testTexts;
        public final String[] trainLabels;
        public final String[] testLabels;
        
        public SplitResult(String[] trainTexts, String[] testTexts, 
                         String[] trainLabels, String[] testLabels) {
            this.trainTexts = trainTexts;
            this.testTexts = testTexts;
            this.trainLabels = trainLabels;
            this.testLabels = testLabels;
        }
    }
    
    public static SplitResult split(String[] texts, String[] labels, double testSize, long randomSeed) {
        Random random = new Random(randomSeed);
        int totalSize = texts.length;
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
        String[] trainTexts = trainIndices.stream().map(i -> texts[i]).toArray(String[]::new);
        String[] testTexts = testIndices.stream().map(i -> texts[i]).toArray(String[]::new);
        String[] trainLabels = trainIndices.stream().map(i -> labels[i]).toArray(String[]::new);
        String[] testLabels = testIndices.stream().map(i -> labels[i]).toArray(String[]::new);
        
        return new SplitResult(trainTexts, testTexts, trainLabels, testLabels);
    }
}