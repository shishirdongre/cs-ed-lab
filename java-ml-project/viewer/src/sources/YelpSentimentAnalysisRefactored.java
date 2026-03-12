public class YelpSentimentAnalysisRefactored {

    private static final String CSV_FILE = "simple_yelp_reviews.csv";
    private static final double TEST_SIZE = 0.2;
    private static final long RANDOM_SEED = 42L;
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

            model.write().overwrite().save(MODEL_PATH);

            SentimentPredictor predictor = new SentimentPredictor();
            Dataset<Row> predictions = predictor.predict(testData, model);

            ModelEvaluator evaluator = new ModelEvaluator();
            evaluator.evaluate(predictions);

            SampleReviewTester sampleTester = new SampleReviewTester();
            sampleTester.run(model, spark);

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }
}
