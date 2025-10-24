import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Spark imports
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.ml.classification.NaiveBayesModel;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.RowFactory;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.when;

/**
 * Yelp Sentiment Analysis using Apache Spark MLlib
 * Implements Multinomial Naive Bayes for text classification
 */
public class YelpSentimentAnalysisSpark {
    
    private static final String CSV_FILE = "simple_yelp_reviews.csv";
    private static final double TEST_SIZE = 0.2;
    private static final long RANDOM_SEED = 42L;
    
    public static void main(String[] args) {
        System.out.println("=== Yelp Sentiment Analysis with Apache Spark MLlib ===");
        
        // Initialize Spark
        SparkConf conf = new SparkConf()
            .setAppName("YelpSentimentAnalysis")
            .setMaster("local[*]")
            .set("spark.sql.adaptive.enabled", "false")
            .set("spark.sql.adaptive.coalescePartitions.enabled", "false");
            
        SparkSession spark = SparkSession.builder()
            .config(conf)
            .getOrCreate();
            
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
        
        try {
            // Load and prepare data
            System.out.println("Loading data...");
            Dataset<Row> data = loadData(spark);
            System.out.println("Loaded " + data.count() + " samples");
            
            // Show class distribution
            data.groupBy("label").count().show();
            
            // Split data
            System.out.println("\nSplitting data...");
            Dataset<Row>[] splits = data.randomSplit(new double[]{1.0 - TEST_SIZE, TEST_SIZE}, RANDOM_SEED);
            Dataset<Row> trainData = splits[0];
            Dataset<Row> testData = splits[1];
            
            System.out.println("Training samples: " + trainData.count());
            System.out.println("Test samples: " + testData.count());
            
            // Build ML pipeline
            System.out.println("\nBuilding ML pipeline...");
            Pipeline pipeline = buildPipeline();
            
            // Train model
            System.out.println("Training Multinomial Naive Bayes...");
            PipelineModel model = pipeline.fit(trainData);
            
            // Make predictions
            System.out.println("Making predictions...");
            Dataset<Row> predictions = model.transform(testData);
            
            // Evaluate model
            System.out.println("\nEvaluating model...");
            evaluateModel(predictions);
            
            // Test sample reviews
            System.out.println("\n=== Sample Predictions ===");
            testSampleReviews(model, spark);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
        
        System.out.println("\n✅ Analysis completed!");
    }
    
    /**
     * Load data from CSV file
     */
    private static Dataset<Row> loadData(SparkSession spark) throws IOException {
        // Define schema for CSV with sentiment column
        StructType schema = new StructType(new StructField[]{
            DataTypes.createStructField("text", DataTypes.StringType, false),
            DataTypes.createStructField("sentiment", DataTypes.StringType, false)
        });
        
        // Read CSV file
        Dataset<Row> data = spark.read()
            .option("header", "true")
            .option("inferSchema", "false")
            .schema(schema)
            .csv(CSV_FILE);
            
        // Convert sentiment string to label integer (positive=1, negative=0)
        data = data.withColumn("label", 
            when(col("sentiment").equalTo("positive"), 1)
            .otherwise(0)
        ).select("text", "label");
            
        return data;
    }
    
    /**
     * Build ML pipeline with text preprocessing and Multinomial Naive Bayes
     */
    private static Pipeline buildPipeline() {
        // Tokenizer - split text into words
        Tokenizer tokenizer = new Tokenizer()
            .setInputCol("text")
            .setOutputCol("words");
            
        // StopWordsRemover - remove common words
        StopWordsRemover stopWordsRemover = new StopWordsRemover()
            .setInputCol("words")
            .setOutputCol("filtered_words");
            
        // HashingTF - convert words to feature vectors (bag of words)
        HashingTF hashingTF = new HashingTF()
            .setInputCol("filtered_words")
            .setOutputCol("rawFeatures")
            .setNumFeatures(10000); // Vocabulary size
            
        // IDF - calculate inverse document frequency
        IDF idf = new IDF()
            .setInputCol("rawFeatures")
            .setOutputCol("features");
            
        // Multinomial Naive Bayes classifier
        NaiveBayes naiveBayes = new NaiveBayes()
            .setFeaturesCol("features")
            .setLabelCol("label")
            .setModelType("multinomial") // This is the key difference!
            .setSmoothing(1.0); // Laplace smoothing parameter
            
        // Create pipeline
        Pipeline pipeline = new Pipeline()
            .setStages(new PipelineStage[]{
                tokenizer,
                stopWordsRemover,
                hashingTF,
                idf,
                naiveBayes
            });
            
        return pipeline;
    }
    
    /**
     * Evaluate model performance
     */
    private static void evaluateModel(Dataset<Row> predictions) {
        // Calculate accuracy
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
            .setLabelCol("label")
            .setPredictionCol("prediction")
            .setMetricName("accuracy");
            
        double accuracy = evaluator.evaluate(predictions);
        System.out.println("Accuracy: " + String.format("%.3f", accuracy));
        
        // Calculate other metrics
        evaluator.setMetricName("weightedPrecision");
        double precision = evaluator.evaluate(predictions);
        System.out.println("Precision: " + String.format("%.3f", precision));
        
        evaluator.setMetricName("weightedRecall");
        double recall = evaluator.evaluate(predictions);
        System.out.println("Recall: " + String.format("%.3f", recall));
        
        evaluator.setMetricName("f1");
        double f1 = evaluator.evaluate(predictions);
        System.out.println("F1-Score: " + String.format("%.3f", f1));
        
        // Show confusion matrix
        System.out.println("\nConfusion Matrix:");
        predictions.groupBy("label", "prediction").count().show();
    }
    
    /**
     * Test sample reviews
     */
    private static void testSampleReviews(PipelineModel model, SparkSession spark) {
        String[] sampleReviews = {
            "Great food, excellent service!",
            "Terrible food, bad service",
            "Amazing pizza, friendly staff",
            "This place is absolutely horrible",
            "Love this restaurant, will come back"
        };
        
        for (String review : sampleReviews) {
            // Create DataFrame with single review
            Dataset<Row> reviewData = spark.createDataFrame(
                Arrays.asList(RowFactory.create(review, 0)), // dummy label
                new StructType(new StructField[]{
                    DataTypes.createStructField("text", DataTypes.StringType, false),
                    DataTypes.createStructField("label", DataTypes.IntegerType, false)
                })
            );
            
            // Make prediction
            Dataset<Row> prediction = model.transform(reviewData);
            Row result = prediction.select("prediction").collectAsList().get(0);
            int predictedLabel = result.getInt(0);
            
            String sentiment = (predictedLabel == 1) ? "positive" : "negative";
            System.out.println("'" + review + "' -> " + sentiment);
        }
    }
}