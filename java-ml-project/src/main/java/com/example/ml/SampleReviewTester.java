package com.example.ml;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.SparkSession;

import com.example.ml.SentimentPredictor;

/**
 * Runs the saved pipeline on fixed example reviews and prints expected vs predicted sentiment.
 */
public class SampleReviewTester {

    private static final String[][] SAMPLE_REVIEWS = {
            {"Great food, excellent service!", "positive"},
            {"Terrible food, bad service", "negative"},
            {"Amazing pizza, friendly staff", "positive"},
            {"This place is absolutely horrible", "negative"},
            {"Love this restaurant, will come back", "positive"},
            {"Worst experience ever", "negative"},
            {"Outstanding quality and service", "positive"},
            {"Complete waste of money", "negative"},
            {"Perfect ambiance and delicious food", "positive"},
            {"Overpriced and disappointing", "negative"},
            {"Best restaurant in town, highly recommend", "positive"},
            {"Rude staff and cold food", "negative"},
            {"Fresh ingredients and great taste", "positive"},
            {"Long wait time and poor service", "negative"},
            {"Excellent value for money", "positive"},
            {"Dirty tables and slow service", "negative"},
            {"Cozy atmosphere and friendly staff", "positive"},
            {"Food was bland and tasteless", "negative"},
            {"Quick service and good portions", "positive"}
    };

    private final SentimentPredictor predictor = new SentimentPredictor();

    /** Prints a small table comparing gold labels to model output for each sample line. */
    public void run(PipelineModel model, SparkSession spark) {
        System.out.println("\n=== Sample reviews ===");
        System.out.println("Match  Expected    Predicted   Snippet");
        System.out.println("--------------------------------------------------");
        for (String[] sample : SAMPLE_REVIEWS) {
            String review = sample[0];
            String expected = sample[1];
            int predictedLabel = predictor.predictLabelForText(model, spark, review);
            String predicted = (predictedLabel == 1) ? "positive" : "negative";
            boolean match = expected.equals(predicted);
            String snippet = review.length() > 48 ? review.substring(0, 45) + "..." : review;
            System.out.printf("%-7s  %-10s  %-10s  %s%n",
                    match ? "yes" : "no", expected, predicted, snippet);
        }
        System.out.println("====================\n");
    }
}
