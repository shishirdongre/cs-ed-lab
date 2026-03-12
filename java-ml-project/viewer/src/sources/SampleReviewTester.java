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

    public void run(PipelineModel model, SparkSession spark) {
        for (int i = 0; i < SAMPLE_REVIEWS.length; i++) {
            String review = SAMPLE_REVIEWS[i][0];
            String expected = SAMPLE_REVIEWS[i][1];

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

            String predicted = (predictedLabel == 1) ? "positive" : "negative";
        }
    }
}
