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
import org.apache.spark.ml.classification.LinearSVC;
import org.apache.spark.ml.classification.LinearSVCModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.RowFactory;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.when;

/**
 * Yelp Sentiment Analysis using Apache Spark MLlib
 * Implements Linear SVC (hinge loss) for text classification
 */
public class YelpSentimentAnalysisSpark {
    
    /**
     * Container class to hold all model components for easy passing between methods
     */
    private static class ModelComponents {
        public final Tokenizer tokenizer;
        public final StopWordsRemover stopWordsRemover;
        public final HashingTF hashingTF;
        public final IDFModel idfModel;
        public final LinearSVCModel svcModel;
        
        public ModelComponents(Tokenizer tokenizer, StopWordsRemover stopWordsRemover, 
                             HashingTF hashingTF, IDFModel idfModel, LinearSVCModel svcModel) {
            this.tokenizer = tokenizer;
            this.stopWordsRemover = stopWordsRemover;
            this.hashingTF = hashingTF;
            this.idfModel = idfModel;
            this.svcModel = svcModel;
        }
    }
    
    private static final String CSV_FILE = "simple_yelp_reviews.csv";
    private static final double TEST_SIZE = 0.2;
    private static final long RANDOM_SEED = 42L;
    
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🤖 YELP SENTIMENT ANALYSIS WITH APACHE SPARK MLlib");
        System.out.println("   Linear SVC Text Classification");
        System.out.println("=".repeat(70));
        
        // Initialize Spark
        SparkSession spark = initializeSpark();
        
        try {
            // Step 1: Load and prepare data
            Dataset<Row> data = loadAndPrepareData(spark);
            
            // Step 2: Split data into train/test
            Dataset<Row>[] splits = splitData(data);
            Dataset<Row> trainData = splits[0];
            Dataset<Row> testData = splits[1];
            
            // Step 3: Train the model
            ModelComponents model = trainModel(trainData);
            
            // Step 4: Make predictions on test data
            Dataset<Row> predictions = makePredictions(testData, model);
            
            // Step 5: Evaluate the model
            evaluateModel(predictions);
            
            // Step 6: Test on sample reviews
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
     * Initialize Spark session with optimized configuration
     */
    private static SparkSession initializeSpark() {
        System.out.println("\n🔧 INITIALIZING SPARK");
        System.out.println("-".repeat(40));
        
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
        
        System.out.println("✅ Spark initialized successfully");
        return spark;
    }
    
    /**
     * Load and prepare data from CSV file
     */
    private static Dataset<Row> loadAndPrepareData(SparkSession spark) throws IOException {
        System.out.println("\n📊 DATA LOADING & PREPARATION");
        System.out.println("-".repeat(40));
        System.out.println("Loading dataset from: " + CSV_FILE);
        
        Dataset<Row> data = loadData(spark);
        long totalSamples = data.count();
        System.out.println("✅ Loaded " + totalSamples + " samples");
        
        // Show class distribution
        System.out.println("\n📈 CLASS DISTRIBUTION:");
        data.groupBy("label").count().show();
        
        return data;
    }
    
    /**
     * Split data into training and test sets
     */
    private static Dataset<Row>[] splitData(Dataset<Row> data) {
        System.out.println("\n✂️  DATA SPLITTING");
        System.out.println("-".repeat(40));
        
        Dataset<Row>[] splits = data.randomSplit(new double[]{1.0 - TEST_SIZE, TEST_SIZE}, RANDOM_SEED);
        Dataset<Row> trainData = splits[0];
        Dataset<Row> testData = splits[1];
        
        long totalSamples = data.count();
        long trainCount = trainData.count();
        long testCount = testData.count();
        
        System.out.printf("Training samples: %d (%.1f%%)\\n", trainCount, (double)trainCount/totalSamples*100);
        System.out.printf("Test samples:    %d (%.1f%%)\\n", testCount, (double)testCount/totalSamples*100);
        
        return splits;
    }
    
    /**
     * Train the machine learning model with all preprocessing stages
     */
    private static ModelComponents trainModel(Dataset<Row> trainData) {
        System.out.println("\n🔧 PROCEDURAL ML STAGES");
        System.out.println("-".repeat(40));
        System.out.println("Building stages with:");
        System.out.println("  • Tokenizer (text → words) - Transformer");
        System.out.println("  • StopWordsRemover (remove common words) - Transformer");
        System.out.println("  • HashingTF (words → features) - Transformer");
        System.out.println("  • IDF (inverse document frequency) - Estimator");
        System.out.println("  • Linear SVC (classifier) - Estimator");
        
        // Define all stages
        Tokenizer tokenizer = new Tokenizer()
            .setInputCol("text")
            .setOutputCol("words");

        StopWordsRemover stopWordsRemover = new StopWordsRemover()
            .setInputCol("words")
            .setOutputCol("filtered_words");

        HashingTF hashingTF = new HashingTF()
            .setInputCol("filtered_words")
            .setOutputCol("rawFeatures")
            .setNumFeatures(10000);

        IDF idf = new IDF()
            .setInputCol("rawFeatures")
            .setOutputCol("features");

        LinearSVC lsvc = new LinearSVC()
            .setFeaturesCol("features")
            .setLabelCol("label")
            .setMaxIter(50)
            .setRegParam(0.1);

        // Process training data through stages
        System.out.println("\n🚀 MODEL TRAINING");
        System.out.println("-".repeat(40));
        System.out.println("Processing training data through stages...");
        
        Dataset<Row> trainTok = tokenizer.transform(trainData);
        Dataset<Row> trainFilt = stopWordsRemover.transform(trainTok);
        Dataset<Row> trainRaw = hashingTF.transform(trainFilt);

        // IDF is an Estimator → fit on TRAIN only
        System.out.println("Fitting IDF model on training data...");
        IDFModel idfModel = idf.fit(trainRaw);
        Dataset<Row> trainFeats = idfModel.transform(trainRaw).cache();

        // Linear SVC is an Estimator → fit on TRAIN features
        System.out.println("Training Linear SVC classifier...");
        long startTime = System.currentTimeMillis();
        LinearSVCModel svcModel = lsvc.fit(trainFeats);
        long trainingTime = System.currentTimeMillis() - startTime;
        System.out.printf("✅ Training completed in %.2f seconds\\n", trainingTime / 1000.0);
        
        return new ModelComponents(tokenizer, stopWordsRemover, hashingTF, idfModel, svcModel);
    }
    
    /**
     * Make predictions on test data using trained models
     */
    private static Dataset<Row> makePredictions(Dataset<Row> testData, ModelComponents models) {
        System.out.println("\n🔮 MAKING PREDICTIONS");
        System.out.println("-".repeat(40));
        System.out.println("Processing test data through stages...");
        
        // Transform test data using the same fitted objects
        Dataset<Row> testTok = models.tokenizer.transform(testData);
        Dataset<Row> testFilt = models.stopWordsRemover.transform(testTok);
        Dataset<Row> testRaw = models.hashingTF.transform(testFilt);
        Dataset<Row> testFeats = models.idfModel.transform(testRaw);

        long predStartTime = System.currentTimeMillis();
        Dataset<Row> predictions = models.svcModel.transform(testFeats);
        long predTime = System.currentTimeMillis() - predStartTime;
        System.out.printf("✅ Predictions completed in %.2f seconds\\n", predTime / 1000.0);
        
        return predictions;
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
     * Test sample reviews with clean output using procedural approach
     */
    private static void testSampleReviews(ModelComponents models, SparkSession spark) {
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
            
            // Make prediction using procedural approach
            Dataset<Row> reviewTok = models.tokenizer.transform(reviewData);
            Dataset<Row> reviewFilt = models.stopWordsRemover.transform(reviewTok);
            Dataset<Row> reviewRaw = models.hashingTF.transform(reviewFilt);
            Dataset<Row> reviewFeats = models.idfModel.transform(reviewRaw);
            Dataset<Row> prediction = models.svcModel.transform(reviewFeats);
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