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
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🤖 YELP SENTIMENT ANALYSIS WITH APACHE SPARK MLlib");
        System.out.println("   Multinomial Naive Bayes Text Classification");
        System.out.println("=".repeat(70));
        
        // Initialize Spark
        SparkConf conf = new SparkConf()
            .setAppName("YelpSentimentAnalysis")
            .setMaster("local[*]")
            .set("spark.sql.adaptive.enabled", "false")
            .set("spark.sql.adaptive.coalescePartitions.enabled", "false")
            .set("spark.ui.showConsoleProgress", "false")
            .set("spark.sql.adaptive.skewJoin.enabled", "false");
            
        SparkSession spark = SparkSession.builder()
            .config(conf)
            .getOrCreate();
            
        // Suppress Spark logs
        spark.sparkContext().setLogLevel("WARN");
            
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
        
        try {
            // Load and prepare data
            System.out.println("\n📊 DATA LOADING & PREPARATION");
            System.out.println("-".repeat(40));
            System.out.println("Loading dataset from: " + CSV_FILE);
            
            Dataset<Row> data = loadData(spark);
            long totalSamples = data.count();
            System.out.println("✅ Loaded " + totalSamples + " samples");
            
            // Show class distribution
            System.out.println("\n📈 CLASS DISTRIBUTION:");
            data.groupBy("label").count().show();
            
            // Split data
            System.out.println("\n✂️  DATA SPLITTING");
            System.out.println("-".repeat(40));
            Dataset<Row>[] splits = data.randomSplit(new double[]{1.0 - TEST_SIZE, TEST_SIZE}, RANDOM_SEED);
            Dataset<Row> trainData = splits[0];
            Dataset<Row> testData = splits[1];
            
            long trainCount = trainData.count();
            long testCount = testData.count();
            System.out.printf("Training samples: %d (%.1f%%)\n", trainCount, (double)trainCount/totalSamples*100);
            System.out.printf("Test samples:    %d (%.1f%%)\n", testCount, (double)testCount/totalSamples*100);
            
            // Build ML pipeline
            System.out.println("\n🔧 MACHINE LEARNING PIPELINE");
            System.out.println("-".repeat(40));
            System.out.println("Building pipeline with:");
            System.out.println("  • Tokenizer (text → words)");
            System.out.println("  • StopWordsRemover (remove common words)");
            System.out.println("  • HashingTF (words → features)");
            System.out.println("  • IDF (inverse document frequency)");
            System.out.println("  • Multinomial Naive Bayes (classifier)");
            
            Pipeline pipeline = buildPipeline();
            
            // Train model
            System.out.println("\n🚀 MODEL TRAINING");
            System.out.println("-".repeat(40));
            System.out.println("Training Multinomial Naive Bayes classifier...");
            long startTime = System.currentTimeMillis();
            PipelineModel model = pipeline.fit(trainData);
            long trainingTime = System.currentTimeMillis() - startTime;
            System.out.printf("✅ Training completed in %.2f seconds\n", trainingTime / 1000.0);
            
            // Make predictions
            System.out.println("\n🔮 MAKING PREDICTIONS");
            System.out.println("-".repeat(40));
            System.out.println("Running predictions on test set...");
            long predStartTime = System.currentTimeMillis();
            Dataset<Row> predictions = model.transform(testData);
            long predTime = System.currentTimeMillis() - predStartTime;
            System.out.printf("✅ Predictions completed in %.2f seconds\n", predTime / 1000.0);
            
            // Evaluate model
            evaluateModel(predictions);
            
            // Test sample reviews
            System.out.println("\n🧪 SAMPLE PREDICTIONS");
            System.out.println("-".repeat(40));
            testSampleReviews(model, spark);
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🎉 ANALYSIS COMPLETED SUCCESSFULLY!");
        System.out.println("=".repeat(70));
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
     * Evaluate model performance with clean, student-friendly output
     */
    private static void evaluateModel(Dataset<Row> predictions) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 MODEL EVALUATION RESULTS");
        System.out.println("=".repeat(60));
        
        // Calculate metrics
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
            .setLabelCol("label")
            .setPredictionCol("prediction");
            
        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
        double precision = evaluator.setMetricName("weightedPrecision").evaluate(predictions);
        double recall = evaluator.setMetricName("weightedRecall").evaluate(predictions);
        double f1 = evaluator.setMetricName("f1").evaluate(predictions);
        
        // Display metrics in a clean format
        System.out.println("\n🎯 CLASSIFICATION METRICS:");
        System.out.println("┌─────────────────┬─────────────┬─────────────┐");
        System.out.println("│ Metric          │ Value       │ Percentage  │");
        System.out.println("├─────────────────┼─────────────┼─────────────┤");
        System.out.printf("│ Accuracy        │ %-11.4f │ %-11.2f%% │\n", accuracy, accuracy * 100);
        System.out.printf("│ Precision       │ %-11.4f │ %-11.2f%% │\n", precision, precision * 100);
        System.out.printf("│ Recall          │ %-11.4f │ %-11.2f%% │\n", recall, recall * 100);
        System.out.printf("│ F1-Score        │ %-11.4f │ %-11.2f%% │\n", f1, f1 * 100);
        System.out.println("└─────────────────┴─────────────┴─────────────┘");
        
        // Performance interpretation
        System.out.println("\n📈 PERFORMANCE INTERPRETATION:");
        if (accuracy >= 0.8) {
            System.out.println("   🟢 EXCELLENT: Model performs very well!");
        } else if (accuracy >= 0.7) {
            System.out.println("   🟡 GOOD: Model performs reasonably well");
        } else if (accuracy >= 0.6) {
            System.out.println("   🟠 FAIR: Model needs improvement");
        } else {
            System.out.println("   🔴 POOR: Model needs significant improvement");
        }
        
        // Confusion Matrix
        System.out.println("\n🔢 CONFUSION MATRIX:");
        System.out.println("   (Rows = Actual, Columns = Predicted)");
        System.out.println("   ┌─────────────┬─────────────┬─────────────┐");
        System.out.println("   │             │ Negative    │ Positive    │");
        System.out.println("   ├─────────────┼─────────────┼─────────────┤");
        
        // Get confusion matrix data
        long[][] confusionMatrix = getConfusionMatrix(predictions);
        System.out.printf("   │ Negative     │ %-11d │ %-11d │\n", confusionMatrix[0][0], confusionMatrix[0][1]);
        System.out.printf("   │ Positive     │ %-11d │ %-11d │\n", confusionMatrix[1][0], confusionMatrix[1][1]);
        System.out.println("   └─────────────┴─────────────┴─────────────┘");
        
        // Calculate additional metrics from confusion matrix
        long trueNegatives = confusionMatrix[0][0];
        long falsePositives = confusionMatrix[0][1];
        long falseNegatives = confusionMatrix[1][0];
        long truePositives = confusionMatrix[1][1];
        
        double sensitivity = (double) truePositives / (truePositives + falseNegatives);
        double specificity = (double) trueNegatives / (trueNegatives + falsePositives);
        
        System.out.println("\n📋 DETAILED METRICS:");
        System.out.printf("   • True Positives:  %d\n", truePositives);
        System.out.printf("   • True Negatives:  %d\n", trueNegatives);
        System.out.printf("   • False Positives: %d\n", falsePositives);
        System.out.printf("   • False Negatives: %d\n", falseNegatives);
        System.out.printf("   • Sensitivity:     %.4f (%.2f%%)\n", sensitivity, sensitivity * 100);
        System.out.printf("   • Specificity:     %.4f (%.2f%%)\n", specificity, specificity * 100);
        
        System.out.println("\n" + "=".repeat(60));
    }
    
    /**
     * Calculate confusion matrix from predictions
     */
    private static long[][] getConfusionMatrix(Dataset<Row> predictions) {
        long[][] matrix = new long[2][2];
        
        List<Row> results = predictions.select("label", "prediction").collectAsList();
        for (Row row : results) {
            // Handle both Integer and Double types from Spark
            int actual, predicted;
            
            // Get actual label
            if (row.get(0) instanceof Integer) {
                actual = row.getInt(0);
            } else {
                actual = ((Double) row.get(0)).intValue();
            }
            
            // Get predicted label
            if (row.get(1) instanceof Integer) {
                predicted = row.getInt(1);
            } else {
                predicted = ((Double) row.get(1)).intValue();
            }
            
            matrix[actual][predicted]++;
        }
        
        return matrix;
    }
    
    /**
     * Test sample reviews with clean output
     */
    private static void testSampleReviews(PipelineModel model, SparkSession spark) {
        String[][] sampleReviews = {
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
        
        System.out.println("Testing model on sample reviews:\n");
        System.out.println("┌─────┬─────────────────────────────────────┬──────────┬──────────┐");
        System.out.println("│ #   │ Review Text                         │ Expected │ Predicted│");
        System.out.println("├─────┼─────────────────────────────────────┼──────────┼──────────┤");
        
        int correct = 0;
        for (int i = 0; i < sampleReviews.length; i++) {
            String review = sampleReviews[i][0];
            String expected = sampleReviews[i][1];
            
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
            
            // Handle both Integer and Double types from Spark
            int predictedLabel;
            if (result.get(0) instanceof Integer) {
                predictedLabel = result.getInt(0);
            } else {
                predictedLabel = ((Double) result.get(0)).intValue();
            }
            
            String predicted = (predictedLabel == 1) ? "positive" : "negative";
            String status = expected.equals(predicted) ? "✅" : "❌";
            if (expected.equals(predicted)) correct++;
            
            // Truncate long reviews for table display
            String displayReview = review.length() > 35 ? review.substring(0, 32) + "..." : review;
            System.out.printf("│ %-3d │ %-35s │ %-8s │ %-8s │ %s\n", 
                i+1, displayReview, expected, predicted, status);
        }
        
        System.out.println("└─────┴─────────────────────────────────────┴──────────┴──────────┘");
        System.out.printf("\n📊 Sample Accuracy: %d/%d (%.1f%%)\n", 
            correct, sampleReviews.length, (double)correct/sampleReviews.length*100);
    }
}