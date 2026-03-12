public class SentimentModelTrainer {

    private static final int NUM_FEATURES = 10000;
    private static final int MAX_ITER = 50;
    private static final double REG_PARAM = 0.1;

    public PipelineModel train(Dataset<Row> trainData) {
        Tokenizer tokenizer = new Tokenizer()
                .setInputCol("text")
                .setOutputCol("words");

        StopWordsRemover stopWordsRemover = new StopWordsRemover()
                .setInputCol("words")
                .setOutputCol("filtered_words");

        HashingTF hashingTF = new HashingTF()
                .setInputCol("filtered_words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(NUM_FEATURES);

        IDF idf = new IDF()
                .setInputCol("rawFeatures")
                .setOutputCol("features");

        LinearSVC lsvc = new LinearSVC()
                .setFeaturesCol("features")
                .setLabelCol("label")
                .setMaxIter(MAX_ITER)
                .setRegParam(REG_PARAM);

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{tokenizer, stopWordsRemover, hashingTF, idf, lsvc});

        return pipeline.fit(trainData);
    }
}
