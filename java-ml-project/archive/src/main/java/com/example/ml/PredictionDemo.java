package com.example.ml;

import com.example.ml.LogisticRegressionClassifier;
import com.example.ml.NaiveBayesClassifier;
import com.example.ml.TextPreprocessor.BagOfWords;

/**
 * Demo predictions on new text
 */
public class PredictionDemo {
    
    public static void predictAndDisplay(String text, NaiveBayesClassifier nbModel, LogisticRegressionClassifier lrModel, 
                                       BagOfWords bagOfWords, String[] labelNames) {
        System.out.println("\nText: '" + text + "'");
        
        // Naive Bayes prediction
        String[] nbPred = NaiveBayesClassifier.predict(nbModel, new String[]{text}, bagOfWords, labelNames);
        System.out.println("Naive Bayes prediction: " + nbPred[0]);
        
        // Logistic Regression prediction
        String[] lrPred = LogisticRegressionClassifier.predict(lrModel, new String[]{text}, bagOfWords, labelNames);
        System.out.println("Logistic Regression prediction: " + lrPred[0]);
    }
}