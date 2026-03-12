import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Spark imports
import org.apache.spark.SparkConf;
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
    }

    /**
     * Initialize Spark session with optimized configuration
     */
    private static SparkSession initializeSpark() {
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

        return spark;
    }

    /**
     * Load and prepare data from CSV file
     */
    private static Dataset<Row> loadAndPrepareData(SparkSession spark) throws IOException {
        Dataset<Row> data = loadData(spark);
        long totalSamples = data.count();

        // Show class distribution
        data.groupBy("label").count().show();

        return data;
    }

    /**
     * Split data into training and test sets
     */
    private static Dataset<Row>[] splitData(Dataset<Row> data) {
        Dataset<Row>[] splits = data.randomSplit(new double[]{1.0 - TEST_SIZE, TEST_SIZE}, RANDOM_SEED);
        Dataset<Row> trainData = splits[0];
        Dataset<Row> testData = splits[1];

        return splits;
    }

    /**
     * Train the machine learning model with all preprocessing stages
     */
    private static ModelComponents trainModel(Dataset<Row> trainData) {
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
        Dataset<Row> trainTok = tokenizer.transform(trainData);
        Dataset<Row> trainFilt = stopWordsRemover.transform(trainTok);
        Dataset<Row> trainRaw = hashingTF.transform(trainFilt);

        // IDF is an Estimator → fit on TRAIN only
        IDFModel idfModel = idf.fit(trainRaw);
        Dataset<Row> trainFeats = idfModel.transform(trainRaw).cache();

        // Linear SVC is an Estimator → fit on TRAIN features
        long startTime = System.currentTimeMillis();
        LinearSVCModel svcModel = lsvc.fit(trainFeats);

        return new ModelComponents(tokenizer, stopWordsRemover, hashingTF, idfModel, svcModel);
    }

    /**
     * Make predictions on test data using trained models
     */
    private static Dataset<Row> makePredictions(Dataset<Row> testData, ModelComponents models) {
        // Transform test data using the same fitted objects
        Dataset<Row> testTok = models.tokenizer.transform(testData);
        Dataset<Row> testFilt = models.stopWordsRemover.transform(testTok);
        Dataset<Row> testRaw = models.hashingTF.transform(testFilt);
        Dataset<Row> testFeats = models.idfModel.transform(testRaw);

        Dataset<Row> predictions = models.svcModel.transform(testFeats);

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
            org.apache.spark.sql.functions.when(org.apache.spark.sql.functions.col("sentiment").equalTo("positive"), 1)
            .otherwise(0)
        ).select("text", "label");

        return data;
    }


    /**
     * Evaluate model performance with clean, student-friendly output
     */
    private static void evaluateModel(Dataset<Row> predictions) {
        // Calculate metrics
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
            .setLabelCol("label")
            .setPredictionCol("prediction");

        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
        double precision = evaluator.setMetricName("weightedPrecision").evaluate(predictions);
        double recall = evaluator.setMetricName("weightedRecall").evaluate(predictions);
        double f1 = evaluator.setMetricName("f1").evaluate(predictions);

        // Get confusion matrix data
        long[][] confusionMatrix = getConfusionMatrix(predictions);
        long trueNegatives = confusionMatrix[0][0];
        long falsePositives = confusionMatrix[0][1];
        long falseNegatives = confusionMatrix[1][0];
        long truePositives = confusionMatrix[1][1];

        double sensitivity = (double) truePositives / (truePositives + falseNegatives);
        double specificity = (double) trueNegatives / (trueNegatives + falsePositives);
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
            if (expected.equals(predicted)) correct++;
        }
    }
}
