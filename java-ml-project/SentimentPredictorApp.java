import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Loads the saved sentiment model and predicts for a single review.
 * Usage: java ... SentimentPredictorApp "review text" [output_file]
 * Requires the model to be trained first (run YelpSentimentAnalysisRefactored once).
 */
public class SentimentPredictorApp {

    private static final String DEFAULT_MODEL_PATH = "saved_sentiment_model";
    private static final String DEFAULT_OUTPUT_FILE = "sentiment_output.json";

    public static void main(String[] args) {
        System.out.println("[DEBUG] SentimentPredictorApp main() entered, args.length=" + args.length);
        if (args.length == 0) {
            System.err.println("Usage: SentimentPredictorApp \"review text\" [output_file]");
            System.err.println("Model path: " + DEFAULT_MODEL_PATH + " (or set SENTIMENT_MODEL_PATH env var)");
            System.exit(1);
        }

        // Last arg = output file; all others = review text (same as YelpSentimentAnalysisRefactored)
        String outputFile = args.length > 1 ? args[args.length - 1] : DEFAULT_OUTPUT_FILE;
        String review = args.length > 1
                ? String.join(" ", Arrays.copyOf(args, args.length - 1))
                : args[0];
        String modelPath = System.getenv().getOrDefault("SENTIMENT_MODEL_PATH", DEFAULT_MODEL_PATH);
        System.out.println("[DEBUG] outputFile=" + outputFile + " modelPath=" + modelPath);

        System.out.println("[DEBUG] Creating SparkSession...");
        SparkSession spark = SparkInitializer.createSession();
        System.out.println("[DEBUG] SparkSession created");

        try {
            System.out.println("[DEBUG] Loading model from " + modelPath + "...");
            PipelineModel model = PipelineModel.load(modelPath);
            System.out.println("[DEBUG] Model loaded");

            System.out.println("[DEBUG] Creating review DataFrame...");
            Dataset<Row> reviewData = spark.createDataFrame(
                    Arrays.asList(RowFactory.create(review, 0)),
                    new StructType(new StructField[]{
                            DataTypes.createStructField("text", DataTypes.StringType, false),
                            DataTypes.createStructField("label", DataTypes.IntegerType, false)
                    })
            );
            System.out.println("[DEBUG] DataFrame created");

            System.out.println("[DEBUG] Running model.transform()...");
            Dataset<Row> predResult = model.transform(reviewData);
            System.out.println("[DEBUG] Transform done, collecting result...");
            Row result = predResult.select("prediction").collectAsList().get(0);
            int predictedLabel = result.get(0) instanceof Integer
                    ? result.getInt(0)
                    : ((Number) result.get(0)).intValue();

            String sentiment = (predictedLabel == 1) ? "positive" : "negative";
            System.out.println("[DEBUG] Prediction: " + sentiment + " (label=" + predictedLabel + ")");

            String json = String.format(
                    "{\"review\":\"%s\",\"sentiment\":\"%s\",\"label\":%d}",
                    escapeJson(review), sentiment, predictedLabel
            );

            System.out.println("[DEBUG] Writing to " + outputFile + "...");
            Path path = Path.of(outputFile);
            Files.writeString(path, json, StandardCharsets.UTF_8);
            System.out.println("[DEBUG] Done, exiting 0");

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            spark.stop();
        }
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
