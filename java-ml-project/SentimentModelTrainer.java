import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.classification.LinearSVC;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Trains the sentiment classification pipeline: tokenizer, stop words, TF-IDF, Linear SVC.
 * Returns a PipelineModel that can be saved and loaded for inference.
 */
public class SentimentModelTrainer {

    private static final int NUM_FEATURES = 10000;
    private static final int MAX_ITER = 50;
    private static final double REG_PARAM = 0.1;

    /**
     * Build and fit pipeline on training data; return fitted PipelineModel.
     */
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
