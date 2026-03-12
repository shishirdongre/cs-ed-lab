import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Refactored Yelp Sentiment Analysis entry point.
 * Uses separate classes: DataLoader, DataSplitter, SentimentModelTrainer,
 * SentimentPredictor, ModelEvaluator, SampleReviewTester.
 * Run this for the class-based design; YelpSentimentAnalysisSpark is the original monolithic version.
 *
 * Command-line usage:
 *   java ... YelpSentimentAnalysisRefactored                    - Full pipeline + sample testing
 *   java ... YelpSentimentAnalysisRefactored "Your review text"  - Train, predict single review, write JSON
 */
public class YelpSentimentAnalysisRefactored {

    private static final String CSV_FILE = "simple_yelp_reviews.csv";
    private static final double TEST_SIZE = 0.2;
    private static final long RANDOM_SEED = 42L;
    private static final String DEFAULT_OUTPUT_FILE = "sentiment_output.json";
    private static final String MODEL_PATH = "saved_sentiment_model";

    public static void main(String[] args) {
        SparkSession spark = SparkInitializer.createSession();

        try {
            DataLoader loader = new DataLoader(CSV_FILE);
            Dataset<Row> data = loader.loadAndPrepare(spark);

            DataSplitter splitter = new DataSplitter(TEST_SIZE, RANDOM_SEED);
            Dataset<Row>[] splits = splitter.split(data);
            Dataset<Row> trainData = splits[0];
            Dataset<Row> testData = splits[1];

            SentimentModelTrainer trainer = new SentimentModelTrainer();
            PipelineModel model = trainer.train(trainData);

            // Save model for inference
            model.write().overwrite().save(MODEL_PATH);

            if (args.length > 0) {
                // CLI mode: predict single review and write JSON
                String outputFile = args.length > 1 ? args[args.length - 1] : DEFAULT_OUTPUT_FILE;
                String review = args.length > 1
                        ? String.join(" ", Arrays.copyOf(args, args.length - 1))
                        : args[0];
                predictAndWriteJson(model, spark, review, outputFile);
            } else {
                // Full pipeline mode
                SentimentPredictor predictor = new SentimentPredictor();
                Dataset<Row> predictions = predictor.predict(testData, model);

                ModelEvaluator evaluator = new ModelEvaluator();
                evaluator.evaluate(predictions);

                SampleReviewTester sampleTester = new SampleReviewTester();
                sampleTester.run(model, spark);
            }

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }

    /**
     * Predict sentiment for a single review and write JSON to file.
     */
    private static void predictAndWriteJson(PipelineModel model, SparkSession spark,
                                           String review, String outputFile) throws IOException {
        SentimentPredictor predictor = new SentimentPredictor();

        Dataset<Row> reviewData = spark.createDataFrame(
                Arrays.asList(RowFactory.create(review, 0)),
                new StructType(new StructField[]{
                        DataTypes.createStructField("text", DataTypes.StringType, false),
                        DataTypes.createStructField("label", DataTypes.IntegerType, false)
                })
        );

        Dataset<Row> predResult = predictor.transform(reviewData, model);
        Row result = predResult.select("prediction").collectAsList().get(0);
        int predictedLabel = result.get(0) instanceof Integer
                ? result.getInt(0)
                : ((Number) result.get(0)).intValue();

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
