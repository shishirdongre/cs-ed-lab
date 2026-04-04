package com.example.ml;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.example.ml.DataLoader;
import com.example.ml.DataSplitter;
import com.example.ml.ModelEvaluator;
import com.example.ml.SampleReviewTester;
import com.example.ml.SentimentModelTrainer;
import com.example.ml.SentimentPredictor;
import com.example.ml.SparkInitializer;

/**
 * Yelp Sentiment Analysis entry point (class-based pipeline).
 * Uses separate classes: DataLoader, DataSplitter, SentimentModelTrainer,
 * SentimentPredictor, ModelEvaluator, SampleReviewTester.
 *
 * Command-line usage:
 *   java ... YelpSentimentAnalysis                    - Full pipeline + sample testing
 *   java ... YelpSentimentAnalysis "Your review text"  - Train, predict single review, write JSON
 */
public class YelpSentimentAnalysis {

    private static final String CSV_FILE = "simple_yelp_reviews.csv";
    private static final double TEST_SIZE = 0.2;
    private static final long RANDOM_SEED = 42L;
    private static final String DEFAULT_OUTPUT_FILE = "sentiment_output.json";
    /** Same path as SentimentPredictorApp; Docker build copies this directory into the runtime image. */
    private static final String SAVED_MODEL_DIR = "saved_sentiment_model";

    public static void main(String[] args) {
        SparkSession spark = SparkInitializer.createSession();

        try {
            // 1) Load labeled reviews from CSV.
            DataLoader loader = new DataLoader(CSV_FILE);
            Dataset<Row> data = loader.load(spark);

            // 2) Train / test split.
            DataSplitter splitter = new DataSplitter(TEST_SIZE, RANDOM_SEED);
            Dataset<Row>[] splits = splitter.split(data);
            Dataset<Row> trainData = splits[0];
            Dataset<Row> testData = splits[1];

            // 3) Fit pipeline on training data only.
            SentimentModelTrainer trainer = new SentimentModelTrainer();
            PipelineModel model = trainer.train(trainData);
            model.write().overwrite().save(SAVED_MODEL_DIR);

            if (args.length > 0) {
                String outputFile = args.length > 1 ? args[args.length - 1] : DEFAULT_OUTPUT_FILE;
                String review = args.length > 1
                        ? String.join(" ", Arrays.copyOf(args, args.length - 1))
                        : args[0];
                predictAndWriteJson(model, spark, review, outputFile);
            } else {
                // Evaluate on held-out test rows, then try hand-written examples.
                SentimentPredictor predictor = new SentimentPredictor();
                Dataset<Row> predictions = predictor.predict(testData, model);

                ModelEvaluator evaluator = new ModelEvaluator();
                evaluator.evaluate(predictions);

                SampleReviewTester sampleTester = new SampleReviewTester();
                sampleTester.run(model, spark);
            }

        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }

    /** Writes one review's predicted sentiment as JSON (used when CLI args are present). */
    private static void predictAndWriteJson(PipelineModel model, SparkSession spark,
                                           String review, String outputFile) throws IOException {
        SentimentPredictor predictor = new SentimentPredictor();
        int predictedLabel = predictor.predictLabelForText(model, spark, review);
        String sentiment = (predictedLabel == 1) ? "positive" : "negative";

        String json = String.format(
                "{\"review\":\"%s\",\"sentiment\":\"%s\",\"label\":%d}",
                escapeJson(review), sentiment, predictedLabel
        );

        Path path = Path.of(outputFile);
        Files.writeString(path, json, StandardCharsets.UTF_8);
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
