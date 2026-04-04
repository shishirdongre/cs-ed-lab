package com.example.ml;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LinearSVC;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.StopWordsRemover;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Fits the text pipeline (tokenize → TF-IDF → linear SVM) on training data.
 */
public class SentimentModelTrainer {

    private static final int NUM_FEATURES = 10000;
    private static final int MAX_ITER = 50;
    private static final double REG_PARAM = 0.1;

    /**
     * Chains stages in order; {@code fit} learns tokenizer/IDF/SVM weights from {@code trainData}.
     */
    public PipelineModel train(Dataset<Row> trainData) {
        // Stage 1: split review text into a sequence of tokens (words) for downstream feature stages.
        Tokenizer tokenizer = new Tokenizer()
                .setInputCol("text")
                .setOutputCol("words");

        // Stage 2: drop very common words (e.g. "the", "a") that carry little sentiment signal.
        StopWordsRemover stopWordsRemover = new StopWordsRemover()
                .setInputCol("words")
                .setOutputCol("filtered_words");

        // Stage 3: map each review's word list to a fixed-size numeric vector (term frequencies via hashing).
        HashingTF hashingTF = new HashingTF()
                .setInputCol("filtered_words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(NUM_FEATURES);

        // Stage 4: rescale raw term frequencies by inverse document frequency (emphasize distinctive words).
        IDF idf = new IDF()
                .setInputCol("rawFeatures")
                .setOutputCol("features");

        // Stage 5: linear classifier that learns weights on the TF-IDF feature vector to predict label.
        LinearSVC lsvc = new LinearSVC()
                .setFeaturesCol("features")
                .setLabelCol("label")
                .setMaxIter(MAX_ITER)
                .setRegParam(REG_PARAM);

        // Chain stages in order; fit(trainData) fits every estimator (e.g. IDF + LinearSVC) on training rows only.
        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{tokenizer, stopWordsRemover, hashingTF, idf, lsvc});

        return pipeline.fit(trainData);
    }
}
